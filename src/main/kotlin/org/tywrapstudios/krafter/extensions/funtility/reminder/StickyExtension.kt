package org.tywrapstudios.krafter.extensions.funtility.reminder

import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.checks.isNotBot
import dev.kordex.core.commands.Arguments
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
					val message = channel.createMessage(text)
					stickies.set(tag, event.message.channelId, message.id, text)
				}
			}
		}

		ephemeralSlashCommand(::StickyArguments, ::StickyForm) {
//			name = TODO Translations.Commands.sticky
//			description = TODO Translations.Commands.Sticky.description

			action { modal ->
				if (modal?.text?.value == null) {
					respond {
						content = "Your text is invalid or null. Please try again."
					}
					return@action
				}
				val message = channel.createMessage(modal.text.value!!)
				stickies.set(arguments.tag, channel.id, message.id, modal.text.value!!)
			}
		}
	}

	class StickyArguments : Arguments() {
		val tag by string {
			name = TODO() // Translations.Args.Sticky.tag
			description = TODO() // Translations.Args.Sticky.Tag.description

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
		override var title: Key = TODO() // Translations.Modals.Sticky.title

		val text = paragraphText {
			label = TODO() // Translations.Modals.Sticky.Text.label
			placeholder = TODO() // Translations.Modals.Sticky.Text.placeholder
			required = true
		}
	}
}
