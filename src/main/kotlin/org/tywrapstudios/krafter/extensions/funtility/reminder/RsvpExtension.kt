package org.tywrapstudios.krafter.extensions.funtility.reminder

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.reply
import dev.kord.core.entity.User
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.commands.converters.impl.timestamp
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.time.TimestampType
import dev.kordex.core.time.toDiscord
import dev.kordex.core.utils.scheduling.Task
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.SCHEDULER
import org.tywrapstudios.krafter.database.entities.RsvpEvent
import org.tywrapstudios.krafter.database.transactors.RsvpTransactor
import org.tywrapstudios.krafter.i18n.Translations
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class RsvpExtension : Extension() {
	override val name: String = "krafter.rsvp"
	val rsvp = RsvpTransactor
	private var checkTask: Task? = null

	@OptIn(ExperimentalTime::class)
	@Suppress("MagicNumber")
	override suspend fun setup() {
		checkTask = SCHEDULER.schedule(
			10.seconds,
			name = "RSVP Checking Task",
			repeat = true
		) {
			val now = Clock.System.now()
			val events = rsvp.getRsvpsBeforeAndAt(now)
			for (event in events) {
				LOGGING.debug("Starting RSVP event ${event.id}.")
				val channel = kord.getChannel(event.channelId) as MessageChannelBehavior
				val message = channel.getMessage(event.id)
				message.reply {
					event.invited.add(0, event.organizerId.value)
					content = event.invited.joinToString(", ") {
						"<@${it}>"
					}
					response(event)
				}
				message.edit {
					rsvp(
						event.title,
						event.description,
						event.eventTime,
						kord.getUser(event.organizerId)!!,
						event.invited,
					)
				}
				rsvp.cancelRsvp(event.id)
			}
		}

		ephemeralSlashCommand(::RsvpArguments) {
			name = Translations.Commands.rsvp
			description = Translations.Commands.Rsvp.description

            action {
                val eventName = arguments.eventName
                val eventTime = arguments.eventTime
                val eventDescription = arguments.description

                val message = channel.createMessage {
                    rsvp(
                        eventName,
                        eventDescription,
                        eventTime.instant,
                        user.asUser(),
                    )
                }

                rsvp.setRsvp(
                    RsvpEvent(
                        message.id,
                        message.channelId,
                        user.id,
                        mutableListOf(),
                        eventName,
                        eventDescription,
                        eventTime.instant,
                    )
                )

                respond {
                    content =
                        "Sent RSVP for event '$eventName' at $eventTime. Description: ${eventDescription ?: "No description"}"
                }
            }
		}

		event<ButtonInteractionCreateEvent> {
			check { failIfNot(event.interaction.componentId == "rsvp:join") }

			action {
				val rsvpEvent = rsvp.getRsvp(event.interaction.message.id)
				if (rsvpEvent == null) {
					event.interaction.respondEphemeral {
						content = "This RSVP event could not be found in the database."
					}
					return@action
				}
				if (rsvpEvent.invited.contains(event.interaction.user.id.value)) {
					rsvp.subScribe(
						event.interaction.message.id,
						event.interaction.user.id,
						true,
					)
					event.interaction.respondEphemeral {
						content = "Removed you from the RSVP list."
					}
				} else {
					rsvp.subScribe(
						event.interaction.message.id,
						event.interaction.user.id,
					)
					event.interaction.respondEphemeral {
						content = "Added you to the RSVP list."
					}
				}

				val newRsvpEvent = rsvp.getRsvp(event.interaction.message.id)!!

				event.interaction.message.edit {
					rsvp(
						newRsvpEvent.title,
						newRsvpEvent.description,
						newRsvpEvent.eventTime,
						kord.getUser(newRsvpEvent.organizerId)!!,
						newRsvpEvent.invited
					)
				}
			}
		}

		event<ButtonInteractionCreateEvent> {
			check { failIfNot(event.interaction.componentId == "rsvp:cancel") }

			action {
				val rsvpEvent = rsvp.getRsvp(event.interaction.message.id)
				if (rsvpEvent == null) {
					event.interaction.respondEphemeral {
						content = "This RSVP event could not be found in the database."
					}
					return@action
				}
				if (rsvpEvent.organizerId != event.interaction.user.id) {
					event.interaction.respondEphemeral {
						content = "Only the event organizer can cancel the RSVP event."
					}
					return@action
				}
				rsvp.cancelRsvp(event.interaction.message.id)
				event.interaction.message.delete()
				event.interaction.respondEphemeral {
					content = "The RSVP event has been cancelled."
				}
			}
		}
	}

	class RsvpArguments : Arguments() {
		val eventName by string {
			name = Translations.Args.Rsvp.eventName
			description = Translations.Args.Rsvp.EventName.description
		}

		val eventTime by timestamp {
			name = Translations.Args.Rsvp.eventTime
			description = Translations.Args.Rsvp.EventTime.description
		}

		val description by optionalString {
			name = Translations.Args.Rsvp.eventDescription
			description = Translations.Args.Rsvp.EventDescription.description
		}
	}

	@OptIn(ExperimentalTime::class)
	fun MessageBuilder.rsvp(
        eventTitle: String,
        eventDescription: String?,
        eventTimeStamp: Instant,
        organizer: User,
        invited: List<ULong> = mutableListOf()
	) {
		embed {
			title = "RSVP: $eventTitle"
			description = eventDescription
			color = if (Clock.System.now() >= eventTimeStamp) DISCORD_GREEN else DISCORD_RED
			field {
				name = "Starts:"
				value = eventTimeStamp.toDiscord(TimestampType.RelativeTime)
			}
			field {
				name = "Current participants:"
				value = if (invited.isEmpty()) {
					"No one... Yet!"
				} else {
					invited.joinToString(", ") {
						"<@${it}>"
					}
				}
			}
			footer {
				text = "hosted by ${organizer.username}"
				icon = organizer.avatar?.cdnUrl?.toUrl()
			}
			timestamp = eventTimeStamp
		}

		actionRow {
			interactionButton(ButtonStyle.Primary, "rsvp:join") {
				label = "Join"
				disabled = Clock.System.now() >= eventTimeStamp
			}
			interactionButton(ButtonStyle.Danger, "rsvp:cancel") {
				label = "Cancel RSVP"
				disabled = Clock.System.now() >= eventTimeStamp
			}
		}
	}

	@OptIn(ExperimentalTime::class)
	suspend fun MessageBuilder.response(
        rsvpEvent: RsvpEvent,
	) {
		val organizer = kord.getUser(rsvpEvent.organizerId)!!

		embed {
			title = "${rsvpEvent.title} is starting!"
			description = rsvpEvent.description
			color = DISCORD_GREEN
			field {
				name = "Started:"
				value = rsvpEvent.eventTime.toDiscord(TimestampType.RelativeTime)
			}
			footer {
				text = "hosted by ${organizer.username}"
				icon = organizer.avatar?.cdnUrl?.toUrl()
			}
			timestamp = rsvpEvent.eventTime
		}
	}
}
