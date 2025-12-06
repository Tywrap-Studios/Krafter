package org.tywrapstudios.krafter.api.mcsrvstatus

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.io.encoding.Base64

suspend fun MinecraftServer.getImage(): ByteArray {
	val call = imageClient.get(getImageAddress())

	val bytes = call.body<ByteArray>()

	return bytes
}

suspend fun MinecraftServer.getImageAddress(): String = "$ENDPOINT_BASE/icon/$address"

fun getImageFromBase64(base64: String) = Base64.decode(base64)
