package org.tywrapstudios.krafter.database.tables

import org.jetbrains.exposed.v1.javatime.timestamp
import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake

/**
 * Table that contains values for a temporary ban. No type: use a tuple.
 */
object TempbanTable : SnowflakeIdTable() {
	val guildId = snowflake("guild_id")
	val unbanTime = timestamp("unban_time")
}
