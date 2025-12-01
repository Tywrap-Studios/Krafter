package org.tywrapstudios.krafter.database.sql

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnTransformer
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable

/**
 * A variant of [IdTable] that uses [Snowflake]s as the [id] value.
 * Good for use if you want to associate objects in a guild, like channels, users or messages
 * with other values that are specific to them.
 */
open class SnowflakeIdTable : IdTable<Snowflake>() {
	override val id: Column<EntityID<Snowflake>> = snowflake("id").entityId()
	override val primaryKey: PrimaryKey? = PrimaryKey(id)
}

/**
 * A [ColumnTransformer] that transforms [ULong]s into [Snowflake]s for the [snowflake] method.
 */
class SnowflakeTransformer : ColumnTransformer<ULong, Snowflake> {
	override fun unwrap(value: Snowflake): ULong {
		return value.value
	}

	override fun wrap(value: ULong): Snowflake {
		return Snowflake(value)
	}
}

/**
 * Extension function to create a [Column] that contains [Snowflake]s.
 */
fun Table.snowflake(name: String) = ulong(name).transform(SnowflakeTransformer())
