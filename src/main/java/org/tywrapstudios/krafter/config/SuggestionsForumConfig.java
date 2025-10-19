package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.Comment;

public class SuggestionsForumConfig implements ConfigClass {

	@Comment("Whether to enable the Suggestion Forum feature.")
	public boolean enabled = false;

	@Comment("People who administer Suggestion Forum functions.")
	public AdministratorList administrators = new AdministratorList();

	@Comment("""
		The name of the channel in which the bot will use to host the suggestion forum. e.g. "suggestions"
		Contrary to what you'd believe, this is still a regular text channel! Do not feed a Forum channel id
		into this setting!""")
	public String channel = "suggestions";

	@Comment("""
        This setting is for those who want to automatically answer suggestions in case there are FAQ set in place
        or if you don't want your players requesting specific things and explain why.

        This configuration value consists of a List [] that has a so called AnswerMap inside with the following structure:
        {
          "id": "update",       <-- A unique identifier for this answer. Used internally.
          "triggers": [         <-- A list of Strings that indicate the "triggers" that result in the auto-answer.
            "Update to",            If a message contains this word(s) it will count as a trigger message.
            "Downgrade to"      |   This list also supports RegEx.
          ],        V   A single String which is the auto-answer given to the post.
          "answer": "We won't update the server because it would break crucial mods we use.",
          "status": "Denied"    <-- One of: Open, Approved, Denied, Invalid, Spam, Future, Stale, Duplicate, Implemented
        }

        Your config might look something like this:

        "auto_answer": [
          {
            "id": "update",
            "triggers": [
              "Update to",
              "Downgrade to"
            ],
            "answer": "We won't update the server because it would break crucial mods we use.",
            "status": "Denied",
          },
          {
            "id": "loader",
            "triggers": [
              "Use Forge",
              "use (forge|quilt)|change( .+)? to (forge|quilt)|make( it)? (forge|quilt)",
            ],
            "answer": "We won't use a different mod loader because they don't have some crucial mods we use.",
            "status": "Denied"
          }, <-- TIP: You can use so called "trailing" commas because the format is JSON5!
        ]""")
	public List<AnswerMap> auto_answer = new ArrayList<>();
	public static class AnswerMap {
		public final String id = "";
		public final List<String> triggers = new ArrayList<>();
		public final String answer = "";
		public final String status = "Denied";
	}

	@Override
	public void validate() {
		channel = Util.channelCheck.apply(channel);
	}
}
