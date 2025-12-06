package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.tables.StarBoardTable
import org.tywrapstudios.krafter.setup

object StarBoardTransactor {
	suspend fun set(message: Snowflake, board: Snowflake) {
		transaction {
			setup()

			StarBoardTable.replace {
				it[id] = message
				it[boardMessage] = board
			}
		}
	}

	suspend fun starred(message: Snowflake): Boolean {
		return transaction {
			setup()

			!StarBoardTable.selectAll().where {
				StarBoardTable.id eq message
			}.empty()
		}
	}

	suspend fun remove(eitherMessage: Snowflake) {
		transaction {
			setup()

			StarBoardTable.deleteWhere {
				(id eq eitherMessage) or (boardMessage eq eitherMessage)
			}
		}
	}

	suspend fun removeAll(): Int {
		return transaction {
			setup()

			StarBoardTable.deleteAll()
		}
	}
}
