# Krafter

Krafter is a content-rich bot for Discord, built using the Kord and KordEx libraries and Kotlin.
Krafter was made with expansibility in mind, allowing for a lot of configurability and customization.
Almost everything is turned off by default, and you can enable only the features you want and need with
minimal effort in the neatly organised config files.

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
