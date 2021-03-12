/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.database;

import de.finn.friendsystem.FriendSystem;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import de.finn.friendsystem.enums.FriendEnum;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import org.bson.Document;

/**
 *
 * @author Finn
 */
public class MongoPlayer {
    
    private final FriendSystem plugin;
    private final UUID uuid;
    private final String name;
    
    public MongoPlayer(FriendSystem plugin, UUID uuid, String name) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.name = name;
    }
    
    /**
     * @param consumer returns the player document
     */
    //<editor-fold defaultstate="collapsed" desc="connectPlayer">
    public void connectPlayer(Consumer<Document> consumer) {
        this.plugin.getMongoManager().getPlayers().find(Filters.eq(FriendEnum.UUID.name(), this.uuid)).first((document, throwable) -> {
            if (throwable != null) {
                System.err.println(throwable.fillInStackTrace());
            }
            
            if (document == null) {
                Document create = (new Document(FriendEnum.UUID.name(), this.uuid))
                        .append(FriendEnum.NAME.name(), this.name)
                        .append(FriendEnum.SKIN.name(), "-")
                        .append(FriendEnum.FRIEND_LIST.name(), new ArrayList())
                        .append(FriendEnum.REQUEST_LIST.name(), new ArrayList())
                        .append(FriendEnum.FAVORITES.name(), new ArrayList())
                        .append(FriendEnum.ALLOW_FRIEND.name(), true)
                        .append(FriendEnum.ALLOW_PARTY.name(), true)
                        .append(FriendEnum.ALLOW_MESSAGE.name(), true)
                        .append(FriendEnum.ALLOW_JUMP.name(), true);
                
                this.plugin.getMongoManager().getPlayers().insertOne(create, (aVoid, throwable2) -> {
                    if (throwable2 != null) {
                        System.err.println(throwable2.fillInStackTrace());
                    }
                    
                    if (aVoid != null) {
                        consumer.accept(create);
                    }
                });
            } else {
                consumer.accept(document);
            }
        });
    }
    //</editor-fold>
    
    /**
     * 
     * @param document is the document to update
     * @param consumer returns the update result
     */
    //<editor-fold defaultstate="collapsed" desc="updatePlayer">
    public void updatePlayer(Document document, Consumer<UpdateResult> consumer) {
        this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), this.uuid), document, (updateResult, throwable) -> {
            if (throwable != null) {
                System.err.println(throwable.fillInStackTrace());
            }
            
            if (updateResult != null) {
                consumer.accept(updateResult);
            } else {
                consumer.accept(null);
            }
        });
    }
    //</editor-fold>
    
    /**
     * @param consumer returns the player document
     */
    //<editor-fold defaultstate="collapsed" desc="foundPlayer">
    public void foundPlayer(Consumer<Document> consumer) {
        this.plugin.getMongoManager().getPlayers().find(Filters.eq(FriendEnum.NAME.name(), this.name)).first((document, throwable) -> {
            if (throwable != null) {
                System.err.println(throwable.fillInStackTrace());
            }
            
            if (document != null) {
                consumer.accept(document);
            } else {
                consumer.accept(null);
            }
        });
    }
    //</editor-fold>
    
}
