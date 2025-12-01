package org.tywrapstudios.krafter.database.tables

import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable

/**
 * Based on [org.tywrapstudios.krafter.database.transactors.KrafterMinecraftLinkTransactor.LinkStatus].
 */
object MinecraftLinkTable : SnowflakeIdTable() {
	val uuid = uuid("uuid")
	val code = uinteger("code").uniqueIndex()
	val verified = bool("verified").default(false)
}
