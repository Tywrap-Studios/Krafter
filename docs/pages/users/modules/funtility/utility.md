# Utility

The Utility module contains some small, but useful functions and commands, that don't
necessarily fall under one bigger config.

### Thread Commands
Thread commands allow members to manage their thread, like pinning messages
or renaming it. This is also especially useful when the bot makes a thread for
the member, rendering them unable to manage it through base Discord features.

:::tip 
This feature works really well in combination with Krafter's 
[Suggestion module](../ext-suggestion)!
:::

### View JSON
Useful for the developers among us, lets you view the Raw JSON response data of a 
message object through the context menu.

### Say
Allows admins to "say" things as the bot, useful if they need to present something
a little more officially or want to hide their identity from the community.

:::info
When an admin uses this feature, it gets logged in your dedicated 
[SAB-channel](/users/modules/ext-safety-and-abuse#sab-channel)
in order to fend off abuse.
:::

### Welcome Message
Greets new members using a personalised image, with their name
and profile picture, in a welcome channel.

### RSVP
RSVP stands for "répondez s'il vous plaît", a French sentence that translates to
"Respond, please".

This function lets members set up RSVP "events" to which other members can respond and join. When the
timestamp for the event is hit, the bot will ping every user that joined in on the event.

> [!IMPORTANT]
> Some moderation bots (namely [Wick](https://wickbot.com)) filter messages based on how
> many members they ping. Whitelisting your bot from these prevents it from accidentally
> being acted upon by these moderation bots.

### Reminder
The reminder function is somewhat close to the RSVP function, but it also allows
you to be DM'd by the bot instead of being pinged publicly.

In short, you can set up reminders to be reminded of something important after a set
amount of time, or after a timestamp has been passed. Alongside this you can set it to 
repeat the reminder until cancelled.
::: info
Regardless of if you have been contacted in DMs, the bot will still send out
a message with the reminder in the channel you set it up on.
:::
::: tip
The bot will calculate the difference between now and your timestamp to determine
a duration for the repetition. So don't feel forced to calculate that
yourself.
:::

### Sticky
The sticky functions lets admins "stick" messages to the bottom of channels. This can be used
if you need to temporarily make your users very aware of something. 

The bot may occasionally
not delete its past message if the message below it is from itself. (This is a safety measure
put in place to ensure the bot does not spam the stuck message after itself.)
The message will always be prefixed with the pin emoji (`📌`) to remind users and admins
that this is a stuck message and not something else random.