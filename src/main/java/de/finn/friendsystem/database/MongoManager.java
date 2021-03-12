/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.database;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;

/**
 *
 * @author Finn
 */
public class MongoManager {
    
    private final String hostname;
    private final int port;

    private MongoClient client;
    private MongoDatabase database;
    
    private MongoCollection<Document> players;
    
    /**
     * @param hostname is the hostname of the database
     * @param port is the port of the database
     */
    public MongoManager(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    //<editor-fold defaultstate="collapsed" desc="connect">
    public void connect(){
        this.client = MongoClients.create(new ConnectionString("mongodb://" + this.hostname + ":" + this.port));
        this.database = this.client.getDatabase("FriendSystem");
        this.players = this.database.getCollection("players");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="disconnectDatabase">
    public void disconnectDatabase(){
        if(this.client != null) this.client.close();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPlayers">
    public MongoCollection<Document> getPlayers() {
        return players;
    }
    //</editor-fold>
    
}
