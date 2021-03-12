/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.manager;

import de.finn.friendsystem.FriendSystem;
import com.mongodb.async.client.FindIterable;
import com.mongodb.client.model.Filters;
import de.finn.friendsystem.database.MongoPlayer;
import de.finn.friendsystem.enums.FriendEnum;
import de.finn.friendsystem.enums.SettingsEnum;
import de.finn.friendsystem.objects.TextComponentBuilder;
import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

/**
 *
 * @author Finn
 */
public class FriendManager {
    
    private final FriendSystem plugin;
    private final FindIterable<Document> found;
    
    public FriendManager(FriendSystem plugin) {
        this.plugin = plugin;
        this.found = plugin.getMongoManager().getPlayers().find();
    }
    
    /**
     * @param sender is the executer of the command
     * @param username is the name of the target
     */
    //<editor-fold defaultstate="collapsed" desc="setFavorite">
    public void setFavorite(ProxiedPlayer sender, String username) {
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        
        if(target != null) {
            /* PLAYER IS ONLINE */
            
            found.filter(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId())).first((document, throwable) -> {
                if(document == null) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                    return;
                }
                
                if(!(document.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class).contains(target.getUniqueId()))) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler befindet sich nicht in deiner Freundesliste.");
                    return;
                }
                
                if(document.get(FriendEnum.FAVORITES.name(), ArrayList.class).contains(target.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast diesen Spieler bereits als Favorit makiert.");
                    return;
                }
                
                if(document.get(FriendEnum.FAVORITES.name(), ArrayList.class).size() >= 10) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast die maximale Anzahl von Favoriten erreicht.");
                    return;
                }
                
                ((ArrayList)document.get(FriendEnum.FAVORITES.name(), (Class)ArrayList.class)).add(target.getUniqueId());
                
                /* UPDATE PLAYER DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), document, (updateResult, thw) -> {
                    if (updateResult == null && thw != null) return;
                    
                    sender.sendMessage(plugin.getPREFIX() + "§7Du hast den Spieler §b" + target.getName() + " §7als Favorit makiert.");
                });
            });
            return;
        }
        
        /* PLAYER IS OFFLINE */
        new MongoPlayer(plugin, sender.getUniqueId(), sender.getName()).foundPlayer((document) -> {
            if(document == null) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                return;
            }

            if(!(document.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class).contains(document.get(FriendEnum.UUID.name(), UUID.class)))) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler befindet sich nicht in deiner Freundesliste.");
                return;
            }

            if(document.get(FriendEnum.FAVORITES.name(), ArrayList.class).contains(document.get(FriendEnum.UUID.name(), UUID.class))) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast diesen Spieler bereits als Favorit makiert.");
                return;
            }

            if(document.get(FriendEnum.FAVORITES.name(), ArrayList.class).size() >= 10) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast die maximale Anzahl von Favoriten erreicht.");
                return;
            }
            
            ((ArrayList)document.get(FriendEnum.FAVORITES.name(), (Class)ArrayList.class)).add(document.get(FriendEnum.UUID.name(), UUID.class));
            
            
            /* UPDATE PLAYER DOCUMENT IN DATABASE */
            this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), document, (updateResult, thw) -> {
                if (updateResult == null && thw != null) return;
                
                sender.sendMessage(plugin.getPREFIX() + "§7Du hast den Spieler §b" + username + " §7als Favorit makiert.");
            });
        });
    }
    //</editor-fold>
    
    /**
     * @param sender is the executer of the command
     * @param username is the name of the target
     */
    //<editor-fold defaultstate="collapsed" desc="removeFavorite">
    public void removeFavorite(ProxiedPlayer sender, String username) {
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        
        if(target != null) {
            /* PLAYER IS ONLINE */
            found.filter(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId())).first((document, throwable) -> {
                if(document == null) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + target.getName() + " §7war noch nie auf dem Netzwerk.");
                    return;
                }
                
                if(!(document.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class).contains(target.getUniqueId()))) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler befindet sich nicht in deiner Freundesliste.");
                    return;
                }
                
                if(!(document.get(FriendEnum.FAVORITES.name(), ArrayList.class).contains(target.getUniqueId()))) return;
                
                ((ArrayList)document.get(FriendEnum.FAVORITES.name(), (Class)ArrayList.class)).remove(target.getUniqueId());
                
                
                /* UPDATE PLAYER DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), document, (updateResult, thw) -> {
                    if (updateResult == null && thw != null) return;
                    
                    sender.sendMessage(plugin.getPREFIX() + "§7Du hast den Spieler §b" + target.getName() + " §7von deinen Favoriten entfernt.");
                });
            });
            return;
        }
        
        /* PLAYER IS OFFLINE */
        new MongoPlayer(plugin, sender.getUniqueId(), sender.getName()).foundPlayer((document) -> {
            if(document == null) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                return;
            }

            if(!(document.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class).contains(document.get(FriendEnum.UUID.name(), UUID.class)))) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler befindet sich nicht in deiner Freundesliste.");
                return;
            }

            if(!(document.get(FriendEnum.FAVORITES.name(), ArrayList.class).contains(document.get(FriendEnum.UUID.name(), UUID.class)))) return;
            
            ((ArrayList)document.get(FriendEnum.FAVORITES.name(), (Class)ArrayList.class)).remove(document.get(FriendEnum.UUID.name(), UUID.class));
            
            
            /* UPDATE PLAYER DOCUMENT IN DATABASE */
            this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), document, (updateResult, thw) -> {
                if (updateResult == null && thw != null) return;
                
                sender.sendMessage(plugin.getPREFIX() + "§7Du hast den Spieler §b" + username + " §7von deinen Favoriten entfernt.");
            });
        });
    }
    //</editor-fold>
    
    /**
     * @param sender is the executer of the command
     * @param username is the name of the target
     */
    //<editor-fold defaultstate="collapsed" desc="sendFriendRequest">
    public void sendFriendRequest(ProxiedPlayer sender, String username) {
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        if (target != null) {
            /* PLAYER IS ONLINE */
            found.filter(Filters.eq(FriendEnum.UUID.name(), target.getUniqueId())).first((document, throwable) -> {
                if(document == null) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                    return;
                }
                
                if(((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).size() >= 30) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§b" + username + " §7hat die maximalen Freundesanfragen erreicht.");
                    return;
                }
                
                if(((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist bereits mit §b" + target.getName() + " §7befreundet.");
                    return;
                }
                
                if(((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast dem Spieler §b" + target.getName() + " §7bereits eine Anfrage gesendet.");
                    return;
                }
                
                ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).add(sender.getUniqueId());
                
                
                /* UPDATE PLAYER DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), target.getUniqueId()), document, (updateResult, thw) -> {
                    if (updateResult == null && thw != null) return;
                    
                    /* SEND FRIEND REQUEST MESSAGE */
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast §b" + target.getName() + " §7eine Anfrage gesendet.");
                    target.sendMessage(this.plugin.getPREFIX() + "§9" + sender.getName() + " §7möchte dein §3Freund §7werden:");
                    target.sendMessage(new TextComponent(plugin.getPREFIX()), new TextComponentBuilder("§7[§aAnnehmen§7]").addClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + sender.getName()).build(),
                    new TextComponent(" "),
                    new TextComponentBuilder("§7[§cAblehnen§7]").addClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + sender.getName()).build());
                });
            });
            return;
        }
        
        /* PLAYER IS OFFLINE */
        found.filter(Filters.eq("NAME", username)).first((document, throwable) -> {
            if(document == null) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                return;
            }

            if(((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).size() >= 30) {
                sender.sendMessage(this.plugin.getPREFIX() + "§b" + username + " §7hat die maximalen Freundesanfragen erreicht.");
                return;
            }

            if(((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist bereits mit §b" + target.getName() + " §7befreundet.");
                return;
            }

            if(((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast dem Spieler §b" + target.getName() + " §7bereits eine Anfrage gesendet.");
                return;
            }
            
            /* UPDATE PLAYER DOCUMENT IN DATABASE */
            this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("NAME", username), document, (updateResult, thw) -> {
                if (updateResult == null && thw != null) return;
                
                sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast §b" + username + " §7eine Anfrage gesendet.");
            });
        });
    }
    //</editor-fold>
    
    /**
     * @param sender is the executer of the command
     * @param username is the name of the target
     */
    //<editor-fold defaultstate="collapsed" desc="acceptFriendRequest">
    public void acceptFriendRequest(ProxiedPlayer sender, String username) {
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        
        if (target != null) {
            /* PLAYER IS ONLINE */
            found.filter(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId())).first((document, throwable) -> {
                if(document == null && throwable != null) return;
                
                if(!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), ArrayList.class)).contains(target.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§b" + target.getName() + " §7hat dir keine Anfrage gesendet.");
                    return;
                }

                if (((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class)).size() >= 100) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Deine Freundesliste ist voll.");
                    return;
                }
                
                ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), ArrayList.class)).remove(target.getUniqueId());
                ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class)).add(target.getUniqueId());
                
                found.filter(Filters.eq(FriendEnum.UUID.name(), target.getUniqueId())).first((foundDocument, thow) -> {
                    if (((ArrayList)foundDocument.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class)).size() >= 100) {
                        sender.sendMessage(this.plugin.getPREFIX() + "§7Die Freundesliste von §b" + target.getName() + " §7ist voll.");
                        return;
                    }
                    
                    ((ArrayList)foundDocument.get(FriendEnum.FRIEND_LIST.name(), ArrayList.class)).add(sender.getUniqueId());
                    
                    /* UPDATE TARGET & PLAYER DOCUMENT IN DATABASE */
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.NAME.name(), username), foundDocument, (updateResult, th) -> {});
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), target.getUniqueId()), foundDocument, (updateResult, th) -> {
                        if (updateResult != null) {
                            target.sendMessage(this.plugin.getPREFIX() + "§7Du bist nun mit §b" + sender.getName() + " §7befreundet.");
                        }
                    });
                    
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), document, (updateResult, arg1) -> {
                        if (updateResult != null) {
                            sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist nun mit §b" + target.getName() + " §7befreundet.");
                        }
                    });
                });
            });
            return;
        }
        
        /* PLAYER IS OFFLINE */
        found.filter(Filters.eq(FriendEnum.NAME.name(), username)).first((document, thow) -> {
            if (document == null) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                return;
            }
            
            if (((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).size() >= 100) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Die Freundesliste von §b" + username + " §7ist voll.");
                return;
            }
            
            ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).add(sender.getUniqueId());
            
            found.filter(Filters.eq("UUID", sender.getUniqueId())).first((foundDocument, throwable) -> {
                if (!((ArrayList)foundDocument.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(document.getString(FriendEnum.UUID.name()))) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§b" + username + " §7hat dir keine Anfrage gesendet.");
                    return;
                }
                
                if (((ArrayList)foundDocument.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).size() >= 100) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Deine Freundesliste ist voll.");
                    return;
                }
                
                ((ArrayList)foundDocument.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).add(document.getString(FriendEnum.UUID.name()));
                
                /* UPDATE PLAYER DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), foundDocument, (updateResult, thw) -> {
                    if (updateResult != null) {
                        sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist nun mit §b" + username + " §7befreundet.");
                    }
                });
            });
        });
    }
    //</editor-fold>
    
    /**
     * @param sender is the executer of the command
     * @param username is the name of the target
     */
    //<editor-fold defaultstate="collapsed" desc="denyFriendRequest">
    public void denyFriendRequest(ProxiedPlayer sender, String username) {
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        
        if (target != null) {
            /* PLAYER IS ONLINE */
            found.filter(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId())).first((document, throwable) -> {
                if (!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(target.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§b" + target.getName() + " §7hat dir keine Anfrage gesendet.");
                    return;
                } 
                
                ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).remove(target.getUniqueId());
                
                /* UPDATE PLAYER DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), document, (updateResult, thw) -> {
                    if (updateResult != null) {
                        sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundesanfrage von §b" + target.getName() + " §7abgelehnt.");
                    }
                });
            });
            return;
        }
        
        /* PLAYER IS OFFLINE */
        found.filter(Filters.eq(FriendEnum.NAME.name(), username)).first((t, thw) -> {
            if (t == null) {
                sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                return;
            } 
            
            found.filter(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId())).first((document, thww) -> {
                if (!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(t.get(FriendEnum.UUID.name(), UUID.class))) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§b" + username + " §7hat dir keine Anfrage gesendet.");
                    return;
                }
                
                ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).remove(t.get(FriendEnum.UUID.name(), UUID.class));
                
                /* UPDATE PLAYER DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId()), document, (updateResult, thw2) -> {
                    if (updateResult != null) {
                        sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundesanfrage von §b" + target.getName() + " §7abgelehnt.");
                    }
                });
            });
        });
    }
    //</editor-fold>
    
    /**
     * @param sender is the executer of the command
     * @param username is the name of the target
     */
    //<editor-fold defaultstate="collapsed" desc="removeFriend">
    public void removeFriend(ProxiedPlayer player, String username) {
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        
        if (target != null) {
            /* PLAYER IS ONLINE */
            found.filter(Filters.eq(FriendEnum.UUID.name(), player.getUniqueId())).first((document, throwable) -> {
                if (throwable != null) {
                    System.err.println(throwable.fillInStackTrace());
                }
                
                if (!((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(target.getUniqueId())) {
                    player.sendMessage(this.plugin.getPREFIX() + "§7Du bist nicht mit §b" + username + " §7befreundet.");
                    return;
                }
                
                ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).remove(target.getUniqueId());
                
                /* UPDATE PLAYER DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", player.getUniqueId()), document, (updateResult, arg1) -> {
                    if (updateResult != null) {
                        player.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundschaft mit §b" + target.getName() + " §7beendet.");
                    }
                });
                
                found.filter(Filters.eq(FriendEnum.UUID.name(), target.getUniqueId())).first((t, thw) -> {
                    ((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).remove(player.getUniqueId());
                    
                    /* UPDATE TARGET DOCUMENT IN DATABASE */
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), target.getUniqueId()), t, (updateResult, th) -> {
                        if (updateResult != null) {
                            target.sendMessage(this.plugin.getPREFIX() + "§b" + player.getName() + " §7hat die Freundschaft beendet.");
                            removeFavorite(player, username);
                        }
                    });
                });
                
            });
            return;
        }
        
        /* PLAYER IS OFFLINE */
        found.filter(Filters.eq(FriendEnum.NAME.name(), username)).first((document, thw) -> {
            if (thw != null) {
                System.err.println(thw.fillInStackTrace());
            }
            
            if (document == null) {
                player.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                return;
            }
            
            found.filter(Filters.eq(FriendEnum.UUID.name(), player.getUniqueId())).first((foundDocument, throwable) -> {
                if (throwable != null) {
                    System.err.println(throwable.fillInStackTrace());
                }

                if (!((ArrayList)foundDocument.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(document.get(FriendEnum.UUID.name(), UUID.class))) {
                    player.sendMessage(this.plugin.getPREFIX() + "§7Du bist nicht mit §b" + username + " §7befreundet.");
                    return;
                }
                
                ((ArrayList)foundDocument.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).remove(document.get(FriendEnum.UUID.name(), UUID.class));
                
                
                /* UPDATE TARGET DOCUMENT IN DATABASE */
                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq(FriendEnum.UUID.name(), player.getUniqueId()), foundDocument, (updateResult, arg1) -> {
                    if (updateResult != null) {
                        player.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundschaft mit §b" + target.getName() + " §7beendet.");
                        removeFavorite(player, username);
                    }
                });
            });
            
        });
        
    }
    //</editor-fold>
    
    /**
     * @param sender is the executer of the command
     */
    //<editor-fold defaultstate="collapsed" desc="listFriends">
    public void listFriends(ProxiedPlayer sender) {
        found.filter(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId())).first((document, throwable) -> {
            ArrayList<UUID> friendList = ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class));
            
            if(friendList.isEmpty()) {
                sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§cLeider hast du noch keine Freunde hinzugefügt."));
                return;
            }
            
            sender.sendMessage(new TextComponent("§3Freundes Liste§7:"));
            friendList.forEach(uuids -> {
                ProxiedPlayer targets = plugin.getProxy().getPlayer(uuids);
                
                if(targets != null) {
                    sender.sendMessage(new TextComponent("§7- §a" + targets.getName() + "§7: §a§lOnline"));
                }else {
                    found.filter(Filters.eq("UUID", uuids)).first((foundDocument, thrw) -> {
                        String name = foundDocument.getString(FriendEnum.NAME.name());
                        sender.sendMessage(new TextComponent("§7- §a" + name + "§7: §c§lOffline"));
                    });
                }
                
            });
        });
    }
     //</editor-fold>
    
    /**
     * @param sender is the executer of the command
     * @param username is the name of the target
     */
    //<editor-fold defaultstate="collapsed" desc="jumpToFriend">
    public void jumpToFriend(ProxiedPlayer sender, String username) {
        ProxiedPlayer target = plugin.getProxy().getPlayer(username);
        
        if(target == null) {
            sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Der Spieler §9" + username + " §7konnte nicht gefunden werden."));
            return;
        }
        
        found.filter(Filters.eq(FriendEnum.UUID.name(), sender.getUniqueId())).first((document, throwable) -> {
            ArrayList<UUID> friendList = ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class));
            if(friendList.contains(target.getUniqueId())) {
                found.filter(Filters.eq(FriendEnum.UUID.name(), target.getUniqueId())).first((foundDocument, thrw) -> {
                    if(foundDocument.getBoolean(FriendEnum.ALLOW_JUMP.name())) {
                        if(sender.getServer().equals(target.getServer())) return;
                        
                        sender.connect(target.getServer().getInfo());
                        sender.sendMessage(plugin.getPREFIX() + "§7Du bist dem Spieler §9" + username + " §7nach gesprungen.");
                        return;
                    }
                    sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Du kannst dem Spieler §9" + username + " §7nicht nachspringen."));
                });
                return;
            }
            sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Der Spieler §9" + username + " §7ist nicht mit dir befreundet."));
        });
    }
    //</editor-fold>
    
    /**
     * 
     * @param sender is the executer of the command
     * @param settingsEnum is an enum to select the setting
     */
    //<editor-fold defaultstate="collapsed" desc="toggleSettings">
    public void toggleSettings(ProxiedPlayer sender, SettingsEnum settingsEnum) {
        found.filter(Filters.eq("UUID", sender.getUniqueId())).first((document, throwable) -> {
            switch(settingsEnum) {
                case JUMP:
                    if(document.getBoolean(FriendEnum.ALLOW_JUMP.name())) {
                        document.replace(FriendEnum.ALLOW_JUMP.name(), false);
                        this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", sender.getUniqueId()), document, (updateResult, th) -> {});
                        sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Dir kann nun keiner mehr §bnachspringen§7."));
                        break;
                    }
                    document.replace(FriendEnum.ALLOW_JUMP.name(), true);
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", sender.getUniqueId()), document, (updateResult, th) -> {});
                    sender.sendMessage(new TextComponent(this.plugin.getPREFIX() + "§7Deine §3Freunde §7können nun wieder §bnachspringen§7."));
                    break;
                case MESSAGE:
                    if(document.getBoolean(FriendEnum.ALLOW_MESSAGE.name())) {
                        document.replace(FriendEnum.ALLOW_MESSAGE.name(), false);
                        sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Du kannst nun keine §bNachrichten §7mehr erhalten."));
                        break;
                    }
                    document.replace(FriendEnum.ALLOW_MESSAGE.name(), true);
                    sender.sendMessage(new TextComponent(this.plugin.getPREFIX() + "§7Du kannst nun wieder §bNachrichten §7erhalten."));
                    break;
                case PARTY:
                    if(document.getBoolean(FriendEnum.ALLOW_PARTY.name())) {
                        document.replace(FriendEnum.ALLOW_PARTY.name(), false);
                        sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Du kannst nun keine §bParty Anfragen §7erhalten."));
                        break;
                    }
                    document.replace(FriendEnum.ALLOW_PARTY.name(), true);
                    sender.sendMessage(new TextComponent(this.plugin.getPREFIX() + "§7Du kannst nun wieder §bParty Anfragen §7erhalten.."));
                    break;
                case REQUEST:
                    if(document.getBoolean(FriendEnum.ALLOW_FRIEND.name())) {
                        document.replace(FriendEnum.ALLOW_FRIEND.name(), false);
                        sender.sendMessage(new TextComponent(this.plugin.getPREFIX() + "§7Du kannst nun keine §bAnfragen §7mehr erhalten"));
                        break;
                    }
                    document.replace(FriendEnum.ALLOW_FRIEND.name(), true);
                    sender.sendMessage(new TextComponent(this.plugin.getPREFIX() + "§7Du kannst nun wieder §bAnfragen §7erhalten."));
                    break;
            }
        });
    }
    //</editor-fold>
    
}
