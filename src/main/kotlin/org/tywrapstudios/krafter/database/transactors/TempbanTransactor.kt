@file:OptIn(ExperimentalTime::class)

package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.tables.TempbanTable
import org.tywrapstudios.krafter.setup
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

object TempbanTransactor {

	/**
	 * Inserts a temporary ban into the database.
	 */
	suspend fun add(member: Snowflake, guild: Snowflake, timestamp: Instant) {
		transaction {
			setup()

			TempbanTable.replace {
				it[id] = member
				it[guildId] = guild
				it[unbanTime] = timestamp.toJavaInstant()
			}
		}
	}

	/**
	 * Removes a temporary ban from the database.
	 */
	suspend fun remove(member: Snowflake, guild: Snowflake) {
		transaction {
			setup()

			TempbanTable.deleteWhere {
				(id eq member) and (guildId eq guild)
			}
		}
	}

	/**
	 * Returns a list with tuples containing the user's and guild's ID's
	 * with unban times that are **at or before now**.
	 */
	suspend fun getExpired(): List<Pair<Snowflake, Snowflake>> {
		return transaction {
			setup()

			TempbanTable.selectAll().filter {
				it[TempbanTable.unbanTime] <= Clock.System.now().toJavaInstant()
			}.map {
				it[TempbanTable.id].value to it[TempbanTable.guildId]
			}
		}
	}

	/**
	 * Returns a list with tuples containing the user's and guild's ID's
	 * with unban times that are **after now**.
	 */
	suspend fun getActive(): List<Pair<Snowflake, Snowflake>> {
		return transaction {
			setup()

			TempbanTable.selectAll().filter {
				it[TempbanTable.unbanTime] > Clock.System.now().toJavaInstant()
			}.map {
				it[TempbanTable.id].value to it[TempbanTable.guildId]
			}
		}
	}
}
