package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlin.time.Duration

/**
 * A thread that a certain member owns, this may be someone other than who made the thread.
 */
@Serializable
data class OwnedThread(
	val id: Snowflake,

	var owner: Snowflake,
	val guildId: Snowflake,
	var preventArchiving: Boolean = false,

	var maxThreadDuration: Duration? = null,
	var maxThreadAfterIdle: Duration? = null,
)
