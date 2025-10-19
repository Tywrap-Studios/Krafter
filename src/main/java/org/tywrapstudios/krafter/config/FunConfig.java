package org.tywrapstudios.krafter.config;

import org.tywrapstudios.blossombridge.api.config.ConfigClass;

import blue.endless.jankson.Comment;

public class FunConfig implements ConfigClass {

	@Comment("Whether to enable the Fun feature.")
	public boolean enabled = false;

	@Comment("People who administer Fun functions.")
	public AdministratorList administrators = new AdministratorList();

	@Override
	public void validate() {

	}
}
