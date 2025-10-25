package org.tywrapstudios.krafter.extensions.funtility

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.User
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
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
import org.tywrapstudios.krafter.database.entities.RsvpEvent
import org.tywrapstudios.krafter.database.transactors.RsvpTransactor
import org.tywrapstudios.krafter.i18n.Translations
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class RsvpExtension : Extension() {
	override val name: String = "krafter.rsvp"
	val rsvp = RsvpTransactor

	@OptIn(ExperimentalTime::class)
	override suspend fun setup() {
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
						user.id,
						listOf(),
						eventName,
						eventDescription,
						eventTime.instant,
					)
				)

				respond {
					content = "Sent RSVP for event '$eventName' at $eventTime. Description: ${eventDescription ?: "No description"}"
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
				if (rsvpEvent.invited.contains(event.interaction.user.id)) {
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

				event.interaction.message.edit {
					rsvp(
						rsvpEvent.title,
						rsvpEvent.description,
						rsvpEvent.eventTime,
						kord.getUser(rsvpEvent.organizerId)!!,
						rsvpEvent.invited
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

	inner class RsvpArguments : Arguments() {
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
	suspend fun MessageBuilder.rsvp(
		eventTitle: String,
		eventDescription: String?,
		eventTimeStamp: Instant,
		organizer: User,
		invited: List<Snowflake> = mutableListOf()
	) {
		embed {
			title = "RSVP: $eventTitle"
			description = eventDescription
			color = DISCORD_RED
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
						"<@${it.value}>"
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
			}
			interactionButton(ButtonStyle.Danger, "rsvp:cancel") {
				label = "Cancel RSVP"
			}
		}
	}
}
