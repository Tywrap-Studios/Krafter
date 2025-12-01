package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
