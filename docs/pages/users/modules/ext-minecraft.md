# Minecraft

The Minecraft module provides various functions related to our favoured blocky game.

## Status
The status functions allow you to look up the status of a server, but also track servers
and display their status through the names of private Discord Voice Channels.

If you select to use one channel, the format will be as follows:
```
ONLINE:
 <Server Name>: <Online Players>/<Max Players>
 
OFFLINE:
 <Server Name>: Offline
 
UNAVAILABLE:
 <Server Name>: Unavailable
```
If you select to use two channels, the format will be as follows:
```
ONLINE:
 <Server Name>: Online
 <Online Players> / <Max Players> players
 
OFFLINE:
 <Server Name>: Offline
 No players
 
UNAVAILABLE:
 <Server Name>: Unavailable
 Unavailable
```

## Connection
The connection function maintains a direct connection to a Minecraft server you
run. This can be done through two means:

#### The RCON protocol
Minecraft has a [built-in RCON](https://minecraft.wiki/w/RCON) (Remote Console) implementation,
following the 
[Source RCON Protocol](https://developer.valvesoftware.com/wiki/Source_RCON_Protocol).
When you set up the correct credentials and address in the config, the bot will use RCON
to perform its tasks.

> [!IMPORTANT]
> This value is compulsory, meaning you HAVE to provide it. The bot will deliberately
> crash if any of these values are left empty.
> 
> The reason this happens is that the RCON protocol is seen as a "last resort" option
> for the bot to use in case the other protocols are not implemented or configured.

#### The CTD protocol
::: danger
This section talks about a software that is conceptual and not yet available.
:::
The CTD protocol is a conceptual protocol to be based on JSON-RPC. Mojang has shown they are 
able to use JSON-RPC efficiently with their new server management protocols and Tywrap Studios
wants to improve the traditional Minecraft RCON protocol, susceptible to many flaws and prone
to a lot of attacks and failure, in order to make external, trusted connections with your
server safer and less of a hassle.

### Chat Connection
The bot can maintain a chat connection between Discord → Minecraft, by watching
over a channel and sending any incoming messages to the server via the selected
protocol.
::: tip
Want a way to link Minecraft → Discord? Check out our mod Chat To Discord over
on [Modrinth](https://modrinth.com/mod/chat-to-discord)! Just like Krafter, 
it's free, open-source, easy to set up and quite configurable. On top of this,
it runs fully server-side, your members don't need to install it.
:::

### Console Connection
The console connections allows your admins and members to run commands on the console
via Discord `/cmd` commands. The `/cmd` group provides a couple of preset commands
members can make use of, but a `/cmd run` command allows admins to run any and all
commands.
::: warning
This function can be quite dangerous to give access to, as everything is run as console.  
We are looking into ways to improve safety on this function.
:::

## Linking
The linking function allows members to link their Minecraft UUID to their Discord user.

This function isn't entirely refined yet, we are looking into ways to improve the following:
- Verification, at the moment the only way to "verify" members is by "Force Linking" them as
  an admin.
- Utility, the bot does not provide a lot of utility apart from allowing users to
  look up other users' profiles and making use of the username in the chat connection.

## Profile Utilities
Adds a couple of utility commands and functions that help with Minecraft profiles,
such as letting you look up profiles by UUID or username.
