package org.tywrapstudios.krafter.i18n

import dev.kordex.core.i18n.types.Bundle
import dev.kordex.core.i18n.types.Key

public object Translations {
  public val bundle: Bundle = Bundle("krafter.strings")

  /**
   * Placeholder
   */
  public val placeholder: Key = Key("placeholder")
      .withBundle(Translations.bundle)

  public object Args {
    public object Bean {
      /**
       * target
       */
      public val target: Key = Key("args.bean.target")
          .withBundle(Translations.bundle)

      public object Target {
        /**
         * Your target...
         */
        public val description: Key = Key("args.bean.target.description")
            .withBundle(Translations.bundle)
      }
    }

    public object Cmd {
      public object Maintenance {
        /**
         * enable
         */
        public val enable: Key = Key("args.cmd.maintenance.enable")
            .withBundle(Translations.bundle)

        public object Enable {
          /**
           * Whether to enable or disable maintenance mode.
           */
          public val description: Key = Key("args.cmd.maintenance.enable.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Mclogs {
        /**
         * log
         */
        public val log: Key = Key("args.cmd.mclogs.log")
            .withBundle(Translations.bundle)

        public object Log {
          /**
           * The log file to request for mclo.gs
           */
          public val description: Key = Key("args.cmd.mclogs.log.description")
              .withBundle(Translations.bundle)
        }
      }

      public object ModDamage {
        /**
         * amount
         */
        public val amount: Key = Key("args.cmd.modDamage.amount")
            .withBundle(Translations.bundle)

        public object Amount {
          /**
           * The amount of damage to deal to the player.
           */
          public val description: Key = Key("args.cmd.modDamage.amount.description")
              .withBundle(Translations.bundle)
        }
      }

      public object ModTpOffline {
        /**
         * location
         */
        public val location: Key = Key("args.cmd.modTpOffline.location")
            .withBundle(Translations.bundle)

        /**
         * x
         */
        public val x: Key = Key("args.cmd.modTpOffline.x")
            .withBundle(Translations.bundle)

        /**
         * y
         */
        public val y: Key = Key("args.cmd.modTpOffline.y")
            .withBundle(Translations.bundle)

        /**
         * z
         */
        public val z: Key = Key("args.cmd.modTpOffline.z")
            .withBundle(Translations.bundle)

        public object Location {
          /**
           * The location to teleport the player to.
           */
          public val description: Key = Key("args.cmd.modTpOffline.location.description")
              .withBundle(Translations.bundle)
        }

        public object X {
          /**
           * The x coordinate to teleport the player to.
           */
          public val description: Key = Key("args.cmd.modTpOffline.x.description")
              .withBundle(Translations.bundle)
        }

        public object Y {
          /**
           * The y coordinate to teleport the player to.
           */
          public val description: Key = Key("args.cmd.modTpOffline.y.description")
              .withBundle(Translations.bundle)
        }

        public object Z {
          /**
           * The z coordinate to teleport the player to.
           */
          public val description: Key = Key("args.cmd.modTpOffline.z.description")
              .withBundle(Translations.bundle)
        }
      }

      public object ModWhitelist {
        /**
         * enable
         */
        public val enable: Key = Key("args.cmd.modWhitelist.enable")
            .withBundle(Translations.bundle)

        public object Enable {
          /**
           * Whether to enable or disable the whitelist.
           */
          public val description: Key = Key("args.cmd.modWhitelist.enable.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Run {
        /**
         * command
         */
        public val command: Key = Key("args.cmd.run.command")
            .withBundle(Translations.bundle)

        public object Command {
          /**
           * The command to run on the Minecraft server
           */
          public val description: Key = Key("args.cmd.run.command.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Tellraw {
        /**
         * message
         */
        public val message: Key = Key("args.cmd.tellraw.message")
            .withBundle(Translations.bundle)

        public object Message {
          /**
           * The tellraw message to send to the server in JSON format
           */
          public val description: Key = Key("args.cmd.tellraw.message.description")
              .withBundle(Translations.bundle)
        }
      }
    }

    public object Minecraft {
      public object ForceLink {
        /**
         * user
         */
        public val member: Key = Key("args.minecraft.forceLink.member")
            .withBundle(Translations.bundle)

        /**
         * uuid
         */
        public val uuid: Key = Key("args.minecraft.forceLink.uuid")
            .withBundle(Translations.bundle)

        public object Member {
          /**
           * The member to link the Minecraft account to.
           */
          public val description: Key = Key("args.minecraft.forceLink.member.description")
              .withBundle(Translations.bundle)
        }

        public object Uuid {
          /**
           * The UUID of the Minecraft account to link.
           */
          public val description: Key = Key("args.minecraft.forceLink.uuid.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Link {
        /**
         * uuid
         */
        public val uuid: Key = Key("args.minecraft.link.uuid")
            .withBundle(Translations.bundle)

        public object Uuid {
          /**
           * The UUID of the Minecraft account you want to link.
           */
          public val description: Key = Key("args.minecraft.link.uuid.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Lookup {
        /**
         * user
         */
        public val member: Key = Key("args.minecraft.lookup.member")
            .withBundle(Translations.bundle)

        public object Member {
          /**
           * The member to look up the linked Minecraft account for.
           */
          public val description: Key = Key("args.minecraft.lookup.member.description")
              .withBundle(Translations.bundle)
        }
      }

      public object SearchUsername {
        /**
         * username
         */
        public val username: Key = Key("args.minecraft.searchUsername.username")
            .withBundle(Translations.bundle)

        public object Username {
          /**
           * The username to search with.
           */
          public val description: Key = Key("args.minecraft.searchUsername.username.description")
              .withBundle(Translations.bundle)
        }
      }

      public object SearchUuid {
        /**
         * uuid
         */
        public val uuid: Key = Key("args.minecraft.searchUuid.uuid")
            .withBundle(Translations.bundle)

        public object Uuid {
          /**
           * The UUID to search with.
           */
          public val description: Key = Key("args.minecraft.searchUuid.uuid.description")
              .withBundle(Translations.bundle)
        }
      }
    }

    public object Rsvp {
      /**
       * event-description
       */
      public val eventDescription: Key = Key("args.rsvp.eventDescription")
          .withBundle(Translations.bundle)

      /**
       * event-name
       */
      public val eventName: Key = Key("args.rsvp.eventName")
          .withBundle(Translations.bundle)

      /**
       * event-time
       */
      public val eventTime: Key = Key("args.rsvp.eventTime")
          .withBundle(Translations.bundle)

      public object EventDescription {
        /**
         * A description of the event.
         */
        public val description: Key = Key("args.rsvp.eventDescription.description")
            .withBundle(Translations.bundle)
      }

      public object EventName {
        /**
         * The name of the event.
         */
        public val description: Key = Key("args.rsvp.eventName.description")
            .withBundle(Translations.bundle)
      }

      public object EventTime {
        /**
         * Date and time of the event. Format: YYYY-MM-DD HH:mm (24-hour time, UTC)
         */
        public val description: Key = Key("args.rsvp.eventTime.description")
            .withBundle(Translations.bundle)
      }
    }

    public object Suggestions {
      public object Edit {
        /**
         * problem
         */
        public val problem: Key = Key("args.suggestions.edit.problem")
            .withBundle(Translations.bundle)

        /**
         * solution
         */
        public val solution: Key = Key("args.suggestions.edit.solution")
            .withBundle(Translations.bundle)

        /**
         * text
         */
        public val text: Key = Key("args.suggestions.edit.text")
            .withBundle(Translations.bundle)

        public object Problem {
          /**
           * New problem text.
           */
          public val description: Key = Key("args.suggestions.edit.problem.description")
              .withBundle(Translations.bundle)
        }

        public object Solution {
          /**
           * New solution text.
           */
          public val description: Key = Key("args.suggestions.edit.solution.description")
              .withBundle(Translations.bundle)
        }

        public object Text {
          /**
           * New suggestion text.
           */
          public val description: Key = Key("args.suggestions.edit.text.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Manage {
        public object AutoResponse {
          /**
           * id
           */
          public val id: Key = Key("args.suggestions.manage.autoResponse.id")
              .withBundle(Translations.bundle)

          public object Id {
            /**
             * Auto response ID
             */
            public val description: Key = Key("args.suggestions.manage.autoResponse.id.description")
                .withBundle(Translations.bundle)
          }
        }

        public object State {
          /**
           * comment
           */
          public val comment: Key = Key("args.suggestions.manage.state.comment")
              .withBundle(Translations.bundle)

          public object Comment {
            /**
             * Comment text to set, 'clear' to remove.
             */
            public val description: Key = Key("args.suggestions.manage.state.comment.description")
                .withBundle(Translations.bundle)
          }
        }
      }
    }

    public object Utility {
      public object Say {
        /**
         * message
         */
        public val message: Key = Key("args.utility.say.message")
            .withBundle(Translations.bundle)

        /**
         * target
         */
        public val target: Key = Key("args.utility.say.target")
            .withBundle(Translations.bundle)

        public object Message {
          /**
           * Message to send
           */
          public val description: Key = Key("args.utility.say.message.description")
              .withBundle(Translations.bundle)
        }

        public object Target {
          /**
           * Channel to use, if not this one
           */
          public val description: Key = Key("args.utility.say.target.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Thread {
        public object Archive {
          /**
           * lock
           */
          public val lock: Key = Key("args.utility.thread.archive.lock")
              .withBundle(Translations.bundle)

          public object Lock {
            /**
             * Whether to lock the thread, if you're staff - defaults to false
             */
            public val description: Key = Key("args.utility.thread.archive.lock.description")
                .withBundle(Translations.bundle)
          }
        }

        public object PinMessage {
          /**
           * message
           */
          public val message: Key = Key("args.utility.thread.pinMessage.message")
              .withBundle(Translations.bundle)

          public object Message {
            /**
             * Message link or ID to pin/unpin
             */
            public val description: Key = Key("args.utility.thread.pinMessage.message.description")
                .withBundle(Translations.bundle)
          }
        }

        public object Rename {
          /**
           * name
           */
          public val name: Key = Key("args.utility.thread.rename.name")
              .withBundle(Translations.bundle)

          public object Name {
            /**
             * Name to give the current thread
             */
            public val description: Key = Key("args.utility.thread.rename.name.description")
                .withBundle(Translations.bundle)
          }
        }

        public object SetOwner {
          /**
           * user
           */
          public val user: Key = Key("args.utility.thread.setOwner.user")
              .withBundle(Translations.bundle)

          public object User {
            /**
             * User to set as the owner of the thread
             */
            public val description: Key = Key("args.utility.thread.setOwner.user.description")
                .withBundle(Translations.bundle)
          }
        }
      }
    }
  }

  public object Checks {
    public object HasId {
      /**
       * Must have id: **{0}**
       */
      public val failed: Key = Key("checks.hasId.failed")
          .withBundle(Translations.bundle)
    }

    public object IsBotModuleAdmin {
      /**
       * Must be bot module admin for **{0}**
       */
      public val failed: Key = Key("checks.isBotModuleAdmin.failed")
          .withBundle(Translations.bundle)
    }

    public object NotHasId {
      /**
       * Must not have id: **{0}**
       */
      public val failed: Key = Key("checks.notHasId.failed")
          .withBundle(Translations.bundle)
    }

    public object NotIsBotModuleAdmin {
      /**
       * Must not be bot module admin for **{0}**
       */
      public val failed: Key = Key("checks.notIsBotModuleAdmin.failed")
          .withBundle(Translations.bundle)
    }
  }

  public object Commands {
    /**
     * bean
     */
    public val bean: Key = Key("commands.bean")
        .withBundle(Translations.bundle)

    /**
     * cmd
     */
    public val cmd: Key = Key("commands.cmd")
        .withBundle(Translations.bundle)

    /**
     * minecraft
     */
    public val minecraft: Key = Key("commands.minecraft")
        .withBundle(Translations.bundle)

    /**
     * rsvp
     */
    public val rsvp: Key = Key("commands.rsvp")
        .withBundle(Translations.bundle)

    /**
     * suggestions
     */
    public val suggestions: Key = Key("commands.suggestions")
        .withBundle(Translations.bundle)

    public object Bean {
      /**
       * Bean.
       */
      public val description: Key = Key("commands.bean.description")
          .withBundle(Translations.bundle)
    }

    public object Cmd {
      /**
       * Commands to run commands via a Minecraft server connection.
       */
      public val description: Key = Key("commands.cmd.description")
          .withBundle(Translations.bundle)

      /**
       * list
       */
      public val list: Key = Key("commands.cmd.list")
          .withBundle(Translations.bundle)

      /**
       * maintenance
       */
      public val maintenance: Key = Key("commands.cmd.maintenance")
          .withBundle(Translations.bundle)

      /**
       * mclogs
       */
      public val mclogs: Key = Key("commands.cmd.mclogs")
          .withBundle(Translations.bundle)

      /**
       * ban
       */
      public val modBan: Key = Key("commands.cmd.modBan")
          .withBundle(Translations.bundle)

      /**
       * clear
       */
      public val modClear: Key = Key("commands.cmd.modClear")
          .withBundle(Translations.bundle)

      /**
       * damage
       */
      public val modDamage: Key = Key("commands.cmd.modDamage")
          .withBundle(Translations.bundle)

      /**
       * heal
       */
      public val modHeal: Key = Key("commands.cmd.modHeal")
          .withBundle(Translations.bundle)

      /**
       * kick
       */
      public val modKick: Key = Key("commands.cmd.modKick")
          .withBundle(Translations.bundle)

      /**
       * whitelist-list
       */
      public val modListWhitelist: Key = Key("commands.cmd.modListWhitelist")
          .withBundle(Translations.bundle)

      /**
       * mute
       */
      public val modMute: Key = Key("commands.cmd.modMute")
          .withBundle(Translations.bundle)

      /**
       * pardon
       */
      public val modPardon: Key = Key("commands.cmd.modPardon")
          .withBundle(Translations.bundle)

      /**
       * restore
       */
      public val modRestore: Key = Key("commands.cmd.modRestore")
          .withBundle(Translations.bundle)

      /**
       * tempmute
       */
      public val modTempMute: Key = Key("commands.cmd.modTempMute")
          .withBundle(Translations.bundle)

      /**
       * tp-offline
       */
      public val modTpOffline: Key = Key("commands.cmd.modTpOffline")
          .withBundle(Translations.bundle)

      /**
       * unban
       */
      public val modUnban: Key = Key("commands.cmd.modUnban")
          .withBundle(Translations.bundle)

      /**
       * unmute
       */
      public val modUnmute: Key = Key("commands.cmd.modUnmute")
          .withBundle(Translations.bundle)

      /**
       * whitelist
       */
      public val modWhitelist: Key = Key("commands.cmd.modWhitelist")
          .withBundle(Translations.bundle)

      /**
       * run
       */
      public val run: Key = Key("commands.cmd.run")
          .withBundle(Translations.bundle)

      /**
       * tellraw
       */
      public val tellraw: Key = Key("commands.cmd.tellraw")
          .withBundle(Translations.bundle)

      /**
       * tempban
       */
      public val tempban: Key = Key("commands.cmd.tempban")
          .withBundle(Translations.bundle)

      /**
       * view-balance
       */
      public val viewBalance: Key = Key("commands.cmd.viewBalance")
          .withBundle(Translations.bundle)

      public object List {
        /**
         * Lists all current players.
         */
        public val description: Key = Key("commands.cmd.list.description")
            .withBundle(Translations.bundle)
      }

      public object Maintenance {
        /**
         * Enable or disable maintenance mode on the Minecraft server.
         */
        public val description: Key = Key("commands.cmd.maintenance.description")
            .withBundle(Translations.bundle)
      }

      public object Mclogs {
        /**
         * Sends a request to mclo.gs for the latest.log, or a log you specify.
         */
        public val description: Key = Key("commands.cmd.mclogs.description")
            .withBundle(Translations.bundle)
      }

      public object ModBan {
        /**
         * Bans the selected player from the server.
         */
        public val description: Key = Key("commands.cmd.modBan.description")
            .withBundle(Translations.bundle)
      }

      public object ModClear {
        /**
         * Clears a player's inventory.
         */
        public val description: Key = Key("commands.cmd.modClear.description")
            .withBundle(Translations.bundle)
      }

      public object ModDamage {
        /**
         * Damages a player by a specified amount.
         */
        public val description: Key = Key("commands.cmd.modDamage.description")
            .withBundle(Translations.bundle)
      }

      public object ModHeal {
        /**
         * Heals a player to full health.
         */
        public val description: Key = Key("commands.cmd.modHeal.description")
            .withBundle(Translations.bundle)
      }

      public object ModKick {
        /**
         * Kicks the selected player from the server.
         */
        public val description: Key = Key("commands.cmd.modKick.description")
            .withBundle(Translations.bundle)
      }

      public object ModListWhitelist {
        /**
         * List all players on the whitelist.
         */
        public val description: Key = Key("commands.cmd.modListWhitelist.description")
            .withBundle(Translations.bundle)
      }

      public object ModMute {
        /**
         * Mutes the selected player on the server.
         */
        public val description: Key = Key("commands.cmd.modMute.description")
            .withBundle(Translations.bundle)
      }

      public object ModPardon {
        /**
         * Pardons the selected player on the server.
         */
        public val description: Key = Key("commands.cmd.modPardon.description")
            .withBundle(Translations.bundle)
      }

      public object ModRestore {
        /**
         * Restores a player's grave.
         */
        public val description: Key = Key("commands.cmd.modRestore.description")
            .withBundle(Translations.bundle)
      }

      public object ModTempMute {
        /**
         * Temporarily mutes the selected player on the server.
         */
        public val description: Key = Key("commands.cmd.modTempMute.description")
            .withBundle(Translations.bundle)
      }

      public object ModTpOffline {
        /**
         * Teleports an offline player.
         */
        public val description: Key = Key("commands.cmd.modTpOffline.description")
            .withBundle(Translations.bundle)
      }

      public object ModUnban {
        /**
         * Unbans the selected player from the server
         */
        public val description: Key = Key("commands.cmd.modUnban.description")
            .withBundle(Translations.bundle)
      }

      public object ModUnmute {
        /**
         * Unmutes the selected player on the server.
         */
        public val description: Key = Key("commands.cmd.modUnmute.description")
            .withBundle(Translations.bundle)
      }

      public object ModWhitelist {
        /**
         * whitelist-add
         */
        public val add: Key = Key("commands.cmd.modWhitelist.add")
            .withBundle(Translations.bundle)

        /**
         * Toggle the whitelist
         */
        public val description: Key = Key("commands.cmd.modWhitelist.description")
            .withBundle(Translations.bundle)

        /**
         * whitelist-remove
         */
        public val remove: Key = Key("commands.cmd.modWhitelist.remove")
            .withBundle(Translations.bundle)

        public object Add {
          /**
           * Add a player to the whitelist.
           */
          public val description: Key = Key("commands.cmd.modWhitelist.add.description")
              .withBundle(Translations.bundle)
        }

        public object Remove {
          /**
           * Remove a player from the whitelist.
           */
          public val description: Key = Key("commands.cmd.modWhitelist.remove.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Run {
        /**
         * Run any command on the Minecraft server
         */
        public val description: Key = Key("commands.cmd.run.description")
            .withBundle(Translations.bundle)
      }

      public object Tellraw {
        /**
         * Send a tellraw message to the server
         */
        public val description: Key = Key("commands.cmd.tellraw.description")
            .withBundle(Translations.bundle)
      }

      public object Tempban {
        /**
         * Temporarily bans the selected player from the server.
         */
        public val description: Key = Key("commands.cmd.tempban.description")
            .withBundle(Translations.bundle)
      }

      public object ViewBalance {
        /**
         * View the balance of a player.
         */
        public val description: Key = Key("commands.cmd.viewBalance.description")
            .withBundle(Translations.bundle)
      }
    }

    public object ManageTags {
      /**
       * edit-auto
       */
      public val editAuto: Key = Key("commands.manageTags.editAuto")
          .withBundle(Translations.bundle)

      /**
       * set-auto
       */
      public val setAuto: Key = Key("commands.manageTags.setAuto")
          .withBundle(Translations.bundle)

      public object EditAuto {
        /**
         * Edit an existing auto-tag
         */
        public val description: Key = Key("commands.manageTags.editAuto.description")
            .withBundle(Translations.bundle)
      }

      public object SetAuto {
        /**
         * Create or replace an auto-tag
         */
        public val description: Key = Key("commands.manageTags.setAuto.description")
            .withBundle(Translations.bundle)
      }
    }

    public object Minecraft {
      /**
       * Minecraft related commands.
       */
      public val description: Key = Key("commands.minecraft.description")
          .withBundle(Translations.bundle)

      /**
       * force-link
       */
      public val forceLink: Key = Key("commands.minecraft.forceLink")
          .withBundle(Translations.bundle)

      /**
       * link
       */
      public val link: Key = Key("commands.minecraft.link")
          .withBundle(Translations.bundle)

      /**
       * lookup
       */
      public val lookup: Key = Key("commands.minecraft.lookup")
          .withBundle(Translations.bundle)

      /**
       * search-username
       */
      public val searchUsername: Key = Key("commands.minecraft.searchUsername")
          .withBundle(Translations.bundle)

      /**
       * search-uuid
       */
      public val searchUuid: Key = Key("commands.minecraft.searchUuid")
          .withBundle(Translations.bundle)

      /**
       * unlink
       */
      public val unlink: Key = Key("commands.minecraft.unlink")
          .withBundle(Translations.bundle)

      public object ForceLink {
        /**
         * Force link a Minecraft account to a Discord account.
         */
        public val description: Key = Key("commands.minecraft.forceLink.description")
            .withBundle(Translations.bundle)
      }

      public object Link {
        /**
         * Link your Minecraft account to your Discord account.
         */
        public val description: Key = Key("commands.minecraft.link.description")
            .withBundle(Translations.bundle)
      }

      public object Lookup {
        /**
         * Lookup the Minecraft account linked to a Discord account.
         */
        public val description: Key = Key("commands.minecraft.lookup.description")
            .withBundle(Translations.bundle)
      }

      public object SearchUsername {
        /**
         * Search a Minecraft profile using its username. Note: cracked accounts won't be able to
         * use this!
         */
        public val description: Key = Key("commands.minecraft.searchUsername.description")
            .withBundle(Translations.bundle)
      }

      public object SearchUuid {
        /**
         * Search a Minecraft profile using a UUID.
         */
        public val description: Key = Key("commands.minecraft.searchUuid.description")
            .withBundle(Translations.bundle)
      }

      public object Unlink {
        /**
         * Unlink your Minecraft account from your Discord account.
         */
        public val description: Key = Key("commands.minecraft.unlink.description")
            .withBundle(Translations.bundle)
      }
    }

    public object Rsvp {
      /**
       * Create an event people can RSVP to.
       */
      public val description: Key = Key("commands.rsvp.description")
          .withBundle(Translations.bundle)
    }

    public object Suggestions {
      /**
       * Commands for managing the suggestion forum.
       */
      public val description: Key = Key("commands.suggestions.description")
          .withBundle(Translations.bundle)

      /**
       * edit
       */
      public val edit: Key = Key("commands.suggestions.edit")
          .withBundle(Translations.bundle)

      /**
       * suggestions-refresh
       */
      public val refresh: Key = Key("commands.suggestions.refresh")
          .withBundle(Translations.bundle)

      /**
       * spreadsheet
       */
      public val spreadsheet: Key = Key("commands.suggestions.spreadsheet")
          .withBundle(Translations.bundle)

      public object Edit {
        /**
         * Edit one of your suggestions.
         */
        public val description: Key = Key("commands.suggestions.edit.description")
            .withBundle(Translations.bundle)
      }

      public object Manage {
        /**
         * manage-auto-response
         */
        public val autoResponse: Key = Key("commands.suggestions.manage.autoResponse")
            .withBundle(Translations.bundle)

        /**
         * manage-state
         */
        public val state: Key = Key("commands.suggestions.manage.state")
            .withBundle(Translations.bundle)

        public object AutoResponse {
          /**
           * Use an automated response to a suggestion.
           */
          public val description: Key = Key("commands.suggestions.manage.autoResponse.description")
              .withBundle(Translations.bundle)
        }

        public object State {
          /**
           * Suggestion state change command; "clear" to remove comment.
           */
          public val description: Key = Key("commands.suggestions.manage.state.description")
              .withBundle(Translations.bundle)
        }
      }

      public object Refresh {
        /**
         * Create a new channel message to allow creation of suggestions.
         */
        public val description: Key = Key("commands.suggestions.refresh.description")
            .withBundle(Translations.bundle)
      }

      public object Spreadsheet {
        /**
         * Download a copy of the suggestions as a spreadsheet.
         */
        public val description: Key = Key("commands.suggestions.spreadsheet.description")
            .withBundle(Translations.bundle)
      }
    }

    public object Utility {
      /**
       * say
       */
      public val say: Key = Key("commands.utility.say")
          .withBundle(Translations.bundle)

      /**
       * thread
       */
      public val thread: Key = Key("commands.utility.thread")
          .withBundle(Translations.bundle)

      public object MessageCommand {
        /**
         * Pin in thread
         */
        public val pinInThread: Key = Key("commands.utility.messageCommand.pinInThread")
            .withBundle(Translations.bundle)

        /**
         * Raw JSON
         */
        public val rawJson: Key = Key("commands.utility.messageCommand.rawJson")
            .withBundle(Translations.bundle)

        /**
         * Unpin in thread
         */
        public val unpinInThread: Key = Key("commands.utility.messageCommand.unpinInThread")
            .withBundle(Translations.bundle)
      }

      public object Say {
        /**
         * Send a message.
         */
        public val description: Key = Key("commands.utility.say.description")
            .withBundle(Translations.bundle)
      }

      public object Thread {
        /**
         * archive
         */
        public val archive: Key = Key("commands.utility.thread.archive")
            .withBundle(Translations.bundle)

        /**
         * backup
         */
        public val backup: Key = Key("commands.utility.thread.backup")
            .withBundle(Translations.bundle)

        /**
         * Thread management commands
         */
        public val description: Key = Key("commands.utility.thread.description")
            .withBundle(Translations.bundle)

        /**
         * pin
         */
        public val pin: Key = Key("commands.utility.thread.pin")
            .withBundle(Translations.bundle)

        /**
         * prevent-archiving
         */
        public val preventArchiving: Key = Key("commands.utility.thread.preventArchiving")
            .withBundle(Translations.bundle)

        /**
         * rename
         */
        public val rename: Key = Key("commands.utility.thread.rename")
            .withBundle(Translations.bundle)

        /**
         * set-owner
         */
        public val setOwner: Key = Key("commands.utility.thread.setOwner")
            .withBundle(Translations.bundle)

        /**
         * unpin
         */
        public val unpin: Key = Key("commands.utility.thread.unpin")
            .withBundle(Translations.bundle)

        public object Archive {
          /**
           * Archive the current thread, if you have permission
           */
          public val description: Key = Key("commands.utility.thread.archive.description")
              .withBundle(Translations.bundle)
        }

        public object Backup {
          /**
           * Get all messages in the current thread, saving them into a Markdown file.
           */
          public val description: Key = Key("commands.utility.thread.backup.description")
              .withBundle(Translations.bundle)
        }

        public object Pin {
          /**
           * Pin a message in this thread, if you have permission
           */
          public val description: Key = Key("commands.utility.thread.pin.description")
              .withBundle(Translations.bundle)
        }

        public object PreventArchiving {
          /**
           * Prevent the current thread from archiving, if you have permission
           */
          public val description: Key = Key("commands.utility.thread.preventArchiving.description")
              .withBundle(Translations.bundle)
        }

        public object Rename {
          /**
           * Rename the current thread, if you have permission
           */
          public val description: Key = Key("commands.utility.thread.rename.description")
              .withBundle(Translations.bundle)
        }

        public object SetOwner {
          /**
           * Change the owner of the thread, if you have permission
           */
          public val description: Key = Key("commands.utility.thread.setOwner.description")
              .withBundle(Translations.bundle)
        }

        public object Unpin {
          /**
           * Unpin a message in this thread, if you have permission
           */
          public val description: Key = Key("commands.utility.thread.unpin.description")
              .withBundle(Translations.bundle)
        }
      }
    }
  }

  public object Components {
    public object Utility {
      public object Thread {
        public object SetOwner {
          /**
           * Yes
           */
          public val confirmButton: Key = Key("components.utility.thread.setOwner.confirmButton")
              .withBundle(Translations.bundle)
        }
      }
    }
  }

  public object Converter {
    public object Suggestion {
      /**
       * "Suggestion ID"
       */
      public val signatureType: Key = Key("converter.suggestion.signatureType")
          .withBundle(Translations.bundle)
    }
  }

  public object Enum {
    public object SuggestionStatus {
      /**
       * Approved
       */
      public val approved: Key = Key("enum.suggestionStatus.approved")
          .withBundle(Translations.bundle)

      /**
       * Denied
       */
      public val denied: Key = Key("enum.suggestionStatus.denied")
          .withBundle(Translations.bundle)

      /**
       * Duplicate
       */
      public val duplicate: Key = Key("enum.suggestionStatus.duplicate")
          .withBundle(Translations.bundle)

      /**
       * Future Concern
       */
      public val future: Key = Key("enum.suggestionStatus.future")
          .withBundle(Translations.bundle)

      /**
       * Implemented
       */
      public val implemented: Key = Key("enum.suggestionStatus.implemented")
          .withBundle(Translations.bundle)

      /**
       * Invalid
       */
      public val invalid: Key = Key("enum.suggestionStatus.invalid")
          .withBundle(Translations.bundle)

      /**
       * Open
       */
      public val `open`: Key = Key("enum.suggestionStatus.open")
          .withBundle(Translations.bundle)

      /**
       * Requires Name
       */
      public val requiresName: Key = Key("enum.suggestionStatus.requires_name")
          .withBundle(Translations.bundle)

      /**
       * Spam
       */
      public val spam: Key = Key("enum.suggestionStatus.spam")
          .withBundle(Translations.bundle)

      /**
       * Stale
       */
      public val stale: Key = Key("enum.suggestionStatus.stale")
          .withBundle(Translations.bundle)
    }
  }

  public object Errors {
    public object Exceptions {
      /**
       * Unknown suggestion ID: {0}
       */
      public val unknownSuggestionId: Key = Key("errors.exceptions.unknownSuggestionId")
          .withBundle(Translations.bundle)
    }
  }

  public object Extensions {
    public object Sab {
      /**
       * {0} Safety and Abuse
       */
      public val footer: Key = Key("extensions.sab.footer")
          .withBundle(Translations.bundle)

      /**
       * Safety and Abuse
       */
      public val name: Key = Key("extensions.sab.name")
          .withBundle(Translations.bundle)
    }
  }

  public object GeneralArgs {
    public object Cmd {
      public object ModCommands {
        /**
         * duration
         */
        public val duration: Key = Key("generalArgs.cmd.modCommands.duration")
            .withBundle(Translations.bundle)

        /**
         * player
         */
        public val player: Key = Key("generalArgs.cmd.modCommands.player")
            .withBundle(Translations.bundle)

        /**
         * reason
         */
        public val reason: Key = Key("generalArgs.cmd.modCommands.reason")
            .withBundle(Translations.bundle)

        public object Duration {
          /**
           * The duration of the action.
           */
          public val description: Key = Key("generalArgs.cmd.modCommands.duration.description")
              .withBundle(Translations.bundle)
        }

        public object Player {
          /**
           * The player to act on.
           */
          public val description: Key = Key("generalArgs.cmd.modCommands.player.description")
              .withBundle(Translations.bundle)
        }

        public object Reason {
          /**
           * The reason for the moderation action.
           */
          public val description: Key = Key("generalArgs.cmd.modCommands.reason.description")
              .withBundle(Translations.bundle)
        }
      }
    }

    public object ManageTags {
      /**
       * trigger
       */
      public val trigger: Key = Key("generalArgs.manageTags.trigger")
          .withBundle(Translations.bundle)

      public object Trigger {
        /**
         * The Regex pattern for matching message content
         */
        public val description: Key = Key("generalArgs.manageTags.trigger.description")
            .withBundle(Translations.bundle)
      }
    }

    public object Suggestions {
      /**
       * status
       */
      public val status: Key = Key("generalArgs.suggestions.status")
          .withBundle(Translations.bundle)

      /**
       * suggestion
       */
      public val suggestion: Key = Key("generalArgs.suggestions.suggestion")
          .withBundle(Translations.bundle)

      public object Status {
        /**
         * Status to apply.
         */
        public val description: Key = Key("generalArgs.suggestions.status.description")
            .withBundle(Translations.bundle)
      }

      public object Suggestion {
        /**
         * Suggestion ID to act on.
         */
        public val description: Key = Key("generalArgs.suggestions.suggestion.description")
            .withBundle(Translations.bundle)
      }
    }
  }

  public object GeneralResponses {
    public object Cmd {
      /**
       * Response:```{0}```
       */
      public val response: Key = Key("generalResponses.cmd.response")
          .withBundle(Translations.bundle)

      /**
       * Command executed successfully.
       */
      public val success: Key = Key("generalResponses.cmd.success")
          .withBundle(Translations.bundle)

      public object Embed {
        /**
         * MSC:
         */
        public val title: Key = Key("generalResponses.cmd.embed.title")
            .withBundle(Translations.bundle)
      }

      public object Error {
        /**
         * Running commands on the Minecraft server is currently disabled.
         */
        public val disabled: Key = Key("generalResponses.cmd.error.disabled")
            .withBundle(Translations.bundle)

        /**
         * Running commands on the Minecraft server is currently unavailable.
         */
        public val unavailable: Key = Key("generalResponses.cmd.error.unavailable")
            .withBundle(Translations.bundle)
      }
    }
  }

  public object Modals {
    public object Utility {
      public object LogEvent {
        /**
         * Log Event
         */
        public val title: Key = Key("modals.utility.logEvent.title")
            .withBundle(Translations.bundle)

        public object Description {
          /**
           * Description
           */
          public val label: Key = Key("modals.utility.logEvent.description.label")
              .withBundle(Translations.bundle)

          /**
           * A few words on what's happening
           */
          public val placeholder: Key = Key("modals.utility.logEvent.description.placeholder")
              .withBundle(Translations.bundle)
        }
      }
    }
  }

  public object Responses {
    public object Cmd {
      public object List {
        /**
         * There are currently no players online.
         */
        public val noPlayers: Key = Key("responses.cmd.list.noPlayers")
            .withBundle(Translations.bundle)

        /**
         * Current players: {0}
         */
        public val players: Key = Key("responses.cmd.list.players")
            .withBundle(Translations.bundle)
      }

      public object Mclogs {
        /**
         * There was an error uploading the log: {0}
         */
        public val error: Key = Key("responses.cmd.mclogs.error")
            .withBundle(Translations.bundle)

        /**
         * Log uploaded successfully: [`{0}`]({1})
         */
        public val success: Key = Key("responses.cmd.mclogs.success")
            .withBundle(Translations.bundle)
      }
    }

    public object Minecraft {
      public object ForceLink {
        /**
         * Successfully linked {0} to Minecraft account with uuid {1}.
         */
        public val success: Key = Key("responses.minecraft.forceLink.success")
            .withBundle(Translations.bundle)

        public object Error {
          /**
           * The specified user already has a verified linked Minecraft account with uuid {0}.
           */
          public val alreadyLinked: Key = Key("responses.minecraft.forceLink.error.alreadyLinked")
              .withBundle(Translations.bundle)

          /**
           * The specified user already has a verified linked Minecraft account with a different
           * uuid: {0}. Please unlink it first.
           */
          public val alreadyLinkedDifferent: Key =
              Key("responses.minecraft.forceLink.error.alreadyLinkedDifferent")
              .withBundle(Translations.bundle)

          /**
           * Force linking Minecraft accounts is currently disabled.
           */
          public val disabled: Key = Key("responses.minecraft.forceLink.error.disabled")
              .withBundle(Translations.bundle)

          /**
           * The provided UUID is invalid. Please provide a valid Minecraft UUID.
           */
          public val invalidUuid: Key = Key("responses.minecraft.forceLink.error.invalidUuid")
              .withBundle(Translations.bundle)
        }
      }

      public object Link {
        /**
         * Please join the Minecraft server and run the command `/ctd-krafter link {0}` to complete
         * the linking process.
         */
        public val success: Key = Key("responses.minecraft.link.success")
            .withBundle(Translations.bundle)

        public object Error {
          /**
           * Linking Minecraft accounts is currently disabled.
           */
          public val disabled: Key = Key("responses.minecraft.link.error.disabled")
              .withBundle(Translations.bundle)

          /**
           * The provided UUID is invalid. Please provide a valid Minecraft UUID.
           */
          public val invalidUuid: Key = Key("responses.minecraft.link.error.invalidUuid")
              .withBundle(Translations.bundle)
        }
      }

      public object Unlink {
        /**
         * Your Minecraft account with uuid {0} has successfully been unlinked.
         */
        public val success: Key = Key("responses.minecraft.unlink.success")
            .withBundle(Translations.bundle)

        public object Error {
          /**
           * Unlinking Minecraft accounts is currently disabled.
           */
          public val disabled: Key = Key("responses.minecraft.unlink.error.disabled")
              .withBundle(Translations.bundle)

          /**
           * Your Discord account is not linked to any Minecraft account.
           */
          public val notLinked: Key = Key("responses.minecraft.unlink.error.notLinked")
              .withBundle(Translations.bundle)
        }
      }
    }

    public object Rsvp {
      public object Error {
        /**
         * The provided date and time is invalid. Please use the format: YYYY-MM-DD HH:mm (24-hour
         * time, UTC)
         */
        public val invalidDate: Key = Key("responses.rsvp.error.invalidDate")
            .withBundle(Translations.bundle)
      }
    }
  }
}
