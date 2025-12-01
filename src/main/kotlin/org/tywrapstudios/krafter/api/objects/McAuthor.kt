package org.tywrapstudios.krafter.api.objects

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

/**
 * Frame type that contains a [Member] and [McPlayer] object, contains certain "helper"
 * functions that allow for getting basic values in a classpath that does not contain
 * Kord(Ex).
 */
class McAuthor(val member: Member, val player: McPlayer?) {
	/**
	 * Returns the name of the [member] in the guild it belongs to.
	 *
	 * Picks nickname over global name over username.
	 */
	fun getName(): String {
		return member.nickname ?: member.globalName ?: getUsername()
	}

	/**
	 * Returns the (global) username of the [member].
	 */
	fun getUsername(): String {
		return member.username
	}

	/**
	 * Returns the Minecraft username of the associated [player].
	 * Null if there is no associated [player].
	 */
	fun getMcName(): String? {
		return player?.name
	}

	/**
	 * Returns the ID (as [Snowflake]) of the [member].
	 */
	fun getId(): Snowflake {
		return member.id
	}

	/**
	 * Returns the ID (as [UUID]) of the associated [player].
	 * Null if there is no associated [player].
	 */
	@OptIn(ExperimentalUuidApi::class)
	fun getMcId(): UUID? {
		return if (player == null) null else Uuid.parse(player.id).toJavaUuid()
	}

	/**
	 * Returns a [String] that can be used in Discord to consistently mention the [member].
	 *
	 * Format: `<@ID>`
	 */
	fun getMention(): String {
		return member.mention
	}

	/**
	 * Returns a [String] that contains the CDN URL to the [member]'s avatar. May be
	 * null if something went wrong fetching the avatar.
	 */
	fun getAvatarUrl(): String? {
		return member.avatar?.cdnUrl?.toUrl()
	}
}
