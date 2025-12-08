# Introduction

Welcome! One of the big features of Krafter is the ability to customise and configure
a lot about your instance of the bot. This is done through countless extension-specific
configuration files in `JSON5` format.

The configuration loading is handled by the [BlossomBridge API](/../BlossomBridge), so any
issues regarding **config and logging** should be directed to that issue tracker!

Before you dive deep into configuring the software to your needs, we want to attract
your attention to a few householding points.

## General Householding
Again, the configuration loading is handled by our API, [BlossomBridge](/../BlossomBridge), any
issues about configuration failing to load should be sent
[here](https://github.com/Tywrap-Studios/BlossomBridge/issues).

You may have issues finding exactly where these files are and how to edit them,
for that, please continue to the [next page](./where-are-they) for an extended explanation after you've
finished this one. For those who understand Docker a bit better: check your volumes.

The config files are `JSON5` files (`.json5`), and are in the `config` directory. When
you edit these files there are a few things you can remember:
1. JSON5 supports "trailing" commas, when you have long lists, feel free to leave
an extra comma `,`, it won't fail.
2. JSON5 supports comments, and we sure made use of that. Every value should have a 
proper, short description of what it does. If you're ever unsure, check out these
config guides or the [user guides](../users/getting-started).
3. Everything (except for some SAB functionality) is turned off by default, we suggest
you run the bot once to load the default configs, exit, and then start it back up again once
you've configured the software to your needs.
4. Due to a technical limitation, you are not able to hotswap configs, every config change
needs a complete restart of the run before it is applied again. It is suggested to batch as
many changes as you can into one restart in order to not have to restart as often.