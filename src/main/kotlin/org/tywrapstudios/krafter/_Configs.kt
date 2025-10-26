@file:Suppress("TooManyFunctions")

package org.tywrapstudios.krafter

import org.tywrapstudios.blossombridge.api.config.ConfigManager
import org.tywrapstudios.krafter.config.AdministratorList
import org.tywrapstudios.krafter.config.AmaConfig
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.config.CrashAnalyticsConfig
import org.tywrapstudios.krafter.config.EmbedChannelsConfig
import org.tywrapstudios.krafter.config.FunConfig
import org.tywrapstudios.krafter.config.HaikuAbbreviationsConfig
import org.tywrapstudios.krafter.config.MinecraftConfig
import org.tywrapstudios.krafter.config.SabConfig
import org.tywrapstudios.krafter.config.SuggestionsForumConfig
import org.tywrapstudios.krafter.config.TagsConfig
import org.tywrapstudios.krafter.config.UtilityConfig
import java.io.File
import kotlin.io.path.createDirectories

fun mainConfig(): BotConfig = CFG.getConfig()
val AmaCFG = ConfigManager(
	AmaConfig::class.java,
	getFile("ama")
)
fun amaConfig() = AmaCFG.getConfig()
val CrashAnalyticsCFG = ConfigManager(
	CrashAnalyticsConfig::class.java,
	getFile("log-parser")
)
fun crashAnalyticsConfig() = CrashAnalyticsCFG.getConfig()
val EmbedCFG = ConfigManager(
	EmbedChannelsConfig::class.java,
	getFile("embed-channels")
)
fun embedChannelsConfig() = EmbedCFG.getConfig()
val MinecraftCFG = ConfigManager(
	MinecraftConfig::class.java,
	getFile("minecraft")
)
fun minecraftConfig() = MinecraftCFG.getConfig()
val SabCFG = ConfigManager(
	SabConfig::class.java,
	getFile("safety-and-abuse")
)
fun sabConfig() = SabCFG.getConfig()
val SuggestionsCFG = ConfigManager(
	SuggestionsForumConfig::class.java,
	getFile("suggestions")
)
fun suggestionsConfig() = SuggestionsCFG.getConfig()
val TagsCFG = ConfigManager(
	TagsConfig::class.java,
	getFile("tags")
)
fun tagsConfig() = TagsCFG.getConfig()
val FunCFG = ConfigManager(
	FunConfig::class.java,
	getFile("funtility")
)
fun funConfig() = FunCFG.getConfig()
val HaikuAbbreviationsCFG = ConfigManager(
	HaikuAbbreviationsConfig::class.java,
	getFile("funtility", "haiku-abbreviations")
)
fun haikuAbbreviationsConfig() = HaikuAbbreviationsCFG.getConfig()
val UtilityCFG = ConfigManager(
	UtilityConfig::class.java,
	getFile("funtility", "utility")
)
fun utilityConfig() = UtilityCFG.getConfig()

fun getAllConfigJsonAsString(comments: Boolean, newlines: Boolean): String {
	var string = ""
	string += CFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += AmaCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += CrashAnalyticsCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += EmbedCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += MinecraftCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += SabCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += SuggestionsCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += TagsCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += FunCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += HaikuAbbreviationsCFG.getConfigJsonAsString(comments, newlines) + "\n"
	string += UtilityCFG.getConfigJsonAsString(comments, newlines) + "\n"
	return string
}

fun loadAllConfigs() {
	CFG.loadConfig()
	AmaCFG.loadConfig()
	CrashAnalyticsCFG.loadConfig()
	EmbedCFG.loadConfig()
	MinecraftCFG.loadConfig()
	SabCFG.loadConfig()
	SuggestionsCFG.loadConfig()
	TagsCFG.loadConfig()
	FunCFG.loadConfig()
	HaikuAbbreviationsCFG.loadConfig()
	UtilityCFG.loadConfig()
}

fun saveAllConfigs() {
	CFG.saveConfig()
	AmaCFG.saveConfig()
	CrashAnalyticsCFG.saveConfig()
	EmbedCFG.saveConfig()
	MinecraftCFG.saveConfig()
	SabCFG.saveConfig()
	SuggestionsCFG.saveConfig()
	TagsCFG.saveConfig()
	FunCFG.saveConfig()
	HaikuAbbreviationsCFG.saveConfig()
	UtilityCFG.saveConfig()
}

internal fun getFile(moduleName: String): File =
	File(
		getConfigDirectory().resolve("ext-$moduleName").createDirectories().toFile(),
		"$moduleName.json5"
	)

internal fun getFile(moduleName: String, partName: String): File =
	File(
		getConfigDirectory().resolve("ext-$moduleName").createDirectories().toFile(),
		"$partName.json5"
	)

fun AdministratorList.getRoles(): Set<String> {
	val roles = HashSet<String>()
	roles.addAll(this.roles)
	roles.addAll(mainConfig().global_administrators.roles)
	return roles
}

fun AdministratorList.getUsers(): Set<String> {
	val users = HashSet<String>()
	users.addAll(this.users)
	users.addAll(mainConfig().global_administrators.users)
	return users
}
