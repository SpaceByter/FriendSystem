package de.finn.friendsystem.commands;

import de.finn.friendsystem.FriendSystem;
import de.finn.friendsystem.enums.SettingsEnum;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Finn
 */
public class FriendCommand extends Command {
    
    private final FriendSystem plugin;

    public FriendCommand(FriendSystem plugin) {
        super("friend", (String)null, new String[]{"friends", "f"});
        this.plugin = plugin;
        this.plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }
    
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) return;
        
        ProxiedPlayer player = (ProxiedPlayer)sender;
        if(args.length == 2) {
            switch(args[0]) {
                case "add":
                    plugin.getFriendManager().sendFriendRequest(player, args[1]);
                    break;
                case "remove":
                    plugin.getFriendManager().removeFriend(player, args[1]);
                    break;
                case "deny":
                    plugin.getFriendManager().denyFriendRequest(player, args[1]);
                    break;
                case "accept":
                    plugin.getFriendManager().acceptFriendRequest(player, args[1]);
                    break;
                case "jump": 
                    plugin.getFriendManager().jumpToFriend(player, args[1]);
                    break;
                case "toggle":
                    if(args[1].equalsIgnoreCase("request")) {
                        plugin.getFriendManager().toggleSettings(player, SettingsEnum.REQUEST);
                        break;
                    }
                    if(args[1].equalsIgnoreCase("notify")) {
                        plugin.getFriendManager().toggleSettings(player, SettingsEnum.MESSAGE);
                        break;
                    }
                    if(args[1].equalsIgnoreCase("party")) {
                        plugin.getFriendManager().toggleSettings(player, SettingsEnum.PARTY);
                        break;
                    }
                    if(args[1].equalsIgnoreCase("jump")) {
                        plugin.getFriendManager().toggleSettings(player, SettingsEnum.JUMP);
                        break;
                    }
                    break;
                    
            }
            return;
        }
        
        if(args.length == 1) {
            switch(args[0]) {
                case "list":
                    plugin.getFriendManager().listFriends(player);
                    break;
            }
            return;
        }
        
        player.sendMessage(this.plugin.getPREFIX() + "§7Command Übersicht:");
        player.sendMessage(" ");
        player.sendMessage("§b/friend add §7<§3Spieler§7> | Schicke jemandem eine §3Einladung§7.");
        player.sendMessage("§b/friend deny §7<§3Spieler§7> | Akzeptiere eine §3Einladung§7.");
        player.sendMessage("§b/friend accept §7<§3Spieler§7> | Lehne eine §3Einladung §7ab.");
        player.sendMessage("§b/friend remove §7<§3Spieler§7> | Entferne jemanden aus deiner §3Freundesliste§7.");
    }
    
}
