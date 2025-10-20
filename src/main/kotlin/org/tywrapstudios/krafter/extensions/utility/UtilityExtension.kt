/*
 * The Krafter Utility Extension was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:OptIn(ExperimentalTime::class, KordPreview::class)

@file:Suppress("MagicNumber", "NoUnusedImports")  // Apparently Duration.Companion.seconds isn't used enough?

package org.tywrapstudios.krafter.extensions.utility

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.MessageType
import dev.kord.core.behavior.channel.asChannelOfOrNull
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.threads.edit
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.thread.TextChannelThreadCreateEvent
import dev.kord.core.event.channel.thread.ThreadUpdateEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.annotations.NotTranslated
import dev.kordex.core.annotations.UnexpectedFunctionBehaviour
import dev.kordex.core.checks.isInThread
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.*
import dev.kordex.core.components.components
import dev.kordex.core.components.ephemeralButton
import dev.kordex.core.components.forms.ModalForm
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralMessageCommand
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.time.TimestampType
import dev.kordex.core.time.toDiscord
import dev.kordex.core.utils.envOrNull
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.forms.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.tywrapstudios.krafter.*
import org.tywrapstudios.krafter.checks.isGlobalBotAdmin
import org.tywrapstudios.krafter.database.entities.OwnedThread
import org.tywrapstudios.krafter.database.transactors.OwnedThreadTransactor
import org.tywrapstudios.krafter.extensions.sab.getOverwrites
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

val STATUS_CHANNEL_ID = envOrNull("STATUS_CHANNEL")
val PIN_DELETE_DELAY = 10.seconds
val THREAD_CREATE_DELETE_DELAY = 30.minutes

const val EVENT_LOGS_CHANNEL = "logged-events"

class UtilityExtension : Extension() {
	override val name: String = "utility"

	private val logger = KotlinLogging.logger { }
	private val threads: OwnedThreadTransactor = OwnedThreadTransactor

	@OptIn(ExperimentalSerializationApi::class)
	private val json = Json {
		prettyPrint = true
		prettyPrintIndent = "    "

		classDiscriminator = "_type"
		encodeDefaults = false
	}

	@OptIn(UnexpectedFunctionBehaviour::class)
	override suspend fun setup() {
		if (STATUS_CHANNEL_ID != null) {
			event<ReadyEvent> {
				action {
					val channel = getOrCreateChannel(
						mainConfig().channel,
						"bot-dump",
						"Bot dump channel where it dumps its dump.",
						mutableSetOf(),
						event.guilds.first().asGuild()
					)

					channel.createMessage {
						content = buildString {
							append("**Bot connected:** ")
							append(Clock.System.now().toDiscord(TimestampType.LongDateTime))
							append(" (")
							append(Clock.System.now().toDiscord(TimestampType.RelativeTime))
							append(")")
						}
					}
				}
			}
		}

//		event<MessageCreateEvent> {
//			check { failIf { event.message.type != MessageType.ChannelPinnedMessage } }
//			check { failIf { event.message.data.authorId != event.kord.selfId } }
//
//			action {
//				delay(PIN_DELETE_DELAY)
//
//				event.message.deleteIgnoringNotFound()
//			}
//		}
//
//		event<MessageCreateEvent> {
//			check { failIf { event.message.type != MessageType.ThreadCreated } }
//
//			action {
//				delay(THREAD_CREATE_DELETE_DELAY)
//
//				event.message.deleteIgnoringNotFound()
//			}
//		}

		event<TextChannelThreadCreateEvent> {
			check { failIf(event.channel.ownerId == kord.selfId) }
			check { failIf(event.channel.member != null) }  // We only want thread creation, not join
			check { failIf(event.channel.owner.asUserOrNull()?.isBot == true) }

			action {
				val owner = event.channel.owner.asUser()

				logger.info { "Thread created by ${owner.tag}" }

				// Work around a Discord API race condition - yes, really!
				// Specifically: "Cannot message this thread until after the post author has sent an initial message."
				delay(2.seconds)

				val parentForum = event.channel.parent.asChannelOfOrNull<ForumChannel>()

				val message = event.channel.createMessage {
					content = "One moment..."
				}

				event.channel.withTyping {
					delay(1.seconds)
				}

				if (parentForum != null) {
					event.channel.getFirstMessage()?.pin("First message in the forum post.")

					event.channel.withTyping {
						delay(1.seconds)
					}

					message.delete()
				} else {
					event.channel.withTyping {
						delay(1.seconds)
					}

					message.edit {
						content = "Welcome to your new thread, ${owner.mention}! This message is at the " +
							"start of the thread. Remember, you're welcome to use the `/thread` commands to manage " +
							"your thread as needed."
					}

					message.pin("First message in the thread.")
				}
			}
		}

		event<ThreadUpdateEvent> {
			action {
				val channel = event.channel
				val ownedThread = threads.get(channel)

				if (channel.isArchived && ownedThread != null && ownedThread.preventArchiving) {
					channel.edit {
						archived = false
						reason = "Preventing thread from being archived."
					}
				}
			}
		}

			ephemeralMessageCommand {
				name = "Raw JSON"

				allowInDms = false

				guild(guildId)

				action {
					val messages = targetMessages.map { it.data }
					val data = json.encodeToString(messages)

					respond {
						content = "Raw message data attached below."

						addFile(
							"message.json",

							ChannelProvider { data.byteInputStream().toByteReadChannel() }
						)
					}
				}
			}

			ephemeralMessageCommand {
				name = "Pin in thread"

				allowInDms = false

				guild(guildId)

				check { isInThread() }

				action {
					val channel = channel.asChannel() as ThreadChannel
					val member = user.asMember(guild!!.id)
					val roles = member.roles.toList().map { it.id }

					if (mainConfig().global_administrators.roles.any { it.snowflake() in roles }) {
						targetMessages.forEach { it.pin("Pinned by ${member.tag}") }
						edit { content = "Messages pinned." }

						return@action
					}

					if (channel.ownerId != user.id && threads.isOwner(channel, user) != true) {
						respond { content = "**Error:** This is not your thread." }

						return@action
					}

					targetMessages.forEach { it.pin("Pinned by ${member.tag}") }

					edit { content = "Messages pinned." }
				}
			}

			ephemeralMessageCommand {
				name = "Unpin in thread"

				allowInDms = false

				guild(guildId)

				check { isInThread() }

				action {
					val channel = channel.asChannel() as ThreadChannel
					val member = user.asMember(guild!!.id)
					val roles = member.roles.toList().map { it.id }

					if (mainConfig().global_administrators.roles.any { it.snowflake() in roles }) {
						targetMessages.forEach { it.unpin("Unpinned by ${member.tag}") }
						edit { content = "Messages unpinned." }

						return@action
					}

					if (channel.ownerId != user.id && threads.isOwner(channel, user) != true) {
						respond { content = "**Error:** This is not your thread." }

						return@action
					}

					targetMessages.forEach { it.unpin("Unpinned by ${member.tag}") }

					edit { content = "Messages unpinned." }
				}
			}

			ephemeralSlashCommand {
				name = "thread"
				description = "Thread management commands"

				allowInDms = false

				guild(guildId)

				check { isInThread() }

				publicSubCommand {
					name = "backup"
					description = "Get all messages in the current thread, saving them into a Markdown file."

					guild(guildId)

					check { isGlobalBotAdmin() }
					check { isInThread() }

					action {
						val thread = channel.asChannelOfOrNull<ThreadChannel>()

						if (thread == null) {
							respondOpposite {
								content = "**Error:** This channel isn't a thread!"
							}

							return@action
						}

						val messageBuilder = StringBuilder()
						val formatter = DateTimeFormatter.ofPattern("dd LL, yyyy -  kk:mm:ss")

						if (thread.lastMessageId == null) {
							respondOpposite {
								content = "**Error:** This thread has no messages!"
							}

							return@action
						}

						val messages = thread.getMessagesBefore(thread.lastMessageId!!)
						val lastMessage = thread.getLastMessage()!!

						val parent = thread.parent.asChannel()

						messageBuilder.append("# Thread: ${thread.name}\n\n")
						messageBuilder.append("* **ID:** `${thread.id.value}`\n")
						messageBuilder.append("* **Parent:** #${parent.name} (`${parent.id.value}`)\n\n")

						val messageStrings: MutableList<String> = mutableListOf()

						messages.collect { msg ->
							val author = msg.author
							val builder = StringBuilder()
							val timestamp = formatter.format(
								msg.id.timestamp.toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime()
							)

							if (msg.content.isNotEmpty() || msg.attachments.isNotEmpty()) {
								val authorName = author?.tag ?: msg.data.author.username

								this@UtilityExtension.logger.debug { "\nAuthor name: `$authorName`\n${msg.content}\n" }

								if (msg.type == MessageType.ChatInputCommand) {
									builder.append("🖥️ ")
								} else if (author == null) {
									builder.append("🌐 ")
								} else if (author.isBot) {
									builder.append("🤖 ")
								} else {
									builder.append("💬 ")
								}

								builder.append("**$authorName** at $timestamp (UTC)\n\n")

								if (msg.content.isNotEmpty()) {
									builder.append(msg.content.lines().joinToString("\n") { line -> "> $line" })
									builder.append("\n\n")
								}

								if (msg.attachments.isNotEmpty()) {
									msg.attachments.forEach { att ->
										builder.append("* 📄 [${att.filename}](${att.url})\n")
									}

									builder.append("\n")
								}

								messageStrings.add(builder.toString())
							}
						}

						messageStrings.reverse()

						lastMessage.let { msg ->
							val author = msg.author
							val builder = StringBuilder()
							val timestamp = formatter.format(
								msg.id.timestamp.toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime()
							)

							if (msg.content.isNotEmpty() || msg.attachments.isNotEmpty()) {
								val authorName = author?.tag ?: msg.data.author.username

								if (msg.type == MessageType.ChatInputCommand) {
									builder.append("🖥️ ")
								} else if (author == null) {
									builder.append("🌐 ")
								} else if (author.isBot) {
									builder.append("🤖 ")
								} else {
									builder.append("💬 ")
								}

								builder.append("**$authorName** at $timestamp (UTC)\n\n")

								if (msg.content.isNotEmpty()) {
									builder.append(msg.content.lines().joinToString("\n") { line -> "> $line" })
									builder.append("\n\n")
								}

								if (msg.attachments.isNotEmpty()) {
									msg.attachments.forEach { att ->
										builder.append("* 📄 [${att.filename}](${att.url})\n")
									}

									builder.append("\n")
								}

								messageStrings.add(builder.toString())
							}
						}

						messageStrings.forEach(messageBuilder::append)

						respond {
							content = "**Thread backup created by ${user.mention}.**"

							addFile(
								"thread.md",

								ChannelProvider { messageBuilder.toString().byteInputStream().toByteReadChannel() }
							)
						}
					}
				}

				ephemeralSubCommand(::RenameArguments) {
					name = "rename"
					description = "Rename the current thread, if you have permission"

					check { isInThread() }

					action {
						val channel = channel.asChannel() as ThreadChannel
						val member = user.asMember(guild!!.id)
						val roles = member.roles.toList().map { it.id }

						if (mainConfig().global_administrators.roles.any { it.snowflake() in roles }) {
							channel.edit {
								name = arguments.name

								reason = "Renamed by ${member.tag}"
							}

							edit { content = "Thread renamed." }

							return@action
						}

						if ((channel.ownerId != user.id && threads.isOwner(channel, user) != true)) {
							edit { content = "**Error:** This is not your thread." }

							return@action
						}

						channel.edit {
							name = arguments.name

							reason = "Renamed by ${member.tag}"
						}

						edit { content = "Thread renamed." }
					}
				}

				ephemeralSubCommand(::ArchiveArguments) {
					name = "archive"
					description = "Archive the current thread, if you have permission"

					check { isInThread() }

					action {
						val channel = channel.asChannel() as ThreadChannel
						val member = user.asMember(guild!!.id)
						val roles = member.roles.toList().map { it.id }
						val ownedThread = threads.get(channel)

						if (mainConfig().global_administrators.roles.any { it.snowflake() in roles }) {
							if (ownedThread != null) {
								ownedThread.preventArchiving = false
								threads.set(ownedThread)
							}

							channel.edit {
								this.archived = true
								this.locked = arguments.lock

								reason = "Archived by ${user.asUser().tag}"
							}

							edit {
								content = "Thread archived"

								if (arguments.lock) {
									content += " and locked"
								}

								content += "."
							}

							return@action
						}

						if (channel.ownerId != user.id && threads.isOwner(channel, user) != true) {
							edit { content = "This is not your thread." }

							return@action
						}

						if (channel.isArchived) {
							edit { content = "**Error:** This channel is already archived." }

							return@action
						}

						if (arguments.lock) {
							edit { content = "**Error:** Only moderators may lock threads." }

							return@action
						}

						if (ownedThread != null && ownedThread.preventArchiving) {
							edit {
								content = "**Error:** This thread can only be archived by a moderator."
							}

							return@action
						}

						channel.edit {
							archived = true

							reason = "Archived by ${user.asUser().tag}"
						}

						edit { content = "Thread archived." }
					}
				}

				ephemeralSubCommand(::PinMessageArguments) {
					name = "pin"
					description = "Pin a message in this thread, if you have permission"

					check { isInThread() }

					action {
						val channel = channel.asChannel() as ThreadChannel
						val member = user.asMember(guild!!.id)
						val roles = member.roles.toList().map { it.id }

						if (arguments.message.channelId != channel.id) {
							edit {
								content = "**Error:** You may only pin a message in the current thread."
							}

							return@action
						}

						if (mainConfig().global_administrators.roles.any { it.snowflake() in roles }) {
							arguments.message.pin("Pinned by ${member.tag}")
							edit { content = "Message pinned." }

							return@action
						}

						if (channel.ownerId != user.id && threads.isOwner(channel, user) != true) {
							edit { content = "**Error:** This is not your thread." }

							return@action
						}

						arguments.message.pin("Pinned by ${member.tag}")

						edit { content = "Message pinned." }
					}
				}

				ephemeralSubCommand(::PinMessageArguments) {
					name = "unpin"
					description = "Unpin a message in this thread, if you have permission"

					check { isInThread() }

					action {
						val channel = channel.asChannel() as ThreadChannel
						val member = user.asMember(guild!!.id)
						val roles = member.roles.toList().map { it.id }

						if (arguments.message.channelId != channel.id) {
							edit {
								content = "**Error:** You may only pin a message in the current thread."
							}

							return@action
						}

						if (mainConfig().global_administrators.roles.any { it.snowflake() in roles }) {
							arguments.message.unpin("Unpinned by ${member.tag}")
							edit { content = "Message unpinned." }

							return@action
						}

						if (channel.ownerId != user.id && threads.isOwner(channel, user) != true) {
							edit { content = "**Error:** This is not your thread." }

							return@action
						}

						arguments.message.unpin("Unpinned by ${member.tag}")

						edit { content = "Message unpinned." }
					}
				}

				ephemeralSubCommand {
					name = "prevent-archiving"
					description = "Prevent the current thread from archiving, if you have permission"

					guild(guildId)

					check { isGlobalBotAdmin() }
					check { isInThread() }

					action {
						val channel = channel.asChannel() as ThreadChannel
						val member = user.asMember(guild!!.id)

						if (channel.isArchived) {
							channel.edit {
								archived = false
								reason = "`/thread prevent-archiving` run by ${member.tag}"
							}
						}

						val thread = threads.get(channel)

						if (thread != null) {
							if (thread.preventArchiving) {
								edit {
									content = "I'm already stopping this thread from being archived."
								}

								return@action
							}

							thread.preventArchiving = true
							threads.set(thread)
						} else {
							threads.set(
								OwnedThread(
									channel.id,
									channel.owner.id,
									channel.guild.id,
									true
								)
							)
						}

						edit { content = "Thread will no longer be archived." }

						guild?.asGuild()?.getModLogChannel()?.createEmbed {
							title = "Thread Persistence Enabled"
							color = DISCORD_BLURPLE

							userField(member, "Moderator")
							channelField(channel, "Thread")
						}
					}
				}

				ephemeralSubCommand(::SetOwnerArguments) {
					name = "set-owner"
					description = "Change the owner of the thread, if you have permission"

					check { isInThread() }

					action {
						val channel = channel.asChannel() as ThreadChannel
						val member = user.asMember(guild!!.id)
						val roles = member.roles.toList().map { it.id }
						var thread = threads.get(channel)

						if (thread == null) {
							thread = OwnedThread(
								id = channel.id,
								owner = channel.ownerId,
								guildId = guild!!.id,
								preventArchiving = false,
							)
						}

						val previousOwner = thread.owner

						if ((thread.owner != user.id && threads.isOwner(channel, user) != true) &&
							!mainConfig().global_administrators.roles.any { it.snowflake() in roles }
						) {
							edit { content = "**Error:** This is not your thread." }
							return@action
						}

						if (thread.owner == arguments.user.id) {
							edit {
								content = "That user already owns this thread."
							}

							return@action
						}

						if (mainConfig().global_administrators.roles.any { it.snowflake() in roles }) {
							thread.owner = arguments.user.id
							threads.set(thread)

							edit { content = "Updated thread owner to ${arguments.user.mention}" }

							guild?.asGuild()?.getModLogChannel()?.createEmbed {
								title = "Thread Owner Updated (Moderator)"
								color = DISCORD_BLURPLE

								userField(member.asUser(), "Moderator")
								userField(guild!!.getMember(previousOwner), "Previous Owner")
								userField(arguments.user, "New Owner")
								channelField(channel, "Thread")
							}
						} else {
							respond {
								embed {
									color = DISCORD_BLURPLE
									description =
										"Are you sure you want to transfer ownership to " +
											"${arguments.user.mention}? To cancel the" +
											" transfer, simply ignore this message."
								}

								components(15.seconds) {
									onTimeout {
										edit {
											embed {
												color = DISCORD_BLURPLE
												description =
													"Action timed out - no change performed"
											}

											components {
												removeAll()
											}
										}
									}

									ephemeralButton {
										label = "Yes"
										action {
											thread.owner = arguments.user.id
											threads.set(thread)

											edit {
												embed {
													color = DISCORD_BLURPLE
													description =
														"Updated thread owner to " +
															arguments.user.mention
												}

												components {
													removeAll()
												}
											}

											guild?.asGuild()?.getModLogChannel()?.createEmbed {
												title = "Thread Owner Updated (User)"
												color = DISCORD_BLURPLE

												userField(member.asUser(), "Previous Owner")
												userField(arguments.user, "New Owner")
												channelField(channel, "Thread")
											}
										}
									}
								}
							}
						}
					}
				}
			}

			ephemeralSlashCommand(::SayArguments) {
				name = "say"
				description = "Send a message."

				allowInDms = false

				guild(guildId)

				check { isGlobalBotAdmin() }

				action {
					val targetChannel = (arguments.target ?: channel.asChannel()) as GuildMessageChannel

					targetChannel.createMessage(arguments.message)

					guild?.asGuild()?.getModLogChannel()?.createEmbed {
						title = "/say command used"
						description = arguments.message

						field {
							inline = true
							name = "Channel"
							value = targetChannel.mention
						}

						field {
							inline = true
							name = "User"
							value = user.mention
						}
					}

					edit { content = "Done!" }
				}
			}
	}

	suspend fun Guild.getModLogChannel() =
		getOrCreateChannel(
			sabConfig().channel,
			"moderation",
			"Safety and Abuse logging and dump channel for the Krafter software",
			getOverwrites(this),
			this
		)

	inner class EventModal : ModalForm() {
		override var title: String = "Log Event"

		val description = lineText {
			label = "Description"
			placeholder = "A few words on what's happening"
		}
	}

	inner class PinMessageArguments : Arguments() {
		val message by message {
			name = "message"
			description = "Message link or ID to pin/unpin"
		}
	}

	inner class RenameArguments : Arguments() {
		val name by string {
			name = "name"
			description = "Name to give the current thread"
		}
	}

	inner class ArchiveArguments : Arguments() {
		val lock by defaultingBoolean {
			name = "lock"
			description = "Whether to lock the thread, if you're staff - defaults to false"

			defaultValue = false
		}
	}

	inner class SetOwnerArguments : Arguments() {
		val user by user {
			name = "user"
			description = "User to set as the owner of the thread"
		}
	}

	inner class SayArguments : Arguments() {
		val message by string {
			name = "message"
			description = "Message to send"
		}

		@OptIn(NotTranslated::class)
		val target by optionalChannel {
			name = "target"
			description = "Channel to use, if not this one"

			validate {
				if (value != null && value !is GuildMessageChannel) {
					fail("${value?.mention} is not a guild text channel.")
				}
			}
		}
	}
}

