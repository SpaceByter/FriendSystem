/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.manager;

import de.finn.friendsystem.FriendSystem;
import java.util.HashMap;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Finn
 */
public class ConversationManager {
    
    private final FriendSystem plugin;
    
    HashMap<ProxiedPlayer, ProxiedPlayer> conversations = new HashMap<>();

    public ConversationManager(FriendSystem plugin) {
        this.plugin = plugin;
    }
    
    public void setReplyTarget(ProxiedPlayer messager, ProxiedPlayer reciever) {
        conversations.put(messager, reciever);
        conversations.put(reciever, messager);
    }
    
    public ProxiedPlayer getReplyTarget(ProxiedPlayer messager) {
        return conversations.get(messager);
    }
    
}
