package org.tywrapstudios.krafter.extensions.tag

import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalColor
import dev.kordex.core.commands.converters.impl.optionalGuild
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.components.forms.ModalForm
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.i18n.toKey
import dev.kordex.core.i18n.types.Key
import dev.kordex.core.utils.FilterStrategy
import dev.kordex.core.utils.respond
import dev.kordex.core.utils.suggestStringMap
import dev.kordex.modules.dev.unsafe.annotations.UnsafeAPI
import dev.kordex.modules.dev.unsafe.commands.slash.InitialSlashCommandResponse
import dev.kordex.modules.dev.unsafe.extensions.unsafeSubCommand
import dev.kordex.modules.func.tags.config.TagsConfig
import dev.kordex.modules.func.tags.data.TagsData
import dev.kordex.modules.func.tags.i18n.generated.TagsTranslations
import dev.kordex.modules.func.tags.nullIfBlank
import org.koin.core.component.inject
import org.tywrapstudios.krafter.NEGATIVE_EMOTE
import org.tywrapstudios.krafter.POSITIVE_EMOTE
import org.tywrapstudios.krafter.database.entities.TriggerTag
import org.tywrapstudios.krafter.database.entities.toKordExTag
import org.tywrapstudios.krafter.database.entities.toTriggerTag
import org.tywrapstudios.krafter.extensions.data.KrafterTagsData
import org.tywrapstudios.krafter.i18n.Translations
import kotlin.getValue

class AutoTagExtension : Extension() {
	override val name: String = "krafter.autoTag"

	val tags = KrafterTagsData
	val tagsConfig: TagsConfig by inject()

	@OptIn(UnsafeAPI::class)
	override suspend fun setup() {

		event<MessageCreateEvent> {
			action {
				val message = event.message.asMessage()
				val tags = tags.getTagsByTrigger(message.content, message.getGuild().id)

				for (tag in tags) {
					message.respond {
						tagsConfig.getTagFormatter()
							.invoke(this, tag)
					}
				}
			}
		}

		ephemeralSlashCommand {
			name = TagsTranslations.Command.ManageTags.name
			description = TagsTranslations.Command.ManageTags.description

			allowInDms = false

			check {
				anyGuild()
			}

			tagsConfig.getStaffCommandChecks().forEach(::check)

			@OptIn(KordUnsafe::class)
			unsafeSubCommand(::SetArgs) {
				name = TagsTranslations.Command.ManageTags.Set.name
				description = TagsTranslations.Command.ManageTags.Set.description

				initialResponse = InitialSlashCommandResponse.None

				action {
					val modalObj = AutoTagEditModal()

					this@unsafeSubCommand.componentRegistry.register(modalObj)

					event.interaction.modal(
						modalObj.translateTitle(getLocale()),
						modalObj.id
					) {
						modalObj.applyToBuilder(this, getLocale())
					}

					interactionResponse = modalObj.awaitCompletion {
						it?.deferEphemeralResponseUnsafe()
					} ?: return@action

					val tag = TriggerTag(
						category = arguments.category,
						description = modalObj.description.value!!,
						key = arguments.key,
						title = modalObj.tagTitle.value!!,
						color = arguments.colour,
						guildId = arguments.guild?.id,
						image = modalObj.imageUrl.value.nullIfBlank(),
						trigger = arguments.trigger,
					)

					tags.setTag(tag)

					tagsConfig.getLoggingChannel(guild!!.asGuild()).createMessage {
//						allowedMentions { }

						content = TagsTranslations.Logging.tagSet
							.withLocale(getLocale())
							.translateNamed(
								"user" to user.mention
							)

						tagsConfig.getTagFormatter().invoke(this, tag.toKordExTag())
					}

					respondEphemeral {
						TagsTranslations.Response.Tag.set
							.withLocale(getLocale())
							.translateNamed(
								"emote" to POSITIVE_EMOTE,
								"tag" to tag.title
							)
					}
				}
			}

			@OptIn(KordUnsafe::class)
			unsafeSubCommand(::EditArgs) {
				name = TagsTranslations.Command.ManageTags.Edit.name
				description = TagsTranslations.Command.ManageTags.Edit.description
				initialResponse = InitialSlashCommandResponse.None

				action {
					var tag = tags.getTagByKey(arguments.key, arguments.guild?.id)?.toTriggerTag()

					if (tag == null) {
						ackEphemeral {
							content = TagsTranslations.Response.Tag.noneFound
								.withLocale(getLocale())
								.translateNamed("emote" to NEGATIVE_EMOTE)
						}

						return@action
					}

					val modalObj = AutoTagEditModal(
						true,
						tag.key,
						tag.title,
						tag.description,
						tag.image
					)

					this@unsafeSubCommand.componentRegistry.register(modalObj)

					event.interaction.modal(
						modalObj.translateTitle(getLocale()),
						modalObj.id
					) {
						modalObj.applyToBuilder(this, getLocale())
					}

					interactionResponse = modalObj.awaitCompletion {
						it?.deferEphemeralResponseUnsafe()
					} ?: return@action

					if (!modalObj.tagTitle.value.isNullOrBlank()) {
						tag = tag.copy(title = modalObj.tagTitle.value!!)
					}

					if (!modalObj.description.value.isNullOrBlank()) {
						tag = tag.copy(description = modalObj.description.value!!)
					}

					if (arguments.category != null) {
						tag = tag.copy(category = arguments.category!!)
					}

					if (arguments.colour != null) {
						tag = tag.copy(color = arguments.colour!!)
					}

					tag = if (modalObj.imageUrl.value.isNullOrBlank()) {
						tag.copy(image = null)
					} else {
						tag.copy(image = modalObj.imageUrl.value)
					}

					tag = tag.copy(
						trigger = arguments.trigger,
					)

					tags.setTriggerTag(tag)

					tagsConfig.getLoggingChannel(guild!!.asGuild()).createMessage {
//						allowedMentions { }

						content = TagsTranslations.Logging.tagEdited
							.withLocale(getLocale())
							.translateNamed(
								"user" to user.mention
							)

						tagsConfig.getTagFormatter().invoke(this, tag.toKordExTag())
					}

					respondEphemeral {
						content = TagsTranslations.Response.Tag.edited
							.withLocale(getLocale())
							.translateNamed(
								"emote" to POSITIVE_EMOTE,
								"tag" to tag.title
							)
					}
				}
			}
		}
	}

	internal class AutoTagEditModal(
		isEditing: Boolean = false,
		key: String? = null,
		private val initialTagTitle: String? = null,
		private val initialDescription: String? = null,
		private val initialImageUrl: String? = null,
	) : ModalForm() {
		override var title: Key = when {
			!isEditing && key != null ->
				TagsTranslations.Modal.Title.createWithTag
					.withNamedPlaceholders("tag" to key)

			!isEditing && key == null ->
				TagsTranslations.Modal.Title.create

			isEditing && key != null ->
				TagsTranslations.Modal.Title.editWithTag
					.withNamedPlaceholders("tag" to key)

			isEditing && key == null ->
				TagsTranslations.Modal.Title.edit

			else -> error("Should be unreachable!")
		}

		val tagTitle = lineText {
			label = TagsTranslations.Modal.Input.title

			initialValue = initialTagTitle?.toKey()
			translateInitialValue = false
		}

		val description = paragraphText {
			label = TagsTranslations.Modal.Input.content

			initialValue = initialDescription?.toKey()
			translateInitialValue = false
		}

		val imageUrl = lineText {
			label = TagsTranslations.Modal.Input.imageUrl

			initialValue = initialImageUrl?.toKey()

			translateInitialValue = false
			required = false
		}
	}

	private fun SetArgs(): SetArgs =
		SetArgs(tags)

	private fun EditArgs(): EditArgs =
		EditArgs(tags)

	internal class SetArgs(tagsData: TagsData) : Arguments() {
		val key by string {
			name = TagsTranslations.Arguments.Set.Key.name
			description = TagsTranslations.Arguments.Set.Key.description
		}

		val category by string {
			name = TagsTranslations.Arguments.Set.Category.name
			description = TagsTranslations.Arguments.Set.Category.description

			autoComplete {
				val categories = tagsData.getAllCategories(data.guildId.value)

				suggestStringMap(
					categories.associateWith { it },
					FilterStrategy.Contains
				)
			}
		}

		val trigger by string {
			name = Translations.GeneralArgs.ManageTags.trigger
			description = Translations.GeneralArgs.ManageTags.Trigger.description
		}

		val colour by optionalColor {
			name = TagsTranslations.Arguments.Set.Colour.name
			description = TagsTranslations.Arguments.Set.Colour.description
		}

		val guild by optionalGuild {
			name = TagsTranslations.Arguments.Set.Server.name
			description = TagsTranslations.Arguments.Set.Server.description
		}
	}

	internal class EditArgs(tagsData: TagsData) : Arguments() {
		val key by string {
			name = TagsTranslations.Arguments.Edit.Key.name
			description = TagsTranslations.Arguments.Edit.Key.description
		}

		val trigger by string {
			name = Translations.GeneralArgs.ManageTags.trigger
			description = Translations.GeneralArgs.ManageTags.Trigger.description
		}

		val guild by optionalGuild {
			name = TagsTranslations.Arguments.Edit.Server.name
			description = TagsTranslations.Arguments.Edit.Server.description
		}

		val category by optionalString {
			name = TagsTranslations.Arguments.Set.Category.name
			description = TagsTranslations.Arguments.Set.Category.description

			autoComplete {
				val categories = tagsData.getAllCategories(data.guildId.value)

				suggestStringMap(
					categories.associateWith { it },
					FilterStrategy.Contains
				)
			}
		}

		val colour by optionalColor {
			name = TagsTranslations.Arguments.Set.Colour.name
			description = TagsTranslations.Arguments.Set.Colour.description
		}
	}
}
