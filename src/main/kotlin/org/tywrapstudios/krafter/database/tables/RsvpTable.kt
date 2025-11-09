package org.tywrapstudios.krafter.database.tables

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.json.json
import org.tywrapstudios.krafter.database.entities.RsvpEvent
import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

object RsvpTable : SnowflakeIdTable() {
	val channelId = snowflake("channel_id")
	val organizerId = snowflake("organizer_id")
	val invited = json<MutableList<ULong>>("invited", Json.Default).default(mutableListOf())
	val title = text("title")
	val description = text("description").nullable()
	val eventTime = timestamp("event_date")

	@OptIn(ExperimentalTime::class)
	fun fromRow(row: ResultRow): RsvpEvent {
		return RsvpEvent(
			row[id].value,
			row[channelId],
			row[organizerId],
			row[invited],
			row[title],
			row[description],
			row[eventTime].toKotlinInstant()
		)
	}
}
