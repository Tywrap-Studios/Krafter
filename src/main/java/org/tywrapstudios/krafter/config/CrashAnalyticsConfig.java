package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.Comment;

public class CrashAnalyticsConfig implements ConfigClass {

	@Comment("Whether to enable the Minecraft log parser feature.")
	public boolean enabled = false;

	@Comment("The name of the channel which the bot will watch for logs. e.g. \"crash-logs\"")
	public String channel = "crash-logs";

	@Comment("""
		A list of mod ids to watch out for when parsing, that go against the rules of your server.
		This has a few mods in here already that are considered cheat mods globally.
		May be a regular expression or wildcard.""")
	public List<String> bad_mods = new ArrayList<>();

	@Override
	public void validate() {
		channel = Util.channelCheck.apply(channel);
	}
}
