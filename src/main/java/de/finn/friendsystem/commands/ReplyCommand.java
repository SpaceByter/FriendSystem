/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.commands;

import com.mongodb.async.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.finn.friendsystem.FriendSystem;
import de.finn.friendsystem.enums.FriendEnum;
import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

/**
 *
 * @author Finn
 */
public class ReplyCommand extends Command{
    
    private final FriendSystem plugin;
    
    private final String prefix = "§7✦ §aMSG §8» ";
    
    public ReplyCommand(FriendSystem plugin) {
        super("reply", (String)null, "r");
        this.plugin = plugin;
        this.plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }
    
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) return;
        if(!(args.length > 0)) {
            sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§b/r §7<§3Nachricht§7>"));
            return;
        }
        
        ProxiedPlayer messager = (ProxiedPlayer)sender;
        ProxiedPlayer reciever = plugin.getConversationManager().getReplyTarget(messager);
        if(reciever == null) {
            messager.sendMessage(prefix + "§7Du hast keine Unterhaltung mit diesem Spieler");
            return;
        }
        
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        found.filter(Filters.eq("UUID", messager.getUniqueId())).first((document, throwable) -> {
            ArrayList<UUID> friendList = ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class));
            if(friendList.contains(reciever.getUniqueId())) {
                String message = "";
                for(int i = 0; i < args.length; i++) {
                    message += " " + args[i];
                }

                messager.sendMessage(new TextComponent(prefix + "§3Du §7» §b" + reciever.getName() + "§7:" + message));
                reciever.sendMessage(new TextComponent(prefix + "§3" + messager.getName() + " §7» §bDir§7:" + message));
                return;
            }
            sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Der Spieler §9" + reciever.getName() + " §7ist nicht mit dir befreundet."));
        });
        
        
    }
}
