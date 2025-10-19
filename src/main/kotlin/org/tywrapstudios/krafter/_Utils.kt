@file:Suppress("WildcardImport", "TooManyFunctions")

package org.tywrapstudios.krafter

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import dev.kordex.core.builders.AboutBuilder
import dev.kordex.core.builders.about.CopyrightType
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.lastOrNull
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.DatabaseManager.krafterSqlLogger
import org.tywrapstudios.krafter.database.tables.*
import java.nio.file.Path
import kotlin.io.path.createDirectories

const val CFG_CHANNEL_REASON = "Config prompted for an automatic new channel creation."
private var copyrightAdded = false

internal fun AboutBuilder.addCopyright() {
	if (copyrightAdded) {
		return
	}

	copyright(
		"Exposed",
		"Apache-2.0",
		CopyrightType.Library,
		"https://www.jetbrains.com/exposed/"
	)
	copyright(
		"Cozy Discord Modules",
		"MPL-2.0",
		CopyrightType.Library,
		"https://github.com/QuiltMC/cozy-discord"
	)
	copyright(
		"Autolink",
		"MIT",
		CopyrightType.Library,
		"https://github.com/robinst/autolink-java"
	)
	copyright(
		"BlossomBridge",
		"MIT",
		CopyrightType.Library,
		"https://docs.tiazzz.me/BlossomBridge/"
	)
	copyright(
		"ExcelKt",
		"MIT",
		CopyrightType.Library,
		"https://github.com/evanrupert/ExcelKt"
	)
	copyright(
		"FlexVer",
		"CC0-1.0",
		CopyrightType.Library,
		"https://git.sleeping.town/unascribed/FlexVer"
	)
	copyright(
		"Jansi",
		"Apache-2.0",
		CopyrightType.Library,
		"https://github.com/fusesource/jansi"
	)
	copyright(
		"RCON",
		"GPL-3.0",
		CopyrightType.Library,
		"https://github.com/jobfeikens/rcon"
	)
	copyright(
		"kotlin-semver",
		"MIT",
		CopyrightType.Library,
		"https://z4kn4fein.github.io/kotlin-semver/"
	)
	copyright(
		"SQLite JDBC Driver",
		"Apache-2.0",
		CopyrightType.Library,
		"https://github.com/xerial/sqlite-jdbc"
	)

	copyrightAdded = true
}

fun config(): BotConfig = CFG.getConfig()

fun saveConfig() = CFG.saveConfig()

fun Transaction.setup() {
    SchemaUtils.create(TagsTable, AmaConfigTable, MinecraftLinkTable, SuggestionTable, OwnedThreadTable)
    addLogger(krafterSqlLogger)
}

fun BotConfig.AdministratorList.getRoles(): Set<String> {
    val roles = HashSet<String>()
    roles.addAll(this.roles)
    roles.addAll(config().global_administrators.roles)
    return roles
}

fun BotConfig.AdministratorList.getUsers(): Set<String> {
    val users = HashSet<String>()
    users.addAll(this.users)
    users.addAll(config().global_administrators.users)
    return users
}

suspend fun getOrCreateChannel(
    providedName: String,
    defaultName: String,
    channelTopic: String = "A channel automatically created by Krafter.",
    permissionOverwrites: MutableSet<Overwrite>?,
    guild: Guild,
): TextChannel {
    var channel: TextChannel?

    val channels = guild
        .channels
        .let { channels ->
            val provided = channels.filter { it.name == providedName }
            val default = channels.filter { it.name == defaultName }
            if (provided.count() < 1) {
                return@let default
            } else {
                return@let provided
            }
        }
    LOGGING.debug("$providedName [$defaultName]: ${channels.count()} channels found.")

    channel = channels.lastOrNull() as? TextChannel

    LOGGING.debug("$providedName [$defaultName]: ${channel?.mention} found.")

    LOGGING.debug("$providedName [$defaultName]: ${providedName == "new"}")
    if (providedName == "new") {
        channel = guild.createTextChannel(defaultName) {
            reason = CFG_CHANNEL_REASON
            topic = channelTopic
            if (permissionOverwrites != null) this.permissionOverwrites = permissionOverwrites
        }
        LOGGING.debug("$providedName [$defaultName]: ${channel.mention} created. New channel.")
    }

    LOGGING.debug("$providedName [$defaultName]: ${providedName.isEmpty() && channel == null} || ${channel == null}")
    if ((providedName.isEmpty() && channel == null) || channel == null) {
        channel = guild.createTextChannel(providedName.ifEmpty { defaultName }) {
            reason = CFG_CHANNEL_REASON
            topic = channelTopic
        }
        LOGGING.debug("$providedName [$defaultName]: ${channel.mention} created. Defaulted name.")
    }

    LOGGING.debug("$providedName [$defaultName]: ${channel.mention} final.")
    return channel
}

// Snowflake skedaddles that are probably bad practice as hell, but I don't care

fun ULong.snowflake() = Snowflake(this)
fun String.snowflake() = Snowflake(this)

@JvmName("mapULongsToSnowflake")
fun Collection<ULong>.snowflake() = this.map { it.snowflake() }.toMutableList()

// Haha Platform declaration clash go brrr
@JvmName("mapStringsToSnowflake")
fun Collection<String>.snowflake() = this.map { it.snowflake() }.toMutableList()

fun Collection<Snowflake>.uLongs() = this.map { it.value }.toMutableList()

fun getDataDirectory(): Path = Path.of("").resolve("data/krafter").createDirectories()
fun getConfigDirectory(): Path = Path.of("").resolve("config").createDirectories()
