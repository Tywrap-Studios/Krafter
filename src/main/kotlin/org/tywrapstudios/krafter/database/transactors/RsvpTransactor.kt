package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteReturning
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.entities.RsvpEvent
import org.tywrapstudios.krafter.database.tables.RsvpTable
import org.tywrapstudios.krafter.setup
import org.tywrapstudios.krafter.uLongs
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

object RsvpTransactor {
	@OptIn(ExperimentalTime::class)
	suspend fun setRsvp(event: RsvpEvent) {
		transaction {
			setup()

			RsvpTable.replace {
				it[id] = event.id
				it[organizerId] = event.organizerId
				it[invited] = event.invited.uLongs()
				it[title] = event.title
				it[description] = event.description
				it[RsvpTable.eventTime] = event.eventTime.toJavaInstant()
			}
		}
	}

	suspend fun getRsvp(eventId: Snowflake): RsvpEvent? {
		return transaction {
			setup()

			val row = RsvpTable.selectAll()
				.where { RsvpTable.id eq eventId }
				.singleOrNull() ?: return@transaction null
			RsvpTable.fromRow(row)
		}
	}

	suspend fun subScribe(eventId: Snowflake, userId: Snowflake, unsubscribe: Boolean = false) {
		transaction {
			setup()

			RsvpTable.selectAll().where { RsvpTable.id eq eventId }
				.forEach {
					val current = it[RsvpTable.invited]
					if (unsubscribe) {
						current.filterNot { id -> id == userId.value }
					} else {
						if (!current.contains(userId.value))
							current.add(userId.value)
					}
					it[RsvpTable.invited] = current
				}
		}
	}

	suspend fun cancelRsvp(eventId: Snowflake): RsvpEvent? {
		return transaction {
			setup()

			return@transaction RsvpTable.fromRow(
				RsvpTable.deleteReturning { RsvpTable.id eq eventId }.firstOrNull() ?: return@transaction null
			)
		}
	}
}
