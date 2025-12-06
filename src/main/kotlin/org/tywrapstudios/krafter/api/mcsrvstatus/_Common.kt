package org.tywrapstudios.krafter.api.mcsrvstatus

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json

const val ENDPOINT_BASE = "https://api.mcsrvstat.us"
const val API_BASE = "https://api.mcsrvstat.us/3"
const val BEDROCK_API_BASE = "https://api.mcsrvstat.us/bedrock/3"

internal val MinecraftServer.apiBase get() = if (bedrock) BEDROCK_API_BASE else API_BASE

internal val client: HttpClient = HttpClient(CIO) {
	install(ContentNegotiation) {
		json(
			kotlinx.serialization.json.Json { ignoreUnknownKeys = true },
			ContentType.Any
		)
	}
	install(UserAgent) {
		agent = "Tywrap-Studios/Krafter (mcsrvstatus API) (tywrap-studios.tiazzz.me)"
	}
}

internal val imageClient: HttpClient = HttpClient(CIO) {
	install(ContentNegotiation) {
		json(
			kotlinx.serialization.json.Json { ignoreUnknownKeys = true },
			ContentType.Image.PNG
		)
	}
	install(UserAgent) {
		agent = "Tywrap-Studios/Krafter (mcsrvstatus Image API) (tywrap-studios.tiazzz.me)"
	}
}
