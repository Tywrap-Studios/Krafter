package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
