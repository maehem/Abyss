/*
    Licensed to the Apache Software Foundation (ASF) under one or more 
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF 
    licenses this file to you under the Apache License, Version 2.0 
    (the "License"); you may not use this file except in compliance with the 
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the 
    License for the specific language governing permissions and limitations 
    under the License.
*/
package com.maehem.flatlinejack.engine;

import static com.maehem.flatlinejack.Engine.LOGGER;

import com.maehem.flatlinejack.Engine;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Player extends Character implements GameStateListener {

    public static final String PLAYER_KEY = "player";
    public static final String MONEY_KEY        = PLAYER_KEY + "." + "money";
    public static final String BANK_MONEY_KEY   = PLAYER_KEY + "." + "bank";
    public static final String HEALTH_KEY       = PLAYER_KEY + "." + "health";
    public static final String CONSTITUTION_KEY = PLAYER_KEY + "." + "constitution";
    public static final String NAME_KEY         = PLAYER_KEY + "." + "name";
    public static final String INVENTORY_KEY    = PLAYER_KEY + "." + "inventory";
    public static final String CURRENT_DECK_KEY = PLAYER_KEY + "." + "deck";

    // DEFAULT VALUES
    public static final String PLAYER_NAME_DEFAULT      = "Jack";
    public static final String PLAYER_NAME_LONG         = "Jack Erek Morse";
    public static final long PLAYER_ID                  = 89472940724234l;
    public static final int PLAYER_MONEY_AMOUNT_DEFAULT = 23;
    public static final int PLAYER_BANK_MONEY_AMOUNT_DEFAULT = 3041;
    public static final int PLAYER_HEALTH_MAX           = 1000;
    public static final int PLAYER_CONSTITUTION_MAX     = 1000;

    private int money = PLAYER_MONEY_AMOUNT_DEFAULT;
    private int bankMoney = PLAYER_BANK_MONEY_AMOUNT_DEFAULT;
    private int health = PLAYER_HEALTH_MAX;
    private int constitution = PLAYER_CONSTITUTION_MAX;
    
    private DeckThing currentDeck = null;
    
    private final GameState gameState;

    public Player(GameState gs) {
        super( PLAYER_NAME_DEFAULT );
        this.gameState = gs;
        gs.addListenter(this);
        //setName(PLAYER_NAME_DEFAULT);
    }

    /**
     * If game state asks for a player value to update a GUI it might be
     * asked for this way.
     * 
     * @param key to ask for ( i.e.  "player.money"
     * @return requested value as a string
     */
    public String getProperty(String key) {
        switch (key) {
            case CONSTITUTION_KEY:  return String.valueOf(getConstitution());
            case HEALTH_KEY:        return String.valueOf(getHealth());
            case MONEY_KEY:         return String.valueOf(getMoney());
            case BANK_MONEY_KEY:    return String.valueOf(getBankMoney());
            case NAME_KEY:          return getName();
            default:                return null;
        }
    }

    /**
     * @return the health
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(int health) {
        this.health = health;
        gameState.notifyPlayerStateChanged(HEALTH_KEY);
    }

    /**
     * Add or remove health value.
     * Performs range checking.
     * 
     * @param amt to change
     */
    public void addHealth( int amt ) {
        int newValue = this.health + amt;
        if ( newValue < 0 ) {
            newValue = 0;
        } else if ( newValue > PLAYER_HEALTH_MAX ) {
            newValue = PLAYER_HEALTH_MAX;
        }
        if ( newValue != this.health ) {
            setHealth(newValue);
        }
    }

    /**
     * @return the constitution
     */
    public int getConstitution() {
        return constitution;
    }

    /**
     * @param constitution the constitution to set
     */
    public void setConstitution(int constitution) {
        this.constitution = constitution;
        gameState.notifyPlayerStateChanged(CONSTITUTION_KEY);
    }

    /**
     * Add or remove constitution value.
     * Performs range checking.
     * 
     * @param amt to change
     */
    public void addConstitution( int amt ) {
        int newValue = this.constitution + amt;
        if ( newValue < 0 ) {
            newValue = 0;  // player death?
        } else if ( newValue > PLAYER_CONSTITUTION_MAX ) {
            newValue = PLAYER_CONSTITUTION_MAX;
        }
        if ( newValue != this.constitution ) {
            setConstitution(newValue);
        }
    }
    
    /**
     * Return the amount of money the player is carrying.
     * 
     * @return the money
     */
    public int getMoney() {
        return money;
    }

    /**
     * @param money the money player carries with them
     */
    public void setMoney(int money) {
        this.money = money;
        gameState.notifyPlayerStateChanged(MONEY_KEY);
    }

    public void addMoney( int amount ) {
        this.money += amount;
        gameState.notifyPlayerStateChanged(MONEY_KEY);
    }
    
    /**
     * Return the amount of money the player has in bank.
     * 
     * @return the money
     */
    public int getBankMoney() {
        return bankMoney;
    }

    /**
     * @param money the money player has in bank
     */
    public void setBankMoney(int money) {
        this.bankMoney = bankMoney;
        gameState.notifyPlayerStateChanged(BANK_MONEY_KEY);
    }
    
    public void addBankMoney( int amount ) {
        this.bankMoney += amount;
        gameState.notifyPlayerStateChanged(BANK_MONEY_KEY);
    }

    @Override
    public void setAInventory(int index, Thing thing) {
        super.setAInventory(index, thing);
        gameState.notifyPlayerStateChanged(INVENTORY_KEY);
    }

    public DeckThing getCurrentDeck() {
        return currentDeck;
    }
    
    public void setCurrentDeck( DeckThing d ) {
        if ( currentDeck != null ) {
            // TODO Unregister current deck.
        }
        this.currentDeck = d;
    }
    
    /**
     * Save important state values on game save
     *
     * @param p @Properties object from game engine
     */
    public void saveState(Properties p) {
        p.setProperty(NAME_KEY, getName());
        p.setProperty(MONEY_KEY, String.valueOf(getMoney()));
        p.setProperty(HEALTH_KEY, String.valueOf(getHealth()));
        p.setProperty(CONSTITUTION_KEY, String.valueOf(getConstitution()));
        if ( currentDeck != null ) {
            p.setProperty(CURRENT_DECK_KEY, String.valueOf("deck" + "." + currentDeck.getClass().getSimpleName()));
        }

        LOGGER.log(Level.WARNING, "Save Inventory.");
        for ( int i=0; i<getAInventory().size(); i++ ) {
            String key = INVENTORY_KEY + "." + i;
            Thing t = getAInventory().get(i);
            //if ( t instanceof EmptyThing ) {
            //    LOGGER.log(Level.INFO, "Player Thing SaveState: EmptyThing will not be saved.");
            //} else {
//                LOGGER.log(Level.INFO, "Player Thing SaveState: {0} will be saved.", t.getClass().getSimpleName());
                t.saveState(key, p);
            //}
        }
    }

    /**
     * Load important state values on game load
     *
     * @param p @Properties object from game engine
     */
    public void loadState(Properties p) {
        LOGGER.log(Level.INFO, "Initialize player settings from save file.");
        setName(p.getProperty(NAME_KEY, "Jack"));
        setMoney(Integer.parseInt(p.getProperty(MONEY_KEY, String.valueOf(PLAYER_MONEY_AMOUNT_DEFAULT))));
        setHealth(Integer.parseInt(p.getProperty(HEALTH_KEY, String.valueOf(PLAYER_HEALTH_MAX))));
        setConstitution(Integer.parseInt(p.getProperty(CONSTITUTION_KEY, String.valueOf(PLAYER_CONSTITUTION_MAX))));

        for ( int i=0; i< getAInventory().size(); i++ ) {
            String key = INVENTORY_KEY + "." + i;
            LOGGER.log(Level.INFO, "Player Inventory Item: " + key);
            String itemClass = p.getProperty(INVENTORY_KEY + "." + i + ".class");
            if ( itemClass != null ) {
                try {
                    Class<?> c = Class.forName(Engine.class.getPackageName() + ".content.things." + itemClass);
                    Constructor<?> cons = c.getConstructor();
                    Thing object = (Thing) cons.newInstance();
                    setAInventory(i, object);
                    LOGGER.log(Level.INFO, "    {0}", key);
                    //getAInventory().set(i, object);
                    object.loadState(p, key);
                } catch (ClassNotFoundException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                
            }
        }
        
        String deck = p.getProperty(CURRENT_DECK_KEY, null);

        if ( deck != null ) {
            // Load current deck if we have it in inventory
            for ( Thing t: getAInventory() ) {
                if ( (t instanceof DeckThing) && t.getClass().getName().endsWith(deck) ) { // "deck.MyDeckName"
                    currentDeck = (DeckThing) t;
                    LOGGER.log(Level.INFO, "===> Player Current Deck: {0}", currentDeck.getName());
                }
            }
        }
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {}

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {}

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}

    @Override
    public void gameStateDisplayChanged(GameState aThis, GameState.Display d) {}

}
