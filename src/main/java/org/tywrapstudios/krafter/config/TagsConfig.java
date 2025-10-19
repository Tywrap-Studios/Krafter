package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import blue.endless.jankson.Comment;

public class TagsConfig implements ConfigClass {

	@Comment("Whether to enable the Tags feature.")
	public boolean enabled = false;

	@Comment("People who administer Tags functions.")
	public AdministratorList administrators = new AdministratorList();

	@Comment("Whether to automatically reply to a message with a tag if it fits certain triggers.")
	public boolean auto_tag = false;

	@Override
	public void validate() {

	}
}
