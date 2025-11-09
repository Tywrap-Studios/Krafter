package org.tywrapstudios.krafter.database.entities

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kordex.modules.func.tags.data.Tag
import kotlinx.serialization.Serializable

/**
 * A variant of [Tag] that contains a [trigger].
 * This is what is actually inserted into the database, and since trigger is nullable,
 * it can easily be interchanged for the tag module and our own use.
 */
@Serializable
data class TriggerTag(
	val category: String,
	val description: String,
	val key: String,
	val title: String,

	val color: Color? = null,
	val guildId: Snowflake? = null,
	val image: String? = null,

	val trigger: String? = null,
)

/**
 * Converts this [TriggerTag] into a KordEx module [Tag]. This drops the trigger value,
 * but the KordEx module does not utilise it anyway.
 */
fun TriggerTag.toKordExTag(): Tag {
	return Tag(
		category = this.category,
		description = this.description,
		key = this.key,
		title = this.title,

		color = this.color,
		guildId = this.guildId,
		image = this.image,
	)
}

/**
 * Converts this [Tag] into a Krafter module [TriggerTag]. KordEx [Tag]s by default do not
 * contain a trigger value, and as such it can't be recovered even if the original tag was an instance
 * of [TriggerTag] converted to a [Tag].
 */
fun Tag.toTriggerTag(): TriggerTag {
	return TriggerTag(
		category = this.category,
		description = this.description,
		key = this.key,
		title = this.title,

		color = this.color,
		guildId = this.guildId,
		image = this.image,
	)
}
