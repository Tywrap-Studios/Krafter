package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.tables.StickyTable
import org.tywrapstudios.krafter.database.tables.StickyTable.channelId
import org.tywrapstudios.krafter.database.tables.StickyTable.lastMessageId
import org.tywrapstudios.krafter.database.tables.StickyTable.tag
import org.tywrapstudios.krafter.database.tables.StickyTable.text
import org.tywrapstudios.krafter.setup

object StickyTransactor {

	/**
	 * Inserts a sticky into the database.
	 */
	suspend fun set(tag: String, channel: Snowflake, lastMessage: Snowflake, content: String) {
		transaction {
			setup()

			StickyTable.insert {
				it[this.tag] = tag
				it[channelId] = channel
				it[lastMessageId] = lastMessage
				it[text] = content
			}
		}
	}

	/**
	 * Returns a tuple containing the channel ID, last message ID and text of a sticky
	 * from the database.
	 */
	suspend fun get(tag: String): Triple<Snowflake, Snowflake, String>? {
		var triple: Triple<Snowflake, Snowflake, String>? = null
		transaction {
			setup()

			StickyTable.selectAll().where(StickyTable.tag eq tag).forEach {
				triple = Triple(it[channelId], it[lastMessageId], it[text])
			}
		}
		return triple
	}

	/**
	 * Returns a list of all the tags active in a channel.
	 */
	suspend fun getTags(channel: Snowflake? = null): List<String> {
		val list = mutableListOf<String>()
		transaction {
			setup()

			if (channel != null) {
				StickyTable.selectAll().where(channelId eq channel).forEach {
					list.add(it[text])
				}
			} else {
				StickyTable.selectAll().forEach {
					list.add(it[text])
				}
			}
		}
		return list
	}

	/**
	 * Returns a list of tuples with the tag, last message ID and text of stickies
	 * from the database.
	 * @param channel The channel to get stickies from, null to get all
	 */
	suspend fun getAll(channel: Snowflake? = null): List<Triple<String, Snowflake, String>> {
		val list = mutableListOf<Triple<String, Snowflake, String>>()
		transaction {
			setup()

			if (channel != null){
				StickyTable.selectAll().where(channelId eq channel).forEach {
					list.add(Triple(it[tag], it[lastMessageId], it[text]))
				}
			} else {
				StickyTable.selectAll().forEach {
					list.add(Triple(it[tag], it[lastMessageId], it[text]))
				}
			}
		}
		return list
	}
}
