package org.tywrapstudios.krafter.api.mcsrvstatus

interface MinecraftServer {
	val address: String
	val bedrock: Boolean
		get() = false
}

fun MinecraftServer(address: String, bedrock: Boolean = false) = object : MinecraftServer {
	override val address: String = address
	override val bedrock: Boolean = bedrock
}
