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
import static com.maehem.flatlinejack.Engine.LOGGER;
import static com.maehem.flatlinejack.engine.Player.INVENTORY_KEY;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.Properties;
import java.util.logging.Level;
import javafx.scene.paint.Color;

/**
 * Thing -- an inventory item or functional object used in the game.
 *
 * @author Mark J Koch [ @maehem on GitHub ]
 */
public abstract class Thing {
    //private final static Logger LOG = Logger.getLogger(Thing.class.getName());

    public final static int DEFAULT_VALUE = 0;
    protected static final int CONDITION_DEFAULT = 1000;
    protected static final int CONDITION_MAX = DeckThing.CONDITION_DEFAULT;
    protected static final String PROPERTY_CONDITION = "condition";

    private String name;
    private Color color = Color.DARKGREY;
    private int value;
    private int repairSkill;
    private int condition = CONDITION_DEFAULT;

    public Thing() {
    }

    public Thing(String name) {
        this.name = name;
        this.value = DEFAULT_VALUE;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public abstract String getPackage();

    public abstract String getDescription();

    /**
     * Save important state values on game save.<p>
     *
     * If a subclass has custom properties to store, then it will use the
     * abstract saveProperties() method.
     *
     * @param key of thing
     * @param p @Properties object from game engine
     */
    public void saveState(String key, Properties p) {
        //String key = GameState.PLAYER_INVENTORY + "." + slot;
        if (name == null) {
            return;
        }
        LOGGER.log(Level.INFO, "Save " + getClass().getSimpleName());
        p.setProperty(key + ".class", getPackage() + "." + getClass().getSimpleName());
//        if ( getValue() != DEFAULT_VALUE ) {
//            p.setProperty(key + ".value", String.valueOf(getValue()));
//        }
        if (getCondition() != getMaxCondition()) {
            p.setProperty(key + "." + PROPERTY_CONDITION, String.valueOf(condition));
            LOGGER.log(Level.INFO, getClass().getSimpleName() + ":: Save property: " + PROPERTY_CONDITION + " = " + getCondition());
        } else {
            LOGGER.log(Level.INFO, "    condition is: " + getCondition() + " which is the default of: " + CONDITION_MAX);
        }

        // Gather any custom value from subclass and store those too.
        Properties saveProperties = saveProperties();
        saveProperties.forEach(
                (k, v) -> {
                    p.setProperty(key + "." + k, (String) v);
                }
        );
    }

    /**
     * Load important state values on game load.<p>
     *
     * If a subclass has custom properties to load, then it should override this
     * class and call super( p, keyPrefix) to make sure these basic things are
     * handled. The overriding class should then use similar syntax as below to
     * load custom values.
     *
     * @param p @Properties object from game engine
     * @param keyPrefix keyPrefix base to parse from
     */
    public void loadState(Properties p, String keyPrefix) {
        setName(p.getProperty(keyPrefix + ".name", getName()));
//        setValue(Integer.parseInt(p.getProperty(keyPrefix + ".value", String.valueOf(getValue() )
//        )));

        LOGGER.log(Level.INFO, "Thing.loadState():  Loading props for:{0} with name: {1}", new Object[]{keyPrefix, getName()});

        String conditionValue = p.getProperty(keyPrefix + "." + PROPERTY_CONDITION);
        if (conditionValue != null) {
            setCondition(Integer.parseInt(conditionValue));
            LOGGER.log(Level.FINER, "{0}:: Load property: " + PROPERTY_CONDITION + " = {1}", new Object[]{getClass().getSimpleName(), getCondition()});
        }

        LOGGER.log(Level.FINER, "    Props: {0}", p.toString());
        // Process sub-class properties by filtering them and stripping
        // off the keyPrefix prefiex.
        Properties sp = new Properties();

        p.forEach((k, v) -> {
            String itemKey = (String) k;
            if (itemKey.startsWith(keyPrefix + ".")
                    && !itemKey.startsWith(keyPrefix + ".name")
                    && !itemKey.startsWith(keyPrefix + ".class")) {
                String shortKey = itemKey.substring(keyPrefix.length() + 1);  // Plus one is for dot-separator character
                //LOG.log(Level.INFO, "    mainKey: {0}    longKey: {1} ==> shortKey: {2}", new Object[]{keyPrefix, k,shortKey});
                sp.setProperty(shortKey, (String) v);
            }
        });
        if (!sp.isEmpty()) {
            LOGGER.log(Level.FINER, "    {0} .: {1} :. has {2} properties to process.",
                    new Object[]{keyPrefix, getClass().getSimpleName(), sp.size()});
            LOGGER.log(Level.FINEST, "        {0}", sp.toString());
        }
        loadProperties(sp);
    }

    /**
     * Store custom subclass properties.
     *
     * Suggested implementation in subclass, as follows:<br><br>
     *
     * <pre><code>
     *   Properties p = new Properties();
     *   p.setProperty("myProperty", myProperty.toString());
     *
     *   return p;
     * </code></pre>
     *
     * @return custom properties to store
     */
    public abstract Properties saveProperties();

    /**
     * Load in any stored properties for subclass objects.
     *
     * @param p
     */
    public abstract void loadProperties(Properties p);

    public abstract String getIconPath();

    public int getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
        if (condition > getMaxCondition()) {
            this.condition = getMaxCondition();
        }
    }

    /**
     * Get the max condition acheivable. Subclasses should override this.
     *
     * @return maximum possible condition value
     */
    public int getMaxCondition() {
        return 0;
    }

    public boolean repairable() {
        return false;
    }

    public boolean canRepair(Character c) {
        return false;
    }

    public boolean canDelete() {
        return false;
    }

    public boolean canGive() {
        return false;
    }

    public boolean canUse() {
        return false;
    }

    public boolean needsRepair() {
        return false;
    }

    public Integer getRepairSkill() {
        return repairSkill;
    }

    public void setRepairSkill(int level) {
        this.repairSkill = level;
        if (level < 0) {
            this.repairSkill = 0;
        }
    }

    public void adjustCondition(int amount) {
        this.condition += amount;
        // Range check and adjust
        if (condition < 0) {
            this.condition = 0;
        }
        if (this.condition > getMaxCondition()) {
            this.condition = getMaxCondition();
        }
    }

    public static final Thing factory( String key, Properties p ) {
        //String key = INVENTORY_KEY + "." + i;
        //LOGGER.log(Level.INFO, "Player Inventory Item: " + key);
        String itemClass = p.getProperty(key + ".class");
        if (itemClass != null) {
            try {
                Class<?> c = Class.forName(Engine.class.getPackageName() + ".content.things." + itemClass);
                Constructor<?> cons = c.getConstructor();
                Thing object = (Thing) cons.newInstance();
                //setAInventory(i, object);
                LOGGER.log(Level.FINER, "    {0}", key);
                //getAInventory().set(i, object);
                object.loadState(p, key);
                return object;
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
        return null;
    }
}
