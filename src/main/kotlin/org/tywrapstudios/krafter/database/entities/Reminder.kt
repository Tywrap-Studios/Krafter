package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A class that contains the values for a reminder in the database.
 * @param id The ID of the reminder, in our implementation the response message ID
 * @param channelId The ID of the channel in which to send the reminder
 * @param ownerId The ID of the person who initially set the reminder
 * @param dm Whether to DM the users with the reminder
 * @param ping Whether to ping the users with the reminder
 * @param users A list of [ULong] ID's of whom to remind
 * @param timestamp When to remind
 * @param duration The duration between reminders
 * @param repeat Whether to repeatedly remind
 * @param content The actual reminder
 */
@Serializable
@OptIn(ExperimentalTime::class)
data class Reminder(
	val id: Snowflake,
	val channelId: Snowflake,
	val ownerId: Snowflake,
	val dm: Boolean,
	val ping: Boolean,
	val users: MutableList<ULong>,
	val timestamp: Instant,
	val duration: Duration,
	val repeat: Boolean,
	val content: String
)
