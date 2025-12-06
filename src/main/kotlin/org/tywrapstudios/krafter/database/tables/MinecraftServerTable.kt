package org.tywrapstudios.krafter.database.tables

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.tywrapstudios.krafter.database.entities.MinecraftServer
import org.tywrapstudios.krafter.database.sql.snowflake

object MinecraftServerTable : Table() {
	val address = text("address").uniqueIndex()
	val bedrock = bool("bedrock").default(false)
	val name = text("name")
	val guildId = snowflake("guild_id")
	val mainChannel = snowflake("main_channel")
	val secondaryChannel = snowflake("secondary_channel").nullable()

	fun fromRow(row: ResultRow): MinecraftServer {
		return MinecraftServer(
			row[address],
			row[bedrock],
			row[name],
			row[guildId],
			row[mainChannel],
			row[secondaryChannel],
		)
	}
}
