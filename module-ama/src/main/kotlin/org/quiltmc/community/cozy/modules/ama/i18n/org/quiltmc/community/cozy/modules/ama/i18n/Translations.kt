package org.quiltmc.community.cozy.modules.ama.i18n

import dev.kordex.core.i18n.types.Bundle
import dev.kordex.core.i18n.types.Key

public object Translations {
  public val bundle: Bundle = Bundle("ama.strings")

  public object Args {
    public object Ama {
      public object Config {
        /**
         * answer-queue-channel
         */
        public val answerQueueChannel: Key = Key("args.ama.config.answerQueueChannel")
            .withBundle(Translations.bundle)

        /**
         * approval-queue
         */
        public val approvalQueue: Key = Key("args.ama.config.approvalQueue")
            .withBundle(Translations.bundle)

        /**
         * button-channel
         */
        public val buttonChannel: Key = Key("args.ama.config.buttonChannel")
            .withBundle(Translations.bundle)

        /**
         * flagged-question-channel
         */
        public val flaggedQuestionChannel: Key = Key("args.ama.config.flaggedQuestionChannel")
            .withBundle(Translations.bundle)

        /**
         * live-chat-channel
         */
        public val liveChatChannel: Key = Key("args.ama.config.liveChatChannel")
            .withBundle(Translations.bundle)

        public object AnswerQueueChannel {
          /**
           * The channel for asked questions to queue up in before response
           */
          public val description: Key = Key("args.ama.config.answerQueueChannel.description")
              .withBundle(Translations.bundle)
        }

        public object ApprovalQueue {
          /**
           * The channel for questions to get sent to, for approval
           */
          public val description: Key = Key("args.ama.config.approvalQueue.description")
              .withBundle(Translations.bundle)
        }

        public object ButtonChannel {
          /**
           * The channel the button for asking questions with is in
           */
          public val description: Key = Key("args.ama.config.buttonChannel.description")
              .withBundle(Translations.bundle)
        }

        public object FlaggedQuestionChannel {
          /**
           * The channel for questions flagged by moderators to be sent too
           */
          public val description: Key = Key("args.ama.config.flaggedQuestionChannel.description")
              .withBundle(Translations.bundle)
        }

        public object LiveChatChannel {
          /**
           * The channel questions will be sent to when answered by staff
           */
          public val description: Key = Key("args.ama.config.liveChatChannel.description")
              .withBundle(Translations.bundle)
        }
      }
    }
  }

  public object Buttons {
    public object Label {
      /**
       * Accept
       */
      public val accept: Key = Key("buttons.label.accept")
          .withBundle(Translations.bundle)

      /**
       * Ask a question
       */
      public val ask: Key = Key("buttons.label.ask")
          .withBundle(Translations.bundle)

      /**
       * Claim
       */
      public val claim: Key = Key("buttons.label.claim")
          .withBundle(Translations.bundle)

      /**
       * Deny
       */
      public val deny: Key = Key("buttons.label.deny")
          .withBundle(Translations.bundle)

      /**
       * Flag
       */
      public val flag: Key = Key("buttons.label.flag")
          .withBundle(Translations.bundle)

      /**
       * Skip
       */
      public val skip: Key = Key("buttons.label.skip")
          .withBundle(Translations.bundle)

      /**
       * Stage
       */
      public val stage: Key = Key("buttons.label.stage")
          .withBundle(Translations.bundle)

      /**
       * Text
       */
      public val text: Key = Key("buttons.label.text")
          .withBundle(Translations.bundle)
    }
  }

  public object Commands {
    /**
     * ama
     */
    public val ama: Key = Key("commands.ama")
        .withBundle(Translations.bundle)

    /**
     * ask
     */
    public val ask: Key = Key("commands.ask")
        .withBundle(Translations.bundle)

    public object Ama {
      /**
       * config
       */
      public val config: Key = Key("commands.ama.config")
          .withBundle(Translations.bundle)

      /**
       * The command for using AMA
       */
      public val description: Key = Key("commands.ama.description")
          .withBundle(Translations.bundle)

      /**
       * start
       */
      public val start: Key = Key("commands.ama.start")
          .withBundle(Translations.bundle)

      /**
       * stop
       */
      public val stop: Key = Key("commands.ama.stop")
          .withBundle(Translations.bundle)

      public object Config {
        /**
         * Configure your AMA settings
         */
        public val description: Key = Key("commands.ama.config.description")
            .withBundle(Translations.bundle)
      }

      public object Start {
        /**
         * Start the AMA for this server
         */
        public val description: Key = Key("commands.ama.start.description")
            .withBundle(Translations.bundle)
      }

      public object Stop {
        /**
         * Stop the AMA for this server
         */
        public val description: Key = Key("commands.ama.stop.description")
            .withBundle(Translations.bundle)
      }
    }

    public object Ask {
      /**
       * Ask a question for the current AMA
       */
      public val description: Key = Key("commands.ask.description")
          .withBundle(Translations.bundle)
    }
  }

  public object Modal {
    public object Ask {
      /**
       * Ask a question
       */
      public val title: Key = Key("modal.ask.title")
          .withBundle(Translations.bundle)

      public object PkId {
        /**
         * PluralKit Member (Optional)
         */
        public val label: Key = Key("modal.ask.pkId.label")
            .withBundle(Translations.bundle)

        /**
         * Either a member name, ID, or UUID
         */
        public val placeholder: Key = Key("modal.ask.pkId.placeholder")
            .withBundle(Translations.bundle)
      }

      public object Question {
        /**
         * Question
         */
        public val label: Key = Key("modal.ask.question.label")
            .withBundle(Translations.bundle)

        /**
         * What are you doing today?
         */
        public val placeholder: Key = Key("modal.ask.question.placeholder")
            .withBundle(Translations.bundle)
      }
    }

    public object Config {
      /**
       * Configure your AMA Session
       */
      public val title: Key = Key("modal.config.title")
          .withBundle(Translations.bundle)

      public object Body {
        /**
         * AMA Embed body
         */
        public val label: Key = Key("modal.config.body.label")
            .withBundle(Translations.bundle)

        /**
         * Ask me any question you can think of!
         */
        public val placeholder: Key = Key("modal.config.body.placeholder")
            .withBundle(Translations.bundle)
      }

      public object Header {
        /**
         * AMA Embed Title
         */
        public val label: Key = Key("modal.config.header.label")
            .withBundle(Translations.bundle)

        /**
         * Ask me anything session!
         */
        public val placeholder: Key = Key("modal.config.header.placeholder")
            .withBundle(Translations.bundle)
      }

      public object Image {
        /**
         * AMA Embed image
         */
        public val label: Key = Key("modal.config.image.label")
            .withBundle(Translations.bundle)

        /**
         * https://i.imgur.com/yRgfdVJ.png
         */
        public val placeholder: Key = Key("modal.config.image.placeholder")
            .withBundle(Translations.bundle)
      }
    }
  }
}
