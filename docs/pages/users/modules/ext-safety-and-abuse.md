# Safety and Abuse
This module manages the safety and fends off abuse in your server, the module name is often
shortened to "SAB".

The module comes with a few standard precautions, and is one of the few modules that has
options that are **enabled by default**. We recommend reading through this before changing
values.

## SAB-channel
The SAB-channel is where the bot will log all its moderation-related messages
and try to notify your moderators of any issues in your server. You can set it in your
config, but the channel that will be made automatically may have messed up permissions,
hence why we suggest you configure this value to a channel you already made yourself.

## **Data Collection!!**
The bot framework, [KordEx](https://kordex.dev), collects data in order to improve its development
and provide useful statistics for the developers and the upstream [Kord](https://kord.dev)
developers.

The bot can collect data on the "none", "minimal", "standard" or "extra" level. For more information
on what each level means, see https://docs.kordex.dev/data-collection. You can also consult that
site for other questions related to the data collection.
::: info REMEMBER
We, Tywrap Studios, are not in charge of this collection. Please forward any questions
or concern to the KordEx developers.
:::

By default, the bot collects data on the "standard" level.

::: danger
If you are running this bot for someone else, please make sure they are aware of this 
data collection.  
While the data does not include any PII, and thus technically does not have to conform to GDPR,
the KordEx developers have still left a way for you to manage your data in case you want to.  

The bot has a known issue of spouting the following error:
```log
[ERROR] Failed to submit data
```
We are looking into why this happens, and how to fix it. For now, you can set the level to
"none" in order to circumvent this error.
:::

## Phishing
The bot blocks phishing through a first-party KordEx module that fetches phishing domains
via the open source Sinking Yachts API, you can also define additional links you want
to have blocked using the config.

## Username Moderation
These functions make the bot automatically moderate usernames and "cleanse" them to be 
compliant with your policies. These functions are ran every time someone updates their
username or when they first join.

### Hoisting
Hoisting is the practice of pulling your username up on the members list by adding 
special non-word characters like "!", "?", "." or numbers to the start of your username.
Because some people see this fall under self-promotion, the bot can cleanse usernames from
these hoisting symbols using the following default RegEx pattern:
```regexp
^[^\p{L}]+
```
**Breakdown:**  
`^` matches the start of the string.  
`[]` "match any of the following in this set".  
`^` (in `[]`) means to negate this set, so match anything but the following in the set  
`\p{L}` matches any letter character from any Unicode language (Latin, Chinese, Japanese).  
`+` means to repeat this pattern for the next tokens an unlimited amount of times until 
it no longer matches once.

### Cancerous Symbols
Cancerous symbols are Unicode characters that do not conform to ASCII. You will most prominently
see this in the form of fonts, but also in the form of symbols that "look" like letters.

Often users can use this to make their username stand out more, but moderators or other members
trying to reference the member (let's say, to kick or mention them) can sometimes not find it 
through their display name. 

Because of this, the bot can make use of the [decancer library](https://github.com/null8626/decancer)
in order to cleanse the names from these symbols and replace them with their proper ASCII variants.
::: info NOTE
The decancer software has the tendency to cleanse Russian-like characters into ASCII variants.

We are not planning to fix this.
:::

## Moderation Commands
Enabling this adds a few nice moderation commands like ban, kick, time-out and most prominently
temporary bans. By default, the bot checks for the expiration of these bans every five minutes,
but you can change this in the config.
