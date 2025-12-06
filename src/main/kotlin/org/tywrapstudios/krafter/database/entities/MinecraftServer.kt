package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.supplier.getChannelOf
import dev.kordex.core.extensions.Extension
import kotlinx.serialization.Serializable
import org.tywrapstudios.krafter.api.mcsrvstatus.MinecraftServer

@Serializable
class MinecraftServer(
	override val address: String,
	override val bedrock: Boolean = false,
	val name: String,
	val guildId: Snowflake,
	val mainChannel: Snowflake,
	val secondaryChannel: Snowflake?,
) : MinecraftServer
