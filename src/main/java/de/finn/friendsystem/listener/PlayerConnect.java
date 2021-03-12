/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.listener;

import com.mongodb.client.model.Filters;
import de.finn.friendsystem.FriendSystem;
import de.finn.friendsystem.database.MongoPlayer;
import de.finn.friendsystem.enums.FriendEnum;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Finn
 */
public class PlayerConnect implements Listener {
    
    private final FriendSystem plugin;
    
    public PlayerConnect(FriendSystem plugin) {
        this.plugin = plugin;
        this.plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }
    
    @EventHandler
    public void LoginEvent(PostLoginEvent event) {
        (new MongoPlayer(plugin, event.getPlayer().getUniqueId(), event.getPlayer().getName())).connectPlayer((document) -> {
            if (!document.getString(FriendEnum.NAME.name()).equalsIgnoreCase(event.getPlayer().getName())) {
                document.replace(FriendEnum.NAME.name(), event.getPlayer().getName());
                plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), event.getPlayer().getUniqueId()), document, (update, t) -> {});
            }
            
            StringBuilder stringBuilder = new StringBuilder();
            
            ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).forEach((uuids) -> {
                ProxiedPlayer player = plugin.getProxy().getPlayer((UUID)uuids);
                if (player != null) {
                    Objects.requireNonNull(this);
                    player.sendMessage(new TextComponent(stringBuilder.append(plugin.getPREFIX()).append("§b").append(event.getPlayer().getName()).append(" §7ist nun §aOnline").toString()));
                }
            });
            
            if (!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).isEmpty()) {
                int requestSize = ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).size();
                ProxiedPlayer player = event.getPlayer();
                Objects.requireNonNull(this);
                player.sendMessage(new TextComponent(stringBuilder.append(plugin.getPREFIX()).append("§7Du hast noch §b").append(requestSize).append(" §7Anfrage(n) offen").toString()));
            }
        });
    }
    
    
    @EventHandler
    public void PlayerQuit(PlayerDisconnectEvent event) {
        new MongoPlayer(plugin, event.getPlayer().getUniqueId(), event.getPlayer().getName()).foundPlayer((document) -> {
            ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).forEach((uuids) -> {
                ProxiedPlayer player = plugin.getProxy().getPlayer((UUID)uuids);
                if (player != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Objects.requireNonNull(this);
                    player.sendMessage(new TextComponent(stringBuilder.append(plugin.getPREFIX()).append("§b").append(event.getPlayer().getName()).append(" §7ist nun §cOffline").toString()));
                }
            });
        });
    
    }
}
