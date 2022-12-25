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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thing  -- an inventory item or functional object used in the game.
 *
 * @author Mark J Koch [ @maehem on GitHub ]
 */
public abstract class Thing {
    private final static Logger LOG = Logger.getLogger(Thing.class.getName());

    private String name;

    public Thing() {}
    
    public Thing( String name ) {
        this.name = name;
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
        if ( name == null ) {
            return;
        }
        p.setProperty(key + ".name", getName());
        p.setProperty(key + ".class", getClass().getSimpleName());

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
     * class and call super( p, key) to make sure these basic things are
     * handled. The overriding class should then use similar syntax as below to
     * load custom values.
     *
     * @param p @Properties object from game engine
     * @param key key base to parse from
     */
    public void loadState(Properties p, String key) {
        setName(p.getProperty(key + ".name", getName()));

        LOG.log(Level.INFO, "Thing.loadState():  Loading props for:" + key + " with name: " + getName());
        LOG.log( Level.INFO, "    Props: " + p.toString());
        // Process sub-class properties by filtering them and stripping
        // off the key prefiex.
        Properties sp = new Properties();

        p.forEach((k, v) -> {
            String itemKey = (String) k;
            if (itemKey.startsWith(key + ".")
                    && !itemKey.startsWith(key + ".name")
                    && !itemKey.startsWith(key + ".class")
            ) {
                String shortKey = itemKey.substring(key.length() + 1);  // Plus one is for dot-separator character
                //LOG.log(Level.INFO, "    mainKey: {0}    longKey: {1} ==> shortKey: {2}", new Object[]{key, k,shortKey});
                sp.setProperty(shortKey, (String) v);
            }
        });
        if ( !sp.isEmpty() ) {
                LOG.log(Level.INFO, "    {0} .: {1} :. has {2} properties to process.", 
                        new Object[]{key,getClass().getSimpleName(),sp.size()});
                LOG.log(Level.INFO, "        {0}", sp.toString());
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
        return -1;
    }
    
    public int getMaxCondition() {
        return 0;
    }

    public boolean repairable() {
        return false;
    }
    
    public boolean canRepair( Character c) {
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
    
}
