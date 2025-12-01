package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteReturning
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.tywrapstudios.krafter.database.entities.Reminder
import org.tywrapstudios.krafter.database.tables.ReminderTable
import org.tywrapstudios.krafter.database.tables.ReminderTable.fromRow
import org.tywrapstudios.krafter.setup
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaDuration
import kotlin.time.toJavaInstant

object ReminderTransactor {
	@OptIn(ExperimentalTime::class)
	suspend fun set(reminder: Reminder) {
		transaction {
			setup()

			ReminderTable.replace {
				it[id] = reminder.id
				it[channelId] = reminder.channelId
				it[ownerId] = reminder.ownerId
				it[dm] = reminder.dm
				it[ping] = reminder.ping
				it[users] = reminder.users
				it[timestamp] = reminder.timestamp.toJavaInstant()
				it[duration] = reminder.duration.toJavaDuration()
				it[repeat] = reminder.repeat
				it[content] = reminder.content
			}
		}
	}

	@OptIn(ExperimentalTime::class)
	suspend fun update(reminderId: Snowflake, reminderTimestamp: Instant) {
		transaction {
			setup()

			ReminderTable.update({ ReminderTable.id eq reminderId }) {
				it[timestamp] = reminderTimestamp.toJavaInstant()
			}
		}
	}

	suspend fun update(reminderId: Snowflake, reminderUsers: MutableList<ULong>) {
		transaction {
			setup()

			ReminderTable.update({ ReminderTable.id eq reminderId }) {
				it[users] = reminderUsers
			}
		}
	}

	suspend fun get(id: Snowflake): Reminder? {
		var reminder: Reminder? = null
		transaction {
			setup()

			ReminderTable.selectAll().where { ReminderTable.id eq id }.forEach {
				reminder = fromRow(it)
			}
		}
		return reminder
	}

	suspend fun getAll(): List<Reminder> {
		return transaction {
			setup()

			ReminderTable.selectAll().map { fromRow(it) }
		}
	}

	suspend fun remove(id: Snowflake): Reminder? {
		return transaction {
			setup()

			fromRow(ReminderTable.deleteReturning { ReminderTable.id eq id }.firstOrNull() ?: return@transaction null)
		}
	}
}
