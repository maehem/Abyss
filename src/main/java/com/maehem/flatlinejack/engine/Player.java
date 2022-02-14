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

import com.maehem.flatlinejack.Engine;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Player extends Character {
    private final static Logger LOG = Logger.getLogger(Player.class.getName());

    public static final String PLAYER_MONEY = "player.money";
    public static final String PLAYER_HEALTH = "player.health";
    public static final String PLAYER_CONSTITUTION = "player.constitution";
    public static final String PLAYER_NAME = "player.name";
    public static final String PLAYER_INVENTORY = "player.inventory";

    // DEFAULT VALUES
    public static final String PLAYER_NAME_DEFAULT = "Neo";
    public static final int PLAYER_MONEY_AMOUNT_DEFAULT = 23000;
    public static final int PLAYER_HEALTH_DEFAULT = 1000;
    public static final int PLAYER_CONSTITUTION_DEFAULT = 1000;

    private int money = PLAYER_MONEY_AMOUNT_DEFAULT;
    private int health = PLAYER_HEALTH_DEFAULT;
    private int constitution = PLAYER_CONSTITUTION_DEFAULT;
    //private final String name = PLAYER_NAME_DEFAULT;

    public Player() {
        setName(PLAYER_NAME_DEFAULT);
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
    }

    /**
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
    }

    /**
     * Save important state values on game save
     *
     * @param p @Properties object from game engine
     */
    public void saveState(Properties p) {
        p.setProperty(PLAYER_NAME, getName());
        p.setProperty(PLAYER_MONEY, String.valueOf(getMoney()));
        p.setProperty(PLAYER_HEALTH, String.valueOf(getHealth()));
        p.setProperty(PLAYER_CONSTITUTION, String.valueOf(getConstitution()));

        for ( int i=0; i<getAInventory().size(); i++ ) {
            String key = PLAYER_INVENTORY + "." + i;
            getAInventory().get(i).saveState(key, p);
        }
    }

    /**
     * Load important state values on game load
     *
     * @param p @Properties object from game engine
     */
    public void loadState(Properties p) {
        setName(p.getProperty(PLAYER_NAME, "Jack"));
        setMoney(Integer.valueOf(p.getProperty(PLAYER_MONEY, String.valueOf(PLAYER_MONEY_AMOUNT_DEFAULT))));
        setHealth(Integer.valueOf(p.getProperty(PLAYER_HEALTH, String.valueOf(PLAYER_HEALTH_DEFAULT))));
        setConstitution(Integer.valueOf(p.getProperty(PLAYER_CONSTITUTION, String.valueOf(PLAYER_CONSTITUTION_DEFAULT))));

        for ( int i=0; i< getAInventory().size(); i++ ) {
            String key = PLAYER_INVENTORY + "." + i;
            String itemClass = p.getProperty(PLAYER_INVENTORY + "." + i + ".class");
            if ( itemClass != null ) {
                try {
                    Class<?> c = Class.forName(Engine.class.getPackageName() + ".content.things." + itemClass);
                    Constructor<?> cons = c.getConstructor();
                    Thing object = (Thing) cons.newInstance();
                    setAInventory(i, object);
                    getAInventory().set(i, object);
                    object.loadState(p, key);
                } catch (ClassNotFoundException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                
            }
        }

    }

    void changeHealth(int amount) {
        setHealth(getHealth() + amount);
        
    }

}
