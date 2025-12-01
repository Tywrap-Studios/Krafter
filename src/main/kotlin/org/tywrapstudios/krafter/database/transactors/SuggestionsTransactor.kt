/*
 * This Krafter database part was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("WildcardImport")

package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MessageBehavior
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.entities.Suggestion
import org.tywrapstudios.krafter.database.tables.SuggestionTable
import org.tywrapstudios.krafter.database.tables.SuggestionTable.fromRow
import org.tywrapstudios.krafter.setup
import org.tywrapstudios.krafter.uLongs

object SuggestionsTransactor {

	/**
	 * Returns a [Suggestion] from the database.
	 */
	suspend fun get(id: Snowflake): Suggestion? {
		var suggestion: Suggestion? = null

		transaction {
			setup()

			SuggestionTable.selectAll()
				.where { SuggestionTable.id eq id }
				.forEach { suggestion = fromRow(it) }
		}

		return suggestion
	}

	/**
	 * Returns a [Suggestion] from the database by its message's ID.
	 */
	suspend fun getByMessage(id: Snowflake): Suggestion? {
		var suggestion: Suggestion? = null

		transaction {
			setup()

			SuggestionTable.selectAll()
				.where { SuggestionTable.message eq id }
				.forEach { suggestion = fromRow(it) }
		}

		return suggestion
	}

	/**
	 * Returns a [Suggestion] from the database by its thread's ID.
	 */
	suspend fun getByThread(id: Snowflake): Suggestion? {
		var suggestion: Suggestion? = null

		transaction {
			setup()

			SuggestionTable.selectAll()
				.where { SuggestionTable.thread eq id }
				.forEach { suggestion = fromRow(it) }
		}

		return suggestion
	}

	/**
	 * Returns a [Suggestion] from the database by its message's behaviour.
	 */
	suspend fun getByMessage(message: MessageBehavior) =
		getByMessage(message.id)

	/**
	 * Returns a list of all the [Suggestion]s in the database.
	 */
	suspend fun getAll(): List<Suggestion> {
		val suggestions = mutableListOf<Suggestion>()

		transaction {
			setup()

			SuggestionTable.selectAll()
				.forEach { suggestions.add(fromRow(it)) }
		}

		return suggestions
	}

	/**
	 * Returns a [Query] of a SQL `SELECT * ... WHERE` operation with the specified [Op].
	 */
	@Deprecated(
		"Unused method from the original package, has no function in this codebase and may not work as expected.",
		level = DeprecationLevel.HIDDEN
	)
	suspend fun find(filter: () -> Op<Boolean>): Query {
		return transaction {
			setup()

			return@transaction SuggestionTable.selectAll().where(filter)
		}
	}

	/**
	 * Inserts a [Suggestion] into the database.
	 */
	suspend fun set(suggestion: Suggestion) {
		transaction {
			setup()

			SuggestionTable.replace {
				it[id] = suggestion.id
				it[guildId] = suggestion.guildId
				it[channelId] = suggestion.channelId

				it[comment] = suggestion.comment
				it[status] = suggestion.status
				it[message] = suggestion.message
				it[thread] = suggestion.thread
				it[threadButtons] = suggestion.threadButtons

				it[text] = suggestion.text
				it[problem] = suggestion.problem
				it[solution] = suggestion.solution

				it[owner] = suggestion.owner
				it[ownerAvatar] = suggestion.ownerAvatar
				it[ownerName] = suggestion.ownerName

				it[positiveVoters] = suggestion.positiveVoters.uLongs()
				it[negativeVoters] = suggestion.negativeVoters.uLongs()

				it[isPluralkit] = suggestion.isPluralkit
			}
		}
	}

	/**
	 * Removes all the suggestions associated with the specified guild
	 */
	suspend fun purgeGuild(guildId: Snowflake) {
		transaction {
			setup()

			SuggestionTable.deleteWhere {
				SuggestionTable.guildId eq guildId
			}
		}
	}
}
