package org.tywrapstudios.krafter.database.tables

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.javatime.duration
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.json.json
import org.tywrapstudios.krafter.database.entities.Reminder
import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration
import kotlin.time.toKotlinInstant

object ReminderTable : SnowflakeIdTable() {
	val channelId = snowflake("channel")
	val ownerId = snowflake("owner")
	val dm = bool("dm")
	val ping = bool("ping")
	val users = json<MutableList<ULong>>("users", Json.Default).default(mutableListOf())
	val timestamp = timestamp("datetime")
	val duration = duration("duration")
	val repeat = bool("repeat")
	val content = mediumText("content")

	@OptIn(ExperimentalTime::class)
	fun fromRow(row: ResultRow): Reminder {
		return Reminder(
			row[id].value,
			row[channelId],
			row[ownerId],
			row[dm],
			row[ping],
			row[users],
			row[timestamp].toKotlinInstant(),
			row[duration].toKotlinDuration(),
			row[repeat],
			row[content]
		)
	}
}
