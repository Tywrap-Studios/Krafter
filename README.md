# Krafter

Krafter is a content-rich bot for Discord, built using the Kord and KordEx libraries and Kotlin.
Krafter was made with expansibility in mind, allowing for a lot of configurability and customisation.
Almost everything is turned off by default, and you can enable only the features you want and need with
minimal effort in the neatly organised config files.

## Features
Krafter comes packed with a variety of on-the-go features, all disabled by default and easy to be
nitpicked to your liking.

<details>
  <summary>Click here to view them all!</summary>

- [x] AMA (Ask me Anything) System - Allow users to ask questions and get answers in a structured manner.
- [x] Minecraft Crash log analytics - Automatically analyse and provide insights on Minecraft crash logs.
- [x] Setting up channels with embeds - Easily create channels with rich embeds for better communication.
- [x] Fun utility commands - A variety of commands for entertainment and utility.
  - [x] Haiku detection - Detect and generate Haiku's in chat.
  - [x] RSVP - Manage event attendance with RSVP commands.
  - [x] Thread utilities - Let your players manage their threads easily.
  - [x] Say things as the bot, view Raw Message data, and more.
  - [x] Sticky - Stick messages to the bottom of channels.
  - [x] Reminders - Reminders that either remind you at a certain point in time, or repeatedly remind you.
- [x] Minecraft server connections - Monitor and interact with your Minecraft server.
  - [x] Linking UUIDs to Discord accounts - Link Minecraft UUIDs to Discord accounts for better integration.
  - [x] Server status - Check the status of your Minecraft server.
  - [x] Run commands remotely - Execute commands on your Minecraft server remotely.
  - [x] Whitelist management - Manage your Minecraft server whitelist directly from Discord.
  - [x] Talk to in-game players - Send messages to in-game players from Discord.
- [x] Minecraft utilities - A set of tools to enhance your Minecraft experience.
  - [x] Profile lookups - Look up Minecraft player profiles using UUIDs or usernames.
  - [x] Skin viewing - View Minecraft player skins directly in Discord.
  - [ ] Minecraft release updates - Get notified about new Minecraft releases. (awaiting first-party module)
- [x] Moderation tools - Tools to help moderate your Discord server.
  - [x] Phishing detection - Detect and prevent phishing attempts in your server.
  - [x] Mod commands - Easily kick, ban or time your users out.
  - [x] Dehoist and decancer - Automatically remove problematic characters from usernames.
- [x] Full-blown Suggestion forums - Allow users to submit and vote on suggestions.
- [x] Starboard - Highlight popular messages with a starboard feature.
- [ ] Reaction roles - Assign roles based on reactions to messages. (kinda obsolete because of onboarding)
- [x] Welcome messages - Send personalised welcome messages to new members.
- [x] Tags - Create and manage custom tags for easy access to frequently used information.
- [x] Auto responses - Set up automatic responses to specific keywords or phrases. (using the tags system)
- [x] Basically flawless PluralKit integration - Seamlessly integrate with PluralKit for enhanced server accessibility.
- [x] Extensive configuration options - Customise the bot to fit your server's needs with a wide range of configuration options.
- [ ] Music bot capabilities - Play music in voice channels. (technical limitation)
- [ ] Giveaways - Host giveaways that choose random member(s). (future concern for personal use)

</details>

## Forking and Development

### Prerequisites

* A JDK 21 or newer: [Download](https://adoptium.net/) and install
* [IntelliJ IDEA](https://www.jetbrains.com/idea/), the Community Edition gets it done.
    * Eclipse is technically also supported, but heavily discouraged. If you want to use Eclipse, make sure you install
	  the [the Kotlin plugin](https://marketplace.eclipse.org/content/kotlin-plugin-eclipse), then go to the `Window`
	  menu, `Preferences`, `Kotlin`, `Compiler` and make sure you set up the `JDK_HOME` and JVM target version
* A Discord bot token, from [the developer dashboard](https://discord.com/developers/applications).

### Setting Up

After you've forked and set up your project in your IDE, create
a file named `.env` in the project root (next to files like the `build.gradle.kts`), and fill it out with your bot's
environment values. Here's an example of what the `.env` file could look like:

```dotenv
TOKEN=AAA....
TEST_SERVER=273....

ENVIRONMENT=dev
... other environment variables ...
```
Make sure to replace the values with your own bot's token and test server ID.
NEVER COMMIT THIS FILE TO VERSION CONTROL! This file contains sensitive information
that should not be shared publicly. Bad things can happen if people get a hold
of your bot token.
