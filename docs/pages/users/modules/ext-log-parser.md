# Log Parser
[//]: # (TODO: Add Cozy badge)

The log parser module will look for Minecraft logs and sum up the problems with
it in order to assist users in fixing their Minecraft instance.

::: warning
The log parser currently works best with the Quilt or
Fabric mod loader, (Neo)Forge is quite unsupported and won't always work.
:::
::: info
The bot will fetch logs from the following paste sites, alongside's Discord's CDN:
::: details
- https://0x0.st
- https://bytebin.lucko.me
- https://termbin.com
- https://mclo.gs
- https://past.ee
- https://pastes.dev
- https://hastebin.com
- https://toptal.com/developers/hastebin
- https://hst.sh
- https://gist.github.com
- https://pastebin.com
- https://pastery.net
- https://paste.atlauncher.com
- https://paste.gg
:::

If the parser detects a mod that is against your policies it will abort parsing the log.
This also happens if the user is running in offline mode (often cracked) or is using a
cracked/problematic launcher. If you define a channel in which the logs should be sent, the
parsing will also be aborted if the log was sent in a different channel.