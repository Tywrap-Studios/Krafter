package org.tywrapstudios.krafter.api.objects

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class McAuthor(val member: Member, val player: McPlayer?) {
	fun getName(): String {
		return member.nickname ?: member.globalName ?: getUsername()
	}

	fun getUsername(): String {
		return member.username
	}

	fun getMcName(): String? {
		return player?.name
	}

	fun getId(): Snowflake {
		return member.id
	}

	@OptIn(ExperimentalUuidApi::class)
	fun getMcId(): UUID? {
		return if (player == null) null else Uuid.parse(player.id).toJavaUuid()
	}

	fun getMention(): String {
		return member.mention
	}

	fun getAvatarUrl(): String? {
		return member.avatar?.cdnUrl?.toUrl()
	}
}
