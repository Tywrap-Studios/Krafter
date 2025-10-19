package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import java.util.HashMap;
import java.util.Map;

import blue.endless.jankson.Comment;

public class EmbedChannelsConfig implements ConfigClass {

	@Comment("Whether to enable the Embed Channels feature.")
	public boolean enabled = false;

	@Comment("People who administer Embed Channels functions.")
	public AdministratorList administrators = new AdministratorList();

	@Comment("""
		A list of channel ids and their channel links.
		Note that for this module, the bot is unable to make new channels automatically.
		Why is this a map and not a list, or even better, a singular entry? Well, the external library used to handle these
		channels works on top of a map like this, for their respective reasons, we just follow. Don't fret though!
		This is one of the least hard setups out here, just simply put the id in the first parentheses, and the full link in the second!
		e.g.: "3848576687382457654": "https://discord.com/channels/4837388485758686865/3848576687382457654""")
	public Map<String, String> channels = new HashMap<>();

	@Override
	public void validate() {

	}
}
