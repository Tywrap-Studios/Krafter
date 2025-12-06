package org.tywrapstudios.krafter.api.mcsrvstatus

import io.ktor.client.request.request
import io.ktor.http.HttpStatusCode

suspend fun MinecraftServer.isOnline(): Boolean? {
	val url = if (bedrock) "$ENDPOINT_BASE/bedrock/simple/$address" else "$ENDPOINT_BASE/simple/$address"
	val response = client.request(url)

	val status = when (response.status) {
		HttpStatusCode.OK -> true
		HttpStatusCode.NotFound -> false
		else -> null
	}

	return status
}

suspend fun MinecraftServer.isOffline(): Boolean? = isOnline()?.not()
