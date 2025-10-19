/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.quiltmc.community.cozy.modules.ama

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.asChannelOfOrNull
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.MessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.event.interaction.GuildButtonInteractionCreateEvent
import dev.kord.rest.builder.message.embed
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.checks.guildFor
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.channel
import dev.kordex.core.commands.converters.impl.optionalChannel
import dev.kordex.core.components.ComponentRegistry
import dev.kordex.core.components.components
import dev.kordex.core.components.ephemeralButton
import dev.kordex.core.components.forms.ModalForm
import dev.kordex.core.components.forms.widgets.LineTextWidget
import dev.kordex.core.components.forms.widgets.ParagraphTextWidget
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.i18n.types.Key
import dev.kordex.modules.dev.unsafe.annotations.UnsafeAPI
import dev.kordex.modules.dev.unsafe.commands.slash.InitialSlashCommandResponse
import dev.kordex.modules.dev.unsafe.extensions.unsafeSlashCommand
import org.koin.core.component.inject
import org.quiltmc.community.cozy.modules.ama.data.AmaConfig
import org.quiltmc.community.cozy.modules.ama.data.AmaData
import org.quiltmc.community.cozy.modules.ama.data.AmaEmbedConfig
import org.quiltmc.community.cozy.modules.ama.enums.QuestionStatusFlag
import org.quiltmc.community.cozy.modules.ama.i18n.Translations

public class AmaExtension : Extension() {
	override val name: String = "ama"

	public val amaData: AmaData by inject()

	@OptIn(UnsafeAPI::class)
	override suspend fun setup() {
		ephemeralSlashCommand {
			name = Translations.Commands.ama
			description = Translations.Commands.Ama.description

			ephemeralSubCommand(::AmaConfigArgs, ::AmaConfigModal) {
				name = Translations.Commands.Ama.config
				description = Translations.Commands.Ama.Config.description
				requirePermission(Permission.SendMessages)

				check {
					anyGuild()
					with(amaData) {
						managementChecks()
					}
				}

				var buttonMessage: Message?
				action { modal ->
					val embedConfig = AmaEmbedConfig(modal?.header?.value!!, modal.body.value, modal.image.value)
					buttonMessage = arguments.buttonChannel.asChannelOf<GuildMessageChannel>().createMessage {
						embed {
							title = embedConfig.title
							description = embedConfig.description
							image = embedConfig.imageUrl
						}
					}

					val buttonId = "ama-button.${buttonMessage!!.id}"

					buttonMessage!!.edit {
						val components = components {
							ephemeralButton {
								label = Translations.Buttons.Label.ask
								style = ButtonStyle.Secondary

								id = buttonId
								disabled = true
								action { }
							}
						}
						components.removeAll()
					}

					amaData.setConfig(
						AmaConfig(
							guild!!.id,
							arguments.answerQueueChannel.id,
							arguments.liveChatChannel.id,
							arguments.buttonChannel.id,
							arguments.approvalQueue?.id,
							arguments.flaggedQuestionChannel?.id,
							embedConfig,
							buttonMessage!!.id,
							buttonId,
							false
						)
					)

					respond { content = "Set AMA config" }
				}
			}

			ephemeralSubCommand {
				name = Translations.Commands.Ama.start
				description = Translations.Commands.Ama.Start.description
				requirePermission(Permission.SendMessages)

				check {
					anyGuild()
					with(amaData) {
						managementChecks()
					}
				}

				action {
					val config = amaData.getConfig(guild!!.id)

					if (config == null) {
						respond {
							content = "There is no AMA config for this guild! Please run `/ama config` first."
						}
						return@action
					}

					if (config.enabled) {
						respond {
							content = "AMA is already started for this guild"
						}
						return@action
					}

					guild!!.getChannelOf<GuildMessageChannel>(config.buttonChannel).getMessage(config.buttonMessage)
						.edit {
							components?.removeFirst()
							val newComponents = components {
								ephemeralButton {
									label = Translations.Buttons.Label.ask
									style = ButtonStyle.Primary

									id = config.buttonId
									action { }
								}
							}
							newComponents.removeAll()
						}

					amaData.modifyButton(guild!!.id, true)

					respond { content = "AMA Started!" }
				}
			}

			ephemeralSubCommand {
				name = Translations.Commands.Ama.stop
				description = Translations.Commands.Ama.Stop.description
				requirePermission(Permission.SendMessages)

				check {
					anyGuild()
					with(amaData) {
						managementChecks()
					}
				}

				action {
					val config = amaData.getConfig(guild!!.id)

					if (config == null) {
						respond {
							content = "There is no AMA config for this guild!"
						}
						return@action
					}

					if (!config.enabled) {
						respond {
							content = "AMA is already stopped for this guild"
						}
						return@action
					}

					guild!!.getChannelOf<GuildMessageChannel>(config.buttonChannel).getMessage(config.buttonMessage)
						.edit {
							components?.removeFirst()
							val newComponents = components {
								ephemeralButton {
									label = Translations.Buttons.Label.ask
									style = ButtonStyle.Secondary

									id = config.buttonId
									disabled = true
									action { }
								}
							}
							newComponents.removeAll()
						}

					amaData.modifyButton(guild!!.id, false)

					respond { content = "AMA Stopped!" }
				}
			}
		}

		event<GuildButtonInteractionCreateEvent> {
			check {
				anyGuild()
				failIfNot { event.interaction.componentId.contains("ama-button.") }
				with(amaData) {
					userChecks()
				}
			}

			action {
				// Start modal creation
				val modalObj = AskModal()
				val componentRegistry: ComponentRegistry by inject()
				var interactionResponse: EphemeralMessageInteractionResponseBehavior? = null

				componentRegistry.register(modalObj)

				event.interaction.modal(modalObj.title.withLocale(getLocale()).translate(), modalObj.id) {
					modalObj.applyToBuilder(this, getLocale())
				}

				modalObj.awaitCompletion { modalSubmitInteraction ->
					interactionResponse = modalSubmitInteraction?.deferEphemeralMessageUpdate()
				}
				// End modal creation

				val config = amaData.getConfig(guildFor(event)!!.id) ?: return@action

				val channelId = config.approvalQueueChannel ?: config.answerQueueChannel
				val channel = guildFor(event)?.getChannelOf<GuildMessageChannel>(channelId)

				val originalInteractionUser = event.interaction.user
				val pkMember = checkPK(originalInteractionUser, modalObj.pkId.value, interactionResponse)
				if (pkMember !is PKResult.Success) {
					return@action
				}

				val embedMessage = channel?.createEmbed {
					questionEmbed(originalInteractionUser, modalObj.question.value, QuestionStatusFlag.NO_FLAG, pkMember.result)
				}

				val answerQueueChannel = guildFor(event)?.getChannelOf<GuildMessageChannel>(config.answerQueueChannel)
				val liveChatChannel = guildFor(event)?.getChannelOf<GuildMessageChannel>(config.liveChatChannel)
				val flaggedQueueChannel = if (config.flaggedQuestionChannel != null) {
					guildFor(event)?.getChannelOf<GuildMessageChannel>(config.flaggedQuestionChannel)
				} else {
					null
				}

				embedMessage?.edit {
					components {
						questionComponents(
							embedMessage,
							originalInteractionUser,
							pkMember.result,
							modalObj.question.value,
							answerQueueChannel,
							liveChatChannel,
							flaggedQueueChannel,
							config.flaggedQuestionChannel
						)
					}
				}

				interactionResponse?.createEphemeralFollowup { content = "Question sent!" }
			}
		}

		unsafeSlashCommand {
			name = Translations.Commands.ask
			description = Translations.Commands.Ask.description

			initialResponse = InitialSlashCommandResponse.None

			check {
				anyGuild()
				with(amaData) {
					userChecks()
				}
			}

			action {
				if (amaData.isButtonEnabled(guild!!.id) == false) {
					ackEphemeral()
					respondEphemeral { content = "The AMA is not running!" }
					return@action
				}
				// Start modal creation
				val modalObj = AskModal()

				this@unsafeSlashCommand.componentRegistry.register(modalObj)

				event.interaction.modal(modalObj.title.withLocale(getLocale()).translate(), modalObj.id) {
					modalObj.applyToBuilder(this, getLocale())
				}

				modalObj.awaitCompletion { modalSubmitInteraction ->
					interactionResponse = modalSubmitInteraction?.deferEphemeralMessageUpdate()
				}

				// End modal creation
				val config = amaData.getConfig(guild!!.id) ?: return@action

				val channelId = config.approvalQueueChannel ?: config.answerQueueChannel
				val channel = guild?.getChannelOf<GuildMessageChannel>(channelId)

				val originalInteractionUser = event.interaction.user

				val pkMember = checkPK(originalInteractionUser, modalObj.pkId.value, interactionResponse)
				if (pkMember !is PKResult.Success) {
					return@action
				}

				val embedMessage = channel?.createEmbed {
					questionEmbed(
						originalInteractionUser,
						modalObj.question.value,
						QuestionStatusFlag.NO_FLAG,
						pkMember.result
					)
				}

				val answerQueueChannel = guild?.getChannelOf<GuildMessageChannel>(config.answerQueueChannel)
				val liveChatChannel = guild?.getChannelOf<GuildMessageChannel>(config.liveChatChannel)
				val flaggedQueueChannel = if (config.flaggedQuestionChannel != null) {
					guildFor(event)?.getChannelOf<GuildMessageChannel>(config.flaggedQuestionChannel)
				} else {
					null
				}

				embedMessage?.edit {
					components {
						questionComponents(
							embedMessage,
							originalInteractionUser,
							pkMember.result,
							modalObj.question.value,
							answerQueueChannel,
							liveChatChannel,
							flaggedQueueChannel,
							config.flaggedQuestionChannel
						)
					}
				}

				interactionResponse?.createEphemeralFollowup { content = "Question Sent" }
			}
		}
	}

	public suspend inline fun Channel?.checkPermission(permissions: Permissions): Boolean? {
		this ?: return null
		val topGuildChannel = this.asChannelOfOrNull<TopGuildChannel>() ?: return null
		return topGuildChannel.getEffectivePermissions(kord.selfId).contains(permissions)
	}

	private suspend fun checkPK(
		user: User,
		pkId: String?,
		interactionResponse: MessageInteractionResponseBehavior?
	): PKResult<PKMember?> {
		if (pkId == null) {
			return PKResult.Success(null)
		}

		val result = user.getPluralKitMember(pkId)
		when (result) {
			PKResult.SystemNotFound -> {
				if (pkId.isBlank()) {
					return PKResult.Success(null)
				}

				interactionResponse?.createEphemeralFollowup {
					content = "**Error**: No PK system is associated with your Discord account. Please try " +
						"again, leaving the field blank."
				}
			}
			PKResult.SystemNotAccessible -> {
				if (pkId.isBlank()) {
					return PKResult.Success(null)
				}

				interactionResponse?.createEphemeralFollowup {
					content = "**Error**: The PK system associated with your Discord account is not " +
						"accessible. Please try again, leaving the field blank, or contact a staff member."
				}
			}
			PKResult.MemberNotFound -> interactionResponse?.createEphemeralFollowup {
				content = "**Error**: No PK member was found with the provided information. If you used " +
					"a member's name, please try again with their ID or UUID."
			}
			PKResult.MemberNotFromUser -> interactionResponse?.createEphemeralFollowup {
				content = "**Error**: The PK member you provided is not from your system. Please try " +
					"again with a member from your system, or leave the field blank."
			}
			PKResult.NotPermitted -> interactionResponse?.createEphemeralFollowup {
				content = "**Error**: The PK system or member not accessible. Please try again, leaving the field " +
					"blank, or contact a staff member."
			}
			PKResult.NoFronter -> return PKResult.Success(null)
			is PKResult.Success -> return result
		}

		return result
	}

	public inner class AmaConfigArgs : Arguments() {
		public val answerQueueChannel: Channel by channel {
			name = Translations.Args.Ama.Config.answerQueueChannel
			description = Translations.Args.Ama.Config.AnswerQueueChannel.description
			requiredChannelTypes = mutableSetOf(ChannelType.GuildText)

			validate {
				val checkResult = value.checkPermission(Permissions(Permission.ViewChannel, Permission.SendMessages))
				failIf(checkResult == false, "The bot cannot see this channel")
				failIf(checkResult == null, "Cannot find this channel!")
			}
		}

		public val liveChatChannel: Channel by channel {
			name = Translations.Args.Ama.Config.liveChatChannel
			description = Translations.Args.Ama.Config.LiveChatChannel.description
			requiredChannelTypes = mutableSetOf(ChannelType.GuildText)

			validate {
				val checkResult = value.checkPermission(Permissions(Permission.ViewChannel, Permission.SendMessages))
				failIf(checkResult == false, "The bot cannot see this channel")
				failIf(checkResult == null, "Cannot find this channel!")
			}
		}

		public val buttonChannel: Channel by channel {
			name = Translations.Args.Ama.Config.buttonChannel
			description = Translations.Args.Ama.Config.ButtonChannel.description
			requiredChannelTypes = mutableSetOf(ChannelType.GuildText)
			validate {
				val checkResult = value.checkPermission(Permissions(Permission.ViewChannel, Permission.SendMessages))
				failIf(checkResult == false, "The bot cannot see this channel")
				failIf(checkResult == null, "Cannot find this channel!")
			}
		}

		public val approvalQueue: Channel? by optionalChannel {
			name = Translations.Args.Ama.Config.approvalQueue
			description = Translations.Args.Ama.Config.ApprovalQueue.description
			requiredChannelTypes = mutableSetOf(ChannelType.GuildText)
			validate {
				val checkResult = value.checkPermission(Permissions(Permission.ViewChannel, Permission.SendMessages))
				failIf(checkResult == false, "The bot cannot see this channel")
			}
		}

		public val flaggedQuestionChannel: Channel? by optionalChannel {
			name = Translations.Args.Ama.Config.flaggedQuestionChannel
			description = Translations.Args.Ama.Config.FlaggedQuestionChannel.description
			requiredChannelTypes = mutableSetOf(ChannelType.GuildText)
			validate {
				val checkResult = value.checkPermission(Permissions(Permission.ViewChannel, Permission.SendMessages))
				failIf(checkResult == false, "The bot cannot see this channel")
			}
		}
	}

	@Suppress("MagicNumber")
	public inner class AmaConfigModal : ModalForm() {
		public override var title: Key = Translations.Modal.Config.title

		public val header: LineTextWidget = lineText {
			label = Translations.Modal.Config.Header.label
			placeholder = Translations.Modal.Config.Header.placeholder
			maxLength = 200
			required = true
		}

		public val body: ParagraphTextWidget = paragraphText {
			label = Translations.Modal.Config.Body.label
			placeholder = Translations.Modal.Config.Body.placeholder
			maxLength = 1_800
			required = false
		}

		public val image: LineTextWidget = lineText {
			label = Translations.Modal.Config.Image.label
			placeholder = Translations.Modal.Config.Image.placeholder
			required = false
		}
	}

	public inner class AskModal : ModalForm() {
		override var title: Key = Translations.Modal.Ask.title

		public val question: ParagraphTextWidget = paragraphText {
			label = Translations.Modal.Ask.Question.label
			placeholder = Translations.Modal.Ask.Question.placeholder
			required = true
		}

		public val pkId: LineTextWidget = lineText {
			label = Translations.Modal.Ask.PkId.label
			placeholder = Translations.Modal.Ask.PkId.placeholder
			required = false
		}
	}
}
