package org.tywrapstudios.krafter.extensions.funtility.reminder

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.User
import dev.kord.core.entity.effectiveName
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.defaultingBoolean
import dev.kordex.core.commands.converters.impl.optionalChannel
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.optionalTimestamp
import dev.kordex.core.components.forms.ModalForm
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.i18n.types.Key
import dev.kordex.core.time.TimestampType
import dev.kordex.core.time.toDiscord
import dev.kordex.core.utils.scheduling.Task
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.SCHEDULER
import org.tywrapstudios.krafter.database.entities.Reminder
import org.tywrapstudios.krafter.database.transactors.ReminderTransactor
import org.tywrapstudios.krafter.i18n.Translations
import org.tywrapstudios.krafter.snowflake
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class ReminderExtension : Extension() {
	override val name = "krafter.reminder"
	val reminders = ReminderTransactor
	private var checkTask: Task? = null

	@OptIn(ExperimentalTime::class)
	override suspend fun setup() {
		checkTask = SCHEDULER.schedule(
			5.seconds,
			name = "Reminder Checking Task",
			repeat = true
		) {
			for (reminder in reminders.getAll()) {
				if (reminder.timestamp > Clock.System.now()) {
					continue
				}
				if (reminder.repeat) {
					val difference = Clock.System.now() - reminder.timestamp
					val offset = floor(difference / reminder.duration).toInt() + 1
					val newStamp = reminder.timestamp + (reminder.duration * offset)
					LOGGING.debug("Reminder ${reminder.id}: ${reminder.timestamp} $difference $offset $newStamp")
					reminders.update(reminder.id, newStamp)
				} else {
					reminders.remove(reminder.id)
				}
				val contacted = mutableMapOf<Snowflake, Boolean>()
				for (userId in reminder.users) {
					contacted[userId.snowflake()] = false
					if (reminder.dm) {
						val user = kord.getUser(userId.snowflake())
						val dm = user?.getDmChannelOrNull()
						if (dm != null) {
							try {
								dm.createMessage(reminder.content)
								contacted[userId.snowflake()] = true
							} catch(_: Exception ) {
								contacted[userId.snowflake()] = false
								continue
							}
						}
					}
				}
				val contactString = contacted.map { (key, value) ->
					if (!value && reminder.ping) {
						return@map "<@$key>"
					} else {
						val user = kord.getUser(key)
						return@map if (user != null) {
							"@${user.effectiveName}"
						} else {
							"$key"
						}
					}
				}.joinToString(", ")
				val channel = kord.getChannel(reminder.channelId) as? MessageChannelBehavior
				channel?.createMessage {
					content = contactString
					embed {
						description = reminder.content
						color = DISCORD_GREEN
					}
				}
			}
		}

		ephemeralSlashCommand(::ReminderArguments, ::ReminderForm) {
			name = Translations.Commands.remind
			description = Translations.Commands.Remind.description

			action { modal ->
				val channel = arguments.channel as? MessageChannelBehavior ?: channel
				val datetime = arguments.timestamp?.instant
				val duration = Duration.parseOrNull(arguments.duration ?: "null")

				val message = channel.createMessage {
					content = "Setting..."
				}
				if (datetime == null && duration == null) {
					respond {
						content = "Your specified date, time or duration was invalid or null.\n" +
							"Make sure to input one of the two."
					}
					message.edit {
						content = "Aborted."
					}
					message.delete("Reminder creation aborted")
					return@action
				}
				if (datetime != null && duration != null) {
					respond {
						content = "You specified both a datetime and a duration, this is invalid.\n" +
							"Make sure to input one of the two. (Not both!)"
					}
					message.edit {
						content = "Aborted."
					}
					message.delete("Reminder creation aborted")
					return@action
				}
				if (modal?.content?.value == null) {
					respond {
						content = "The content value is null or invalid. Make sure to properly input a content."
					}
					message.edit {
						content = "Aborted."
					}
					return@action
				}

				val instant = if (duration != null) Clock.System.now() + duration else datetime!!
				val reminderDuration = duration ?: (instant - Clock.System.now())
				reminders.set(Reminder(
					message.id,
					channel.id,
					user.id,
					arguments.dm,
					arguments.ping,
					mutableListOf(user.id.value),
					instant,
					reminderDuration,
					arguments.repeat,
					modal.content.value!!
				))
				message.edit {
					reminder(
						modal.content.value!!,
						instant,
						message.id,
						arguments.repeat,
						user.asUser(),
					)
				}
				respond {
					content = "Reminder successfully set."
				}
			}
		}

		event<ButtonInteractionCreateEvent> {
			check { failIfNot(event.interaction.componentId == "reminder:join") }

			action {
				val reminder = reminders.get(event.interaction.message.id)
				if (reminder == null) {
					event.interaction.respondEphemeral {
						content = "This RSVP event could not be found in the database."
					}
					return@action
				}
				if (reminder.users.contains(event.interaction.user.id.value)) {
					reminders.update(
						event.interaction.message.id,
						reminder.users.filter { it != event.interaction.user.id.value }.toMutableList(),
					)
					event.interaction.respondEphemeral {
						content = "Removed you from the reminder list."
					}
				} else {
					reminder.users.add(event.interaction.user.id.value)
					reminders.update(
						event.interaction.message.id,
						reminder.users
					)
					event.interaction.respondEphemeral {
						content = "Added you to the reminder list."
					}
				}
			}
		}

		event<ButtonInteractionCreateEvent> {
			check { failIfNot(event.interaction.componentId == "reminder:cancel") }

			action {
				val reminder = reminders.get(event.interaction.message.id)
				if (reminder == null) {
					event.interaction.respondEphemeral {
						content = "This RSVP event could not be found in the database."
					}
					return@action
				}
				if (reminder.ownerId != event.interaction.user.id) {
					event.interaction.respondEphemeral {
						content = "Only the reminder owner can cancel the reminder."
					}
					return@action
				}
				reminders.remove(event.interaction.message.id)
				event.interaction.message.delete()
				event.interaction.respondEphemeral {
					content = "The reminder has been cancelled."
				}
			}
		}
	}

	class ReminderArguments : Arguments() {
		val channel by optionalChannel {
			name = Translations.Args.Remind.channel
			description = Translations.Args.Remind.Channel.description
		}

		val dm by defaultingBoolean {
			name = Translations.Args.Remind.dm
			description = Translations.Args.Remind.Dm.description
			defaultValue = false
		}

		val ping by defaultingBoolean {
			name = Translations.Args.Remind.ping
			description = Translations.Args.Remind.Ping.description
			defaultValue = true
		}

		val timestamp by optionalTimestamp {
			name = Translations.Args.Remind.timestamp
			description = Translations.Args.Remind.Timestamp.description
		}

		val duration by optionalString {
			name = Translations.Args.Remind.duration
			description = Translations.Args.Remind.Duration.description
		}

		val repeat by defaultingBoolean {
			name = Translations.Args.Remind.repeat
			description = Translations.Args.Remind.Repeat.description
			defaultValue = false
		}
	}

	class ReminderForm : ModalForm() {
		override var title: Key = Translations.Modals.Remind.title

		val content = paragraphText {
			label = Translations.Modals.Remind.Content.label
			placeholder = Translations.Modals.Remind.Content.placeholder
			maxLength = 1_800
		}
	}

	@OptIn(ExperimentalTime::class)
	fun MessageBuilder.reminder(
		reminderContent: String,
		nextReminderTimeStamp: Instant,
		id: Snowflake,
		repeat: Boolean,
		dumbassForgetfulPerson: User,
	) {
		embed {
			title = "Reminder set"
			description = reminderContent
			color = DISCORD_GREEN
			field {
				name = "Timestamp"
				value = "${nextReminderTimeStamp.toDiscord(TimestampType.RelativeTime)}, repeats: $repeat"
			}
			field {
				name = "Id"
				value = "`${id.value}`, use this to manage the reminder."
			}
			footer {
				text = "reminder for ${dumbassForgetfulPerson.username}"
				icon = dumbassForgetfulPerson.avatar?.cdnUrl?.toUrl()
			}
			timestamp = nextReminderTimeStamp
		}

		actionRow {
			interactionButton(ButtonStyle.Primary, "reminder:join") {
				label = "Join"
			}
			interactionButton(ButtonStyle.Danger, "reminder:cancel") {
				label = "Cancel reminder"
			}
		}
	}
}
