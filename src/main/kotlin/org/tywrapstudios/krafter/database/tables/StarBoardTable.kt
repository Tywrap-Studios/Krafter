package org.tywrapstudios.krafter.database.tables

import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake

object StarBoardTable : SnowflakeIdTable() {
	val boardMessage = snowflake("board_message").uniqueIndex()
}
