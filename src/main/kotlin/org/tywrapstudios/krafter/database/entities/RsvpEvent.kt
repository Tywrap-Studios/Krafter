package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A class that contains the values for an RSVP in the database.
 * @param id The ID of the RSVP, in our implementation the response message ID
 * @param channelId The channel in which to send the RSVP reminder
 * @param organizerId The ID of the person who initially requested the RSVP
 * @param invited A list of [ULong] ID's of whom to ping for the RSVP
 * @param title The title of the event
 * @param description A description of the event
 * @param eventTime When the event takes place
 */
@Serializable
data class RsvpEvent @OptIn(ExperimentalTime::class) constructor(
	val id: Snowflake,
	val channelId: Snowflake,
	val organizerId: Snowflake,
	val invited: MutableList<ULong>,
	val title: String,
	val description: String?,
	val eventTime: Instant,
)
