package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.deleteReturning
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.entities.RsvpEvent
import org.tywrapstudios.krafter.database.tables.RsvpTable
import org.tywrapstudios.krafter.setup
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

object RsvpTransactor {

	/**
	 * Inserts an RSVP into the database.
	 */
	@OptIn(ExperimentalTime::class)
	suspend fun setRsvp(event: RsvpEvent) {
		transaction {
			setup()

			RsvpTable.replace {
				it[id] = event.id
				it[channelId] = event.channelId
				it[organizerId] = event.organizerId
				it[invited] = event.invited
				it[title] = event.title
				it[description] = event.description
				it[RsvpTable.eventTime] = event.eventTime.toJavaInstant()
			}
		}
	}

	/**
	 * Returns all the RSVP's that occur **after** the specified [Instant].
	 */
	@ExperimentalTime
	suspend fun getRsvpsAfter(time: Instant): List<RsvpEvent> {
		val events = mutableListOf<RsvpEvent>()
		transaction {
			setup()

			RsvpTable.selectAll()
				.where { RsvpTable.eventTime greater time.toJavaInstant() }
				.forEach {
					events.add(RsvpTable.fromRow(it))
				}
		}
		return events
	}

	/**
	 * Returns all the RSVP's that occur **before** the specified [Instant].
	 */
	@ExperimentalTime
	suspend fun getRsvpsBeforeAndAt(time: Instant): List<RsvpEvent> {
		val events = mutableListOf<RsvpEvent>()
		transaction {
			setup()

			RsvpTable.selectAll()
				.where { RsvpTable.eventTime lessEq time.toJavaInstant() }
				.forEach {
					events.add(RsvpTable.fromRow(it))
				}
		}
		return events
	}

	/**
	 * Returns an RSVP from the database.
	 */
	suspend fun getRsvp(eventId: Snowflake): RsvpEvent? {
		return transaction {
			setup()

			val row = RsvpTable.selectAll()
				.where { RsvpTable.id eq eventId }
				.singleOrNull() ?: return@transaction null
			RsvpTable.fromRow(row)
		}
	}

	/**
	 * Adds or removes a person from the invited list.
	 * @param unsubscribe Whether to remove the user from the list instead of adding them to it
	 */
	suspend fun subScribe(eventId: Snowflake, userId: Snowflake, unsubscribe: Boolean = false) {
		transaction {
			setup()

			val current = RsvpTable.selectAll()
				.where { RsvpTable.id eq eventId }
				.singleOrNull() ?: return@transaction

			RsvpTable.replace {
				it[id] = current[id]
				it[channelId] = current[channelId]
				it[organizerId] = current[organizerId]
				it[title] = current[title]
				it[description] = current[description]
				it[eventTime] = current[eventTime]
				val current = current[invited]
				if (unsubscribe) {
					current.remove(userId.value)
				} else if (!current.contains(userId.value)) {
					current.add(userId.value)
				}
				it[invited] = current
			}
		}
	}

	/**
	 * Removes and returns an RSVP from the database.
	 */
	suspend fun cancelRsvp(eventId: Snowflake): RsvpEvent? {
		return transaction {
			setup()

			return@transaction RsvpTable.fromRow(
				RsvpTable.deleteReturning { RsvpTable.id eq eventId }.firstOrNull() ?: return@transaction null
			)
		}
	}
}
