package org.tywrapstudios.krafter.database.tables

import org.jetbrains.exposed.v1.javatime.timestamp
import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake

object TempbanTable : SnowflakeIdTable() {
	val guildId = snowflake("guild_id")
	val unbanTime = timestamp("unban_time")
}
