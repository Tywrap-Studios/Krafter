package org.tywrapstudios.krafter.database.transactors

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteReturning
import org.jetbrains.exposed.v1.jdbc.replace
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.krafter.database.entities.MinecraftServer
import org.tywrapstudios.krafter.database.tables.MinecraftServerTable
import org.tywrapstudios.krafter.database.tables.MinecraftServerTable.fromRow
import org.tywrapstudios.krafter.setup

object MinecraftServerTransactor {
	suspend fun set(server: MinecraftServer) {
		transaction {
			setup()

			MinecraftServerTable.replace {
				it[address] = server.address
				it[bedrock] = server.bedrock
				it[name] = server.name
				it[guildId] = server.guildId
				it[mainChannel] = server.mainChannel
				it[secondaryChannel] = server.secondaryChannel
			}
		}
	}

	suspend fun get(address: String): MinecraftServer? {
		var server: MinecraftServer? = null
		transaction {
			setup()

			MinecraftServerTable.selectAll().where { MinecraftServerTable.address eq address }
				.forEach {
					server = fromRow(it)
				}
		}
		return server
	}

	suspend fun getAll(): List<MinecraftServer> {
		val list = mutableListOf<MinecraftServer>()
		transaction {
			setup()

			MinecraftServerTable.selectAll()
				.forEach {
					list.add(fromRow(it))
				}
		}
		return list
	}

	suspend fun remove(address: String): MinecraftServer? {
		return transaction {
			setup()

			return@transaction fromRow(
				MinecraftServerTable.deleteReturning {
					MinecraftServerTable.address eq address
				}.firstOrNull() ?: return@transaction null
			)
		}
	}
}
