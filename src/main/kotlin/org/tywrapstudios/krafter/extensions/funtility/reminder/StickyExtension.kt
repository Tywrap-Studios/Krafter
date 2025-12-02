package org.tywrapstudios.krafter.extensions.funtility.reminder

import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.checks.isNotBot
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.components.forms.ModalForm
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.i18n.types.Key
import dev.kordex.core.utils.FilterStrategy
import dev.kordex.core.utils.suggestStringMap
import org.tywrapstudios.krafter.database.transactors.StickyTransactor
import org.tywrapstudios.krafter.i18n.Translations

class StickyExtension : Extension() {
	override val name: String = "krafter.sticky"
	val stickies = StickyTransactor

	override suspend fun setup() {
		event<MessageCreateEvent> {
			check { isNotBot() }

			action {
				val channelStickies = stickies.getAll(event.message.channelId)
				for ((tag, lastMessage, text) in channelStickies) {
					val channel = event.message.channel
					channel.deleteMessage(lastMessage)
					val message = channel.createMessage("# \uD83D\uDCCC\n${text}")
					stickies.update(tag, message.id)
				}
			}
		}

		ephemeralSlashCommand {
			name = Translations.Commands.sticky
			description = Translations.Commands.Sticky.description

			ephemeralSubCommand(::StickyArguments, ::StickyForm) {
				name = Translations.Commands.Sticky.add
				description = Translations.Commands.Sticky.Add.description

				action { modal ->
					if (modal?.text?.value == null) {
						respond {
							content = "Your text is invalid or null. Please try again."
						}
						return@action
					}
					val message = channel.createMessage("# \uD83D\uDCCC\n${modal.text.value!!}")
					stickies.set(arguments.tag, channel.id, message.id, modal.text.value!!)
					respond {
						content = "Sticky successfully set"
					}
				}
			}

			ephemeralSubCommand(::StickyArguments) {
				name = Translations.Commands.Sticky.remove
				description = Translations.Commands.Sticky.Remove.description

				action {
					stickies.remove(arguments.tag)
					respond {
						content = "Sticky successfully removed"
					}
				}
			}
		}
	}

	class StickyArguments : Arguments() {
		val tag by string {
			name = Translations.Args.Sticky.tag
			description = Translations.Args.Sticky.Tag.description

			autoComplete {
				val tags = StickyTransactor.getTags()

				suggestStringMap(
					tags.associateWith { it },
					FilterStrategy.Contains
				)
			}
		}
	}

	class StickyForm : ModalForm() {
		override var title: Key = Translations.Modals.Sticky.title

		val text = paragraphText {
			label = Translations.Modals.Sticky.Text.label
			placeholder = Translations.Modals.Sticky.Text.placeholder
			required = true
		}
	}
}
