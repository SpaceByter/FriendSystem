/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem;

import de.finn.friendsystem.commands.FriendCommand;
import de.finn.friendsystem.database.MongoManager;
import de.finn.friendsystem.listener.PlayerConnect;
import de.finn.friendsystem.manager.FriendManager;
import net.md_5.bungee.api.plugin.Plugin;

/**
 *
 * @author Finn
 */

public class FriendSystem extends Plugin {
    
    private final String PREFIX = "§7✦ §3Freunde §8» ";
    private MongoManager mongoManager;
    private FriendManager friendManager;
    
    @Override
    public void onEnable() {
        init();
        
        new FriendCommand(this);
        new PlayerConnect(this);
    }

    @Override
    public void onDisable() {
        this.mongoManager.disconnectDatabase();
    }
    
    //<editor-fold defaultstate="collapsed" desc="init">
    private void init() {
        this.mongoManager = new MongoManager("localhost", 27017);
        this.mongoManager.connect();
        
        this.friendManager = new FriendManager(this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPREFIX">
    public String getPREFIX() {
        return PREFIX;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getMongoManager">
    public MongoManager getMongoManager() {
        return mongoManager;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getFriendManager">
    public FriendManager getFriendManager() {
        return friendManager;
    }
    //</editor-fold>
    
}
