package org.tywrapstudios.krafter.api.mcsrvstatus

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

suspend fun MinecraftServer.asResponse(): StatusResponse? {
	val response = client.get("$apiBase/$address")
	val body = response.body<JsonObject>()
	return when (isOnline()) {
		true -> {
			Json.decodeFromJsonElement<OnlineResponse>(body)
		}

		false -> {
			Json.decodeFromJsonElement<OfflineResponse>(body)
		}

		else -> null
	}
}
