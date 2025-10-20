package org.tywrapstudios.krafter.config;

import static org.tywrapstudios.krafter.config.Util.channelCheck;

import blue.endless.jankson.Comment;
import org.tywrapstudios.blossombridge.api.config.BasicConfigClass;

import java.util.*;

public class BotConfig extends BasicConfigClass {

    @Comment("Whether the bot should be run altogether.")
    public boolean enabled = true;

    @Comment("""
            Role and user ids that are considered global administrators for the bot.
            They most notably have full permission over most of the SAB and Misc functionality.
            Don't worry though, you can set separate admins for separate functions in their respective configs.""")
    public AdministratorList global_administrators = new AdministratorList();

    @Comment("The prefix for chat commands.")
    public String prefix = ">>";

	@Comment("The name of the channel where the bot should dump its logs and whatever.")
	public String channel = "bot-dump";

	@Comment("""
		The PluralKit software allows you to add accessibility to the bot for Plural people and Systems.
		No idea what plurality is? No worries! There are enough sources online that can explain it neatly.
		We personally recommend reading the following one: https://quiltmc.org/en/community/pluralkit/, as it also
		nicely explains how the PluralKit software works and how to use it.

		Enabling this module is non-invasive, purely helpful, and will only fully work once you add the PluralKit
		bot to your server yourself.""")
	public boolean plural_kit = false;

	@Comment("The type of status the bot should show.\n" +
		"Must be one of: playing, listening, streaming, watching, competing, none.")
	public String status_type = "playing";

	@Comment("The text the bot should show as its status.\n" +
		"Might be overridden by other modules.")
	public String status_text = "with Cords";

	@Comment("Used for the \"streaming\" type.")
	public String status_url = "";

    @Override
    public void validate() {
		if (!List.of(
			"playing",
			"listening",
			"streaming",
			"watching",
			"competing"
		).contains(status_type) || (
			status_text.isEmpty() && !Objects.equals(status_type, "none")
		)) {
			status_type = "playing";
			status_text = "with Cords";
		}

		channel = channelCheck.apply(channel);
    }
}
