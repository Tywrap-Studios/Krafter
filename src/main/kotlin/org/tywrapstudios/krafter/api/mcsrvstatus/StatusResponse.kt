package org.tywrapstudios.krafter.api.mcsrvstatus

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

interface StatusResponse {
	val ip: String
	val port: Int
	val hostname: String?

	suspend fun asOnline(bedrock: Boolean = false): OnlineResponse = Json.decodeFromJsonElement<OnlineResponse>(getBody(bedrock))

	suspend fun asOffline(bedrock: Boolean = false): OfflineResponse = Json.decodeFromJsonElement<OfflineResponse>(getBody(bedrock))

	private suspend fun getBody(bedrock: Boolean): JsonObject {
		val apiBase = if (bedrock) BEDROCK_API_BASE else API_BASE
		val address = if (hostname != null) hostname!! else "$ip:$port"
		val response = client.get("$apiBase/$address")
		return response.body<JsonObject>()
	}
}
