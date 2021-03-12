/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.finn.friendsystem.objects;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author Finn
 */
public class TextComponentBuilder {
    
    private final String text;
    private String hover;
    private String click;
    private ClickEvent.Action action;
            
    public TextComponentBuilder(String text) {
        this.text = text;
    }
    
    /**
     * @param hover is the text that is displayed when you hover over the message
     * @return this class
     */
    //<editor-fold defaultstate="collapsed" desc="addHover">
    public TextComponentBuilder addHover(String hover) {
        this.hover = hover;
        return this;
    }
    //</editor-fold>
    
    /**
     * @param clickEventAction is the action that is performed when you click on the text
     * @param value is the string for the action
     * @return this class
     */
    //<editor-fold defaultstate="collapsed" desc="addClickEvent">
    public TextComponentBuilder addClickEvent(ClickEvent.Action clickEventAction, String value) {
        this.action = clickEventAction;
        this.click = value;
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="build">
    public TextComponent build() {
        TextComponent textComponent = new TextComponent();
        textComponent.setText(this.text);
        if(this.hover != null) {
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.hover).create()));
        }
        if(this.click != null && (this.action != null)) {
            textComponent.setClickEvent(new ClickEvent(action, this.click));
        }
        return textComponent;
    }
    //</editor-fold>
    
    public enum ClickEventType {
        RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
    }
}
