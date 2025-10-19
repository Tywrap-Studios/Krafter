/*
 * The Krafter Log Parsing Extension was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.tywrapstudios.krafter.extensions.logs

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.behavior.channel.asChannelOfOrNull
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.Event
import dev.kordex.core.checks.channelFor
import dev.kordex.core.checks.guildFor
import org.quiltmc.community.cozy.modules.logs.data.Log
import org.quiltmc.community.cozy.modules.logs.data.Order
import org.quiltmc.community.cozy.modules.logs.types.LogParser
import org.tywrapstudios.krafter.crashAnalyticsConfig
import org.tywrapstudios.krafter.mainConfig
import org.tywrapstudios.krafter.getOrCreateChannel

class WrongLocationMessageSender : LogParser() {
    override val identifier: String = "wrong-location-message-sender"
    override val order = Order(Int.MIN_VALUE) // be the first parser to run (to destroy the log if necessary)
	var allowedChannel: TextChannel? = null

    @SuppressWarnings("ReturnCount", "MagicNumber")
    override suspend fun predicate(log: Log, event: Event): Boolean {
        val channel = channelFor(event)?.asChannelOfOrNull<TextChannel>() ?: return false
        val guild = guildFor(event)?.asGuildOrNull() ?: return false
		allowedChannel = getOrCreateChannel(
			crashAnalyticsConfig().channel,
			"crash-logs",
			"Send your crash logs here to get help.",
			mutableSetOf(),
			guild
		)

		return channel.id != allowedChannel?.id
	}

    @OptIn(KordExperimental::class, KordUnsafe::class)
	override suspend fun process(log: Log) {
        log.abort("Log sent in the wrong location.\nPlease use ${allowedChannel?.mention} to parse logs.")
    }
}
