package org.tywrapstudios.krafter.api.mcsrvstatus

import kotlinx.serialization.Serializable

@Serializable
data class OfflineResponse(
	val online: Boolean,
	override val ip: String,
	override val port: Int,
	override val hostname: String? = null,
	val debug: DebugInfo,
) : StatusResponse
