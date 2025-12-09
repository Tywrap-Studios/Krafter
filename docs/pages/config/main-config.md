# The Main file
The main file can be found at the root of the Config directory.
It is named `krafter.json5` and contains some basic configuration for the bot
that applies on a global context across all extensions.

## Global Administrators and admin lists {#admin-lists}

The main file contains a list of global administrators for the bot. These users
and roles have full access to all commands and features of the bot, regardless of
the server or extension. This is useful for bot/server owners or trusted users who need
to manage the bot across every function.

Alongside this, every individual extension can also define its own admin lists for more granular
control. These extension-specific admin lists can supplement the global admin list, meaning
that users or roles defined in the global list will also have admin access in the extension alongside
those defined specifically for that extension.

## Other options
### Prefix
The main file also allows you to set a global prefix for the bot commands. This prefix will be used
across all extensions unless overridden by an extension-specific prefix. This is only
for chat-commands, and not for slash commands.
### PluralKit
See more here: [PluralKit Integration](../users/pluralkit)
### Status
You can set a global status for the bot that will be displayed on its profile.
This is mostly for show, the bot does not change its status based on behaviour unless
overridden by a module.
### Util Config
BlossomBridge comes with a set of logging utilities that keep our log format
consistent across modules and other projects. Because of this, it needs two
config values:  
- **Debug Mode:** When enabled, the logging utilities will output debug-level logs at the INFO level of
the specified "Debug" Logger.  
- **Suppress Warns:** When enabled, the logging utilities will not output
any more WARN or ERROR level logs from the specified "Info" or "Debug" loggers. This is useful 
for production environments where you want to minimise log noise.
::: warning
Turning off warns is not recommended unless you are certain you do not want to see
any warning or error logs from the bot. This could lead to missing important
information about the bot's operation.  
We may ask you to enable debug mode or disable warn suppression if you
report issues with the bot, as it helps us diagnose problems.
:::