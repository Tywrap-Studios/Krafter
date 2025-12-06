package org.tywrapstudios.krafter.api.mcsrvstatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class OnlineResponse(
	val online: String,
	override val ip: String,
	override val port: Int,
	override val hostname: String? = null,
	val debug: DebugInfo,
	val version: String,
	val protocol: ProtocolInfo? = null,
	val icon: String? = null,
	val software: String? = null,
//	val map: MapInfo,
	@SerialName("gamemode")
	val gameMode: String? = null,
	@SerialName("serverid")
	val serverId: String? = null,
	@SerialName("eula_blocked")
	val eulaBlocked: Boolean? = null,
	val motd: RchInfo,
	val players: PlayerInfo,
	val plugins: List<AddOnInfo>? = null,
	val mods: List<AddOnInfo>? = null,
	val info: RchInfo? = null,
) : StatusResponse {
	fun getAllAddOns(): List<AddOnInfo> {
		val list = mutableListOf<AddOnInfo>()
		if (plugins != null) {
			list.addAll(plugins)
		}
		if (mods != null) {
			list.addAll(mods)
		}
		return list
	}
}

@Serializable
data class ProtocolInfo(
	val version: Int,
	val name: String? = null,
) {
	override fun toString(): String = "$name ($version)"
}

@Serializable
data class MapInfo(
	val raw: String,
	val clean: String,
	val html: String,
)

@Serializable
data class RchInfo(
	val raw: List<String>,
	val clean: List<String>,
	val html: List<String>,
)

@Serializable
data class PlayerInfo(
	val online: Int,
	val max: Int,
	val list: List<PlayerProfile>? = emptyList(),
)

@Serializable
data class PlayerProfile @OptIn(ExperimentalUuidApi::class) constructor(
	val name: String,
	val uuid: Uuid,
) {
	@OptIn(ExperimentalUuidApi::class)
	override fun toString(): String = "$name (${uuid.toHexDashString()})"
}

@Serializable
data class AddOnInfo(
	val name: String,
	val version: String,
)
