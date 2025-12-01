/*
 * This Krafter database part was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import org.tywrapstudios.krafter.extensions.suggestion.SuggestionStatus

/**
 * A class that contains the values for a suggestion in the database.
 * @param id The ID of the suggestion
 * @param guildId The ID of the guild in which this suggestion was sent
 * @param channelId The ID of the channel in which this suggestion was sent
 * @param comment A moderator comment/response on the suggestion
 * @param status The current implementation status of the suggestion
 * @param message The ID of the message that contains the suggestion embed
 * @param thread The ID of the thread that contains the discussion about the suggestion
 * @param threadButtons The buttons on the thread message
 * @param text The suggestion
 * @param problem The problem given by the suggester
 * @param solution The solution proposed by the suggester
 * @param owner The ID of the suggester
 * @param ownerAvatar The avatar of the suggester
 * @param ownerName The name of the suggester
 * @param positiveVoters All the people who upvoted the suggestion
 * @param negativeVoters All the people who downvoted the suggestion
 * @param isPluralkit Whether the suggester used PluralKit to suggest
 */
@Serializable
data class Suggestion(
	val id: Snowflake,
	val guildId: Snowflake,
	val channelId: Snowflake,

	var comment: String? = null,
	var status: SuggestionStatus = SuggestionStatus.RequiresName,
	var message: Snowflake? = null,
	var thread: Snowflake? = null,
	var threadButtons: Snowflake? = null,

	var text: String,
	var problem: String? = null,
	var solution: String? = null,

	val owner: Snowflake,
	val ownerAvatar: String?,
	val ownerName: String,

	val positiveVoters: MutableList<Snowflake> = mutableListOf(),
	val negativeVoters: MutableList<Snowflake> = mutableListOf(),

	val isPluralkit: Boolean = false,
) {
	val positiveVotes get() = positiveVoters.size
	val negativeVotes get() = negativeVoters.size
	val voteDifference get() = positiveVotes - negativeVotes
}
