package org.tywrapstudios.krafter.database.tables

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.tywrapstudios.krafter.database.sql.snowflake

/**
 * Table that contains values for a stickied message. No type: use a tuple.
 */
object StickyTable : IntIdTable() {
	val tag = varchar("tag", 20).uniqueIndex()
	val channelId = snowflake("channel")
	val lastMessageId = snowflake("last_message")
	val text = largeText("text")
}
