/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.database;

import de.finn.friendsystem.FriendSystem;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import org.bson.Document;
import org.bson.conversions.Bson;

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
    
    public void connectPlayer(Consumer<Document> consumer) {
      this.plugin.getMongoManager().getPlayers().find(Filters.eq("UUID", this.uuid)).first((document, throwable) -> {
         if (throwable != null) {
            System.err.println(throwable.fillInStackTrace());
         }

         if (document == null) {
            Document create = (new Document("UUID", this.uuid)).append("NAME", this.name).append("SKIN", "-").append("FRIEND_LIST", new ArrayList()).append("REQUEST_LIST", new ArrayList()).append("BLOCK_LIST", new ArrayList()).append("ALLOW_FRIEND", true).append("ALLOW_PARTY", true).append("ALLOW_MESSAGE", true).append("ALLOW_JUMP", true);
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

   public void updatePlayer(Document document, Consumer<UpdateResult> consumer) {
      this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", this.uuid), document, (updateResult, throwable) -> {
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

   public void foundPlayer(Consumer<Document> consumer) {
      this.plugin.getMongoManager().getPlayers().find(Filters.eq("NAME", this.name)).first((document, throwable) -> {
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

    public void updateFoundPlayer(Document document, Consumer<UpdateResult> consumer) {
        this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("NAME", this.name), document, (updateResult, throwable) -> {
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
    
}
