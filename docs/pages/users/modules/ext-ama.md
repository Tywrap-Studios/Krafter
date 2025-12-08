# AMA
[//]: # (TODO: Add Cozy badge)
AMA stands for "Ask me Anything", and is a common way for developers, community managers, streamers 
and many more to interact with their community and answer questions live.

The module requires some more setup after it has been enabled and configured in the [configuration
files](/config/ext-ama), namely through the use of the `/ama config` command.

## Config Command
You have to further configure your AMA sessions using the `/ama config` command.  
**Available arguments:**
- `answer-queue-channel`: The channel for asked questions to queue up in before response (channel)
- `live-chat-channel`: The channel questions will be sent to when answered by staff (channel)
- `button-channel`: The channel the button for asking questions with is in (channel)
- `approval-queue`: The channel for questions to get sent to, for approval (channel)
- `flagged-question-channel`: The channel for questions flagged by moderators to be sent too (channel)

After this command has been run, you are prompted to fill out a modal form with a header, body and 
image link field. These are used to define what the Ask embed with the button will look like and what it will display.

## Starting and stopping
Once this has been set up, you can start an AMA session using `/ama start` and end it using
`/ama stop`. While the session is up, users can ask questions using `/ama ask` or by using the
embed in the `button-channel`.

### Channels
Before eventually reaching the responders, a question undergoes the following route:  
First it gets sent to `approval-queue`, the approvers have the option to Accept, Deny or Flag the question:
- Allow: sends it to the next station;
- Deny: marks it as not getting processed further;
- Flag: sends it to `flagged-question-channel` (often a moderator channel) for moderation staff
to take a look at. It won't be processed further.

After being allowed, the question gets sent to `answer-queue-channel` where responders
have the option to Claim the question if they are sure they want to answer it. After a responder
has answered the question, they can mark it as answered via Stage or via Text Chat, after which
a message will be sent to `live-chat-channel` in order to notify the people asking questions
that the question has been answered.