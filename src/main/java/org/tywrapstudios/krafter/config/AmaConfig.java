package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import blue.endless.jankson.Comment;

public class AmaConfig implements ConfigClass {

	@Comment("Whether to enable the AMA feature.")
	public boolean enabled = false;

	@Comment("People who administer AMA functions.")
	public AdministratorList administrators = new AdministratorList();

	@Override
	public void validate() {

	}
}
