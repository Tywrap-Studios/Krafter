package org.tywrapstudios.krafter.config;

import org.intellij.lang.annotations.RegExp;
import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.Comment;

public class SabConfig implements ConfigClass {

	@Comment("People who administer your server.")
	public AdministratorList administrators = new AdministratorList();

	@Comment("Name of the channel to use for SAB messages.")
	public String channel = "moderation";

	@Comment("""
		The bot software collects data.
		This value must be one of "none", "minimal", "standard" or "extra" to be valid,
		otherwise we'll set it back to "standard" by default. Can only be applied after a full restart.

		For more information on what data the bot collects, how to get at it, and how it's stored,
		please see here: https://docs.kordex.dev/data-collection.html""")
	public String data_collection = "standard";

	@Comment("Whether the bot should block, report and keep your server clean of phishing links.")
	public boolean block_phishing = true;

	@Comment("Additional domains you want removed. Only works if block_phishing is true.")
	public List<String> banned_domains = new ArrayList<>();

	@Comment("A link to the place where your rules are stated.")
	public String rules_link = "";

	@Comment("Whether to block and fix hoisted usernames (usernames that intentionally\n" +
		"try to go up the members list by adding special characters like \".\" and \"!\").")
	public boolean block_hoisting = false;

	@Comment("""
		The regex used to detect hoisted characters in usernames.
		It is recommended to only replace the "\\p{L}" part as this defines characters themselves,
		and the rest defines the fact that it's at the start of the username.
		(Default regex removes all non-Unicode letter (Latin, Cyrillic, Greek, Kanji, etc.) at the start.)""")
	@RegExp
	public String hoist_regex = "^[^\\p{L}]+";

	@Comment("Whether to decancer (replace cancerous, such as fonts, characters with their\n" +
		"ASCII variant) usernames.")
	public boolean decancer_usernames = false;

	@Comment("Whether to enable mod commands such as ban, kick, timeout, etc.")
	public boolean mod_commands = false;

	@Comment("The interval in minutes at which the bot checks to expire duration-based moderation actions.")
	public int minute_interval = 5;

	@Override
	public void validate() {
		channel = Util.channelCheck.apply(channel);

		if (!List.of("none", "minimal", "standard", "extra").contains(data_collection)) data_collection = "standard";

		if (minute_interval < 1) minute_interval = 5;
	}
}
