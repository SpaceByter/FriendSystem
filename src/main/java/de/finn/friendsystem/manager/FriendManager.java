/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.manager;

import de.finn.friendsystem.FriendSystem;
import com.mongodb.async.client.FindIterable;
import com.mongodb.client.model.Filters;
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

    public FriendManager(FriendSystem plugin) {
        this.plugin = plugin;
    }
    
    //<editor-fold defaultstate="collapsed" desc="sendFriendRequest">
    public void sendFriendRequest(ProxiedPlayer sender, String username) {
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        if (target != null) {
            found.filter(Filters.eq("UUID", target.getUniqueId())).first((t, throwablee) -> {
                if (t == null) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                } else if (((ArrayList)t.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).size() >= 30) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§b" + username + " §7hat die maximalen Freundesanfragen erreicht.");
                } else if (((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist bereits mit §b" + target.getName() + " §7befreundet.");
                } else if (((ArrayList)t.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast dem Spieler §b" + target.getName() + " §7bereits eine Anfrage gesendet.");
                } else if (((ArrayList)t.get(FriendEnum.BLOCK_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du kannst §b" + target.getName() + " §7keine Freundschaftsanfrage senden.");
                } else {
                    ((ArrayList)t.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).add(sender.getUniqueId());
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", target.getUniqueId()), t, (updateResult, thw) -> {
                        if (updateResult == null && thw != null) {
                            sender.sendMessage(this.plugin.getPREFIX() + "§7Es liegt ein Problem vor bitte melde den Bug!");
                        } else {
                            sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast §b" + target.getName() + " §7eine Anfrage gesendet.");
                            target.sendMessage(this.plugin.getPREFIX() + "§9" + sender.getName() + " §7möchte dein §3Freund §7werden:");
                            target.sendMessage(new TextComponent(plugin.getPREFIX()), new TextComponentBuilder("§7[§aAnnehmen§7]").addClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + sender.getName()).build(),
                            new TextComponent(" "),
                            new TextComponentBuilder("§7[§cAblehnen§7]").addClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + sender.getName()).build());
                        }
                    });
                }
            });
        } else {
            found.filter(Filters.eq("NAME", username)).first((t, throwable) -> {
                if (t == null) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                } else if (((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist breits mit §3" + username + " §7befreundet");
                } else if (((ArrayList)t.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast dem Spieler §b" + target.getName() + " §7bereits eine Anfrage gesendet.");
                } else if (((ArrayList)t.get(FriendEnum.BLOCK_LIST.name(), (Class)ArrayList.class)).contains(sender.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du kannst §b" + target.getName() + " §7keine Freundschaftsanfrage senden.");
                } else {
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("NAME", username), t, (updateResult, thw) -> {
                        if (updateResult == null && thw != null) {
                            sender.sendMessage(this.plugin.getPREFIX() + "§7Es liegt ein Problem vor bitte melde den Bug!");
                        } else {
                            sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast §b" + username + " §7eine Anfrage gesendet.");
                        }
                    });
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="acceptFriendRequest">
    public void acceptFriendRequest(ProxiedPlayer sender, String username) {
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        if (target != null) {
            found.filter(Filters.eq("UUID", sender.getUniqueId())).first((document, throwable) -> {
                if (document != null && throwable == null) {
                    if (!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(target.getUniqueId())) {
                        sender.sendMessage(this.plugin.getPREFIX() + "§b" + target.getName() + " §7hat dir keine Anfrage gesendet.");
                    } else if (((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).size() >= 50) {
                        sender.sendMessage(this.plugin.getPREFIX() + "§7Deine Freundesliste ist voll.");
                    } else {
                        ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).remove(target.getUniqueId());
                        ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).add(target.getUniqueId());
                        found.filter(Filters.eq("UUID", target.getUniqueId())).first((t, thow) -> {
                            if (((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).size() >= 35) {
                                sender.sendMessage(this.plugin.getPREFIX() + "§7Die Freundesliste von §b" + target.getName() + " §7ist voll.");
                            } else {
                                ((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).add(sender.getUniqueId());
                                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("NAME", username), t, (updateResult, th) -> {
                                });
                                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", target.getUniqueId()), t, (updateResult, th) -> {
                                    if (updateResult != null) {
                                        target.sendMessage(this.plugin.getPREFIX() + "§7Du bist nun mit §b" + sender.getName() + " §7befreundet.");
                                    }
                                    
                                });
                                this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", sender.getUniqueId()), document, (updateResult, arg1) -> {
                                    if (updateResult != null) {
                                        sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist nun mit §b" + target.getName() + " §7befreundet.");
                                    }
                                    
                                });
                            }
                        });
                    }
                } else {
                    if (throwable != null) {
                        System.err.println(throwable.fillInStackTrace());
                    }
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Es liegt ein Problem vor bitte melde den Bug!");
                }
            });
        } else {
            found.filter(Filters.eq("NAME", username)).first((t, thow) -> {
                if (t == null) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                } else if (((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).size() >= 50) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Die Freundesliste von §b" + username + " §7ist voll.");
                } else {
                    ((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).add(sender.getUniqueId());
                    found.filter(Filters.eq("UUID", sender.getUniqueId())).first((document, Throwable) -> {
                        if (!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(t.getString("UUID"))) {
                            sender.sendMessage(this.plugin.getPREFIX() + "§b" + username + " §7hat dir keine Anfrage gesendet.");
                        } else if (((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).size() >= 35) {
                            sender.sendMessage(this.plugin.getPREFIX() + "§7Deine Freundesliste ist voll.");
                        } else {
                            ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).add(t.getString("UUID"));
                            this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", sender.getUniqueId()), document, (updateResult, arg1) -> {
                                if (updateResult != null) {
                                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du bist nun mit §b" + username + " §7befreundet.");
                                }
                            });
                        }
                    });
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="denyFriendRequest">
    public void denyFriendRequest(ProxiedPlayer sender, String username) {
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        if (target != null) {
            found.filter(Filters.eq("UUID", sender.getUniqueId())).first((document, throwable) -> {
                if (!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(target.getUniqueId())) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§b" + target.getName() + " §7hat dir keine Anfrage gesendet.");
                } else {
                    ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).remove(target.getUniqueId());
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", sender.getUniqueId()), document, (updateResult, thw) -> {
                        if (updateResult != null) {
                            sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundesanfrage von §b" + target.getName() + " §7abgelehnt.");
                        }
                    });
                }
            });
        } else {
            found.filter(Filters.eq("NAME", username)).first((t, thw) -> {
                if (t == null) {
                    sender.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                } else {
                    found.filter(Filters.eq("UUID", sender.getUniqueId())).first((document, thww) -> {
                        if (!((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).contains(t.getString("UUID"))) {
                            sender.sendMessage(this.plugin.getPREFIX() + "§b" + username + " §7hat dir keine Anfrage gesendet.");
                        } else {
                            ((ArrayList)document.get(FriendEnum.REQUEST_LIST.name(), (Class)ArrayList.class)).remove(t.getString("UUID"));
                            this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", sender.getUniqueId()), document, (updateResult, thw2) -> {
                                if (updateResult != null) {
                                    sender.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundesanfrage von §b" + target.getName() + " §7abgelehnt.");
                                }
                            });
                        }
                    });
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeFriend">
    public void removeFriend(ProxiedPlayer player, String username) {
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        ProxiedPlayer target = this.plugin.getProxy().getPlayer(username);
        if (target != null) {
            found.filter(Filters.eq("UUID", player.getUniqueId())).first((document, throwable) -> {
                if (throwable != null) {
                    System.err.println(throwable.fillInStackTrace());
                }
                if (!((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(target.getUniqueId())) {
                    player.sendMessage(this.plugin.getPREFIX() + "§7Du bist nicht mit §b" + username + " §7befreundet.");
                } else {
                    ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).remove(target.getUniqueId());
                    this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", player.getUniqueId()), document, (updateResult, arg1) -> {
                        if (updateResult != null) {
                            player.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundschaft mit §b" + target.getName() + " §7beendet.");
                        }
                    });
                    found.filter(Filters.eq("UUID", target.getUniqueId())).first((t, thw) -> {
                        ((ArrayList)t.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).remove(player.getUniqueId());
                        this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", target.getUniqueId()), t, (updateResult, th) -> {
                            if (updateResult != null) {
                                target.sendMessage(this.plugin.getPREFIX() + "§b" + player.getName() + " §7hat die Freundschaft beendet.");
                            }
                        });
                    });
                }
            });
        } else {
            found.filter(Filters.eq("NAME", username)).first((t, thw) -> {
                if (thw != null) {
                    System.err.println(thw.fillInStackTrace());
                }
                
                if (t == null) {
                    player.sendMessage(this.plugin.getPREFIX() + "§7Der Spieler §b" + username + " §7war noch nie auf dem Netzwerk.");
                } else {
                    found.filter(Filters.eq("UUID", player.getUniqueId())).first((document, throwable) -> {
                        if (throwable != null) {
                            System.err.println(throwable.fillInStackTrace());
                        }
                        
                        if (!((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).contains(t.getString("UUID"))) {
                            player.sendMessage(this.plugin.getPREFIX() + "§7Du bist nicht mit §b" + username + " §7befreundet.");
                        } else {
                            ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class)).remove(t.getString("UUID"));
                            this.plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("UUID", player.getUniqueId()), document, (updateResult, arg1) -> {
                                if (updateResult != null) {
                                    player.sendMessage(this.plugin.getPREFIX() + "§7Du hast die Freundschaft mit §b" + target.getName() + " §7beendet.");
                                }
                            });
                        }
                    });
                }
            });
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="listFriends">
    public void listFriends(ProxiedPlayer sender) {
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        found.filter(Filters.eq("UUID", sender.getUniqueId())).first((document, throwable) -> {
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
                    found.filter(Filters.eq("UUID", uuids)).first((d, t) -> {
                        String name = d.getString(FriendEnum.NAME.name());
                        sender.sendMessage(new TextComponent("§7- §a" + name + "§7: §c§lOffline"));
                    });
                }
                
            });
        });
    }
     //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="jumpToFriend">
    public void jumpToFriend(ProxiedPlayer sender, String username) {
        ProxiedPlayer target = plugin.getProxy().getPlayer(username);
        
        if(target == null) {
            sender.sendMessage(new TextComponent(plugin.getPREFIX() + "§7Der Spieler §9" + username + " §7konnte nicht gefunden werden."));
            return;
        }
        
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        found.filter(Filters.eq("UUID", sender.getUniqueId())).first((document, throwable) -> {
            ArrayList<UUID> friendList = ((ArrayList)document.get(FriendEnum.FRIEND_LIST.name(), (Class)ArrayList.class));
            if(friendList.contains(target.getUniqueId())) {
                found.filter(Filters.eq("UUID", target.getUniqueId())).first((d, t) -> {
                    if(d.getBoolean(FriendEnum.ALLOW_JUMP.name())) {
                        if(sender.getServer().equals(target.getServer())) {
                            return;
                        }
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
    
    //<editor-fold defaultstate="collapsed" desc="toggleSettings">
    public void toggleSettings(ProxiedPlayer sender, SettingsEnum settingsEnum) {
        
        FindIterable<Document> found = this.plugin.getMongoManager().getPlayers().find();
        
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
