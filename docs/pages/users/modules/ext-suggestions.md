# Suggestions
[//]: # (TODO: Add Cozy badge)
The Suggestions module lets you set up a Suggestions Forum for your member
to suggest things in. It comes packed with a lot of utilities to properly
manage them and can be especially nice if you're running a community that
gives a lot of feedback.
:::tip
This module works great with the [thread commands](./funtility/utility#thread-commands) utility!
:::

## The Channel
> [!IMPORTANT]
> Contrary to what the name "Suggestions Forum" may imply, this channel
> is still a bare text channel. Not a Discord forum.

The suggestions channel contains all the suggestions, the discussion threads for them
and most prominently a button to make a new suggestion.

## Auto-answers
Your community might suggest things that that get asked for a lot, or there are simply
features you do not wish to implement. For this reason you are able to set up so-called
"Auto-answers", these checks will be applied to every suggestion, and if they match 
with the suggestion it will be automatically closed with your custom response attached.

### Format
The auto-answers are added via config, view more about the format [there](/config/ext-suggestions).

::: tip The suggestion can be given one of the following statuses, this is what they should represent:
- **Open:** The suggestion is open and is still waiting to be reviewed;  
- **Requires Name:** The suggestion has a bad or empty name, and should receive (a better) one;  
- **Approved:** The suggestion is approved and will be taken into account;  
- **Denied:** The suggestion is denied and won't be taken into account;  
- **Invalid:** The suggestion is invalid and won't be taken into account until a new, better
one is made;  
- **Spam:** This suggestion has been made too many times over and over again and won't 
be taken into account because of it;  
- **Future:** The suggestion is valid, but is a future concern and will therefore not be taken into
account _at the moment_, and will instead be looked at later;  
- **Stale:** The suggestion is too simple and not yet refined enough to be considered;  
- **Duplicate:** Someone has already suggested this;  
- **Implemented:** The suggestion is already implemented.
:::