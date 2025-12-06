package org.tywrapstudios.krafter.api.mcsrvstatus

data class OfflineResponse(
	val online: Boolean,
	override val ip: String,
	override val port: Int,
	override val hostname: String? = null,
	val debug: DebugInfo,
) : StatusResponse
