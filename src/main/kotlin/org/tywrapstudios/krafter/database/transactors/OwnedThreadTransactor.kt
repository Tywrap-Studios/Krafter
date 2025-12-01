package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.entities.OwnedThread
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.fromRow
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.guildId
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.maxThreadAfterIdle
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.maxThreadDuration
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.owner
import org.tywrapstudios.krafter.database.tables.SuggestionTable
import org.tywrapstudios.krafter.setup
import kotlin.time.toJavaDuration

@Suppress("TooManyFunctions")
object OwnedThreadTransactor {

	/**
	 * Gets the [OwnedThread] by the ID of the thread.
	 */
	suspend fun get(id: Snowflake): OwnedThread? {
		var thread: OwnedThread? = null

		transaction {
			setup()

			OwnedThreadTable.selectAll()
				.where { OwnedThreadTable.id eq id }
				.forEach { thread = fromRow(it) }
		}

		return thread
	}

	/**
	 * Gets the [OwnedThread] by behaviour.
	 */
	suspend fun get(thread: ThreadChannelBehavior) =
		get(thread.id)

	/**
	 * Inserts an [OwnedThread] into the database.
	 */
	suspend fun set(thread: OwnedThread) {
		transaction {
			setup()

			OwnedThreadTable.replace {
				it[id] = thread.id

				it[owner] = thread.owner
				it[guildId] = thread.guildId
				it[preventArchiving] = thread.preventArchiving

				it[maxThreadDuration] = thread.maxThreadDuration?.toJavaDuration()
				it[maxThreadAfterIdle] = thread.maxThreadAfterIdle?.toJavaDuration()
			}
		}
	}

	/**
	 * Returns a list of [OwnedThread]s that have a duration.
	 */
	suspend fun getAllWithDuration(): List<OwnedThread> {
		val threads = mutableListOf<OwnedThread>()

		transaction {
			setup()

			OwnedThreadTable.selectAll()
				.where { (maxThreadDuration neq null) or (maxThreadAfterIdle neq null) }
				.forEach { threads.add(fromRow(it)) }
		}

		return threads
	}

	/**
	 * Gets an [OwnedThread] by its owner's ID.
	 */
	suspend fun getByOwner(id: Snowflake): OwnedThread? {
		var thread: OwnedThread? = null

		transaction {
			setup()

			OwnedThreadTable.selectAll()
				.where { owner eq id }
				.forEach { thread = fromRow(it) }
		}

		return thread
	}

	/**
	 * Gets an [OwnedThread] by its owner's behaviour.
	 */
	suspend fun getByOwner(user: UserBehavior) =
		getByOwner(user.id)

	/**
	 * Gets an [OwnedThread] by its guild's ID.
	 */
	suspend fun getByGuild(id: Snowflake): OwnedThread? {
		var thread: OwnedThread? = null

		transaction {
			setup()

			OwnedThreadTable.selectAll()
				.where { guildId eq id }
				.forEach { thread = fromRow(it) }
		}

		return thread
	}

	/**
	 * Gets an [OwnedThread] by its guild's behaviour.
	 */
	suspend fun getByGuild(guild: GuildBehavior) =
		getByGuild(guild.id)

	/**
	 * Gets an [OwnedThread] by its owner's and a guild's ID.
	 */
	suspend fun getByOwnerAndGuild(owner: Snowflake, guild: Snowflake): OwnedThread? {
		var thread: OwnedThread? = null

		transaction {
			setup()

			SuggestionTable.selectAll()
				.where { (OwnedThreadTable.owner eq owner) and (guildId eq guild) }
				.forEach { thread = fromRow(it) }
		}

		return thread
	}

	/**
	 * Gets an [OwnedThread] by its owner's behaviour and a guild's ID.
	 */
	suspend fun getByOwnerAndGuild(owner: UserBehavior, guild: Snowflake) =
		getByOwnerAndGuild(owner.id, guild)

	/**
	 * Gets an [OwnedThread] by its owner's ID and a guild's behaviour.
	 */
	suspend fun getByOwnerAndGuild(owner: Snowflake, guild: GuildBehavior) =
		getByOwnerAndGuild(owner, guild.id)

	/**
	 * Gets an [OwnedThread] by its owner's and a guild's behaviour.
	 */
	suspend fun getByOwnerAndGuild(owner: UserBehavior, guild: GuildBehavior) =
		getByOwnerAndGuild(owner.id, guild.id)

	/**
	 * Returns whether a person owns a thread by ID's.
	 */
	suspend fun isOwner(thread: Snowflake, user: Snowflake) =
		get(thread)?.let { it.owner == user }

	/**
	 * Returns whether a person (by behaviour) owns a thread by ID.
	 */
	suspend fun isOwner(thread: Snowflake, user: UserBehavior) =
		isOwner(thread, user.id)

	/**
	 * Returns whether a person (by ID) owns a thread by behaviour.
	 */
	suspend fun isOwner(thread: ThreadChannelBehavior, user: Snowflake) =
		isOwner(thread.id, user)

	/**
	 * Returns whether a person owns a thread by behaviours.
	 */
	suspend fun isOwner(thread: ThreadChannelBehavior, user: UserBehavior) =
		isOwner(thread.id, user.id)
}
