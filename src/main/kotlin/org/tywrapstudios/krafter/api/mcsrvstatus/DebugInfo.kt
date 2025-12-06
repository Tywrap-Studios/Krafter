package org.tywrapstudios.krafter.api.mcsrvstatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DebugInfo(
	val ping: Boolean,
	val query: Boolean,
	val bedrock: Boolean,
	val srv: Boolean,
	@SerialName("querymismatch")
	val queryMismatch: Boolean,
	@SerialName("ipinsrv")
	val ipInSrv: Boolean,
	@SerialName("cnameinsrv")
	val cnameInSrv: Boolean,
	@SerialName("animatedmotd")
	val animatedMotd: Boolean,
	@SerialName("cachehit")
	val cacheHit: Boolean,
	@SerialName("cachetime")
	val cacheTime: Long,
	@SerialName("cacheexpire")
	val cacheExpire: Long,
	@SerialName("apiversion")
	val apiVersion: Int,
)
