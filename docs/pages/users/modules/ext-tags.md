# Tags <Badge type="tip" text="KordEx Module" />

Tags are a simple, yet effective way to instantly answer your frequently asked questions,
or to provide information to users quickly without having to explain everything yourself.

## Managing tags
Tags can be managed by using the `/manage-tags` subcommands.
::: info NOTE
Do not confuse `/manage-tags` with `/managetags`, this is a difference
we will soon get into.
:::
When you make a tag, you have to give it a unique key to be identified by as well as a
category. Categories can be reused whereas keys cannot be reused.

When you use the `edit` commands, you can only modify the values with arguments that do
**not** contain "Immutable" in the description. (e.g. the category, but not the key or guild/server)
::: danger
Due to a limitation, you always have to provide `this` to the guild/server argument,
otherwise the tag **will not load** for your server.
:::

## Auto-tags
When a question gets asked so much you start seeing patterns in how people ask it, you 
can set up an auto-tag. This is a feature added by Krafter itself, and therefore has to
use a different command root from the KordEx module.
::: info REMEMBER
`/manage-tags` is for the KordEx tags  
`/managetags` is for the Krafter tags

You can use `/tag` as normal for both cases.
:::

When you make a new auto-tag, you are asked to set the same parameters as for a KordEx
tag, alongside a "trigger" argument. This trigger is a RegEx pattern that gets applied to
every message, if it finds at least one match, it automatically replies to the message
with the provided tag.

::: details Example
Consider the following questions:
> "Where is the IP for the server?"  
> "Where IP?"  
> "ip"  
> "what is the ip"  

You can consider setting up the following RegExp:
```regexp
(\sip[!?.\s]|\sIP[!?.\s])
```
**Breakdown:**  
`()` indicates a matching group.  
`\s` any whitespace character (like spaces and tabs).  
`ip` match literally the characters "i" and "p" in that order.  
`[]` "match any of the following in this set".  
`!?.\s` match literally the characters "!", "?", "." and any whitespace character.  
`|` indicates the boolean OR operator  
`\sIP[!?.\s]` matches the same, but "I" and "P" instead of "i" and "p".  

That last step is especially important, as pattern matching is case-sensitive for
auto-tags.

Examples: (`>><<` indicates the match)  
```
Where is >>ip<<
>>ip?<<
I have such drip
iP
Maybe >>IP?<<
>>IP!<<
pi r squared
piss...
ips!
```
:::
::: danger
Due to a limitation, you always have to provide `this` to the guild/server argument,
otherwise the tag **will not load** for your server.
:::