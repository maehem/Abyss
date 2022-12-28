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
package com.maehem.flatlinejack.content.things;

import com.maehem.flatlinejack.content.things.software.EmptySoftwareThing;
import com.maehem.flatlinejack.content.things.ram.EmptyRamThing;
import com.maehem.flatlinejack.Engine;
import com.maehem.flatlinejack.engine.Character;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Thing;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract Cyberspace Deck.
 * 
 * 
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public abstract class DeckThing extends Thing {
    private final static Logger LOG = Logger.getLogger(Thing.class.getName());

    private static final String PROPERTY_CONDITION = "condition";
    private static final String PROPERTY_SHEILD = "shield";
    private static final String PROPERTY_BASE_RAM = "base-ram";
    private static final String PROPERTY_RAM_SLOT = "ram-slot";
    private static final String PROPERTY_SOFTWARE_SLOT = "software-slot";
    
    private static final int CONDITION_DEFAULT = 1000;
    private static final int SHIELD_DEFAULT = 300;
    private static final int RAM_DEFAULT = 128;
    private static final int SOFTWARE_SLOTS_DEFAULT = 20;
    private static final int REPAIR_SKILL_MIN = 1;
    
    private Integer condition = CONDITION_DEFAULT;
    private Integer shield = SHIELD_DEFAULT;
    private Integer ram = RAM_DEFAULT;
    
    private final ArrayList<SoftwareThing> softwareSlots = new ArrayList<>();
    private final ArrayList<RamThing> ramSlots = new ArrayList<>();
    

    public DeckThing( String name, int softwareCapacity, int ramSlots ) {
        super(name);
        setNumSoftwareSlots(softwareCapacity);
        setNumRamSlots(ramSlots);        
    }
    
    private  void setNumSoftwareSlots(int cap){
        // Fill the softwareSlots with EmptySoftwareThing placeholders.
        for ( int i=0; i< cap; i++ ) {
            softwareSlots.add(new EmptySoftwareThing());
        }
    }
    
    /**
     * Add software to first available slot.   Caller should first make
     * sure slots are available.
     * 
     * @param software 
     * @return  
     */
    public boolean addSoftware( SoftwareThing software ) {
        for ( int i=0; i< softwareSlots.size(); i++) {
            if ( softwareSlots.get(i) instanceof EmptySoftwareThing ) {
                softwareSlots.set(i, software);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return count of empty softwareSlots.
     * 
     * @return empty slot count
     */
    public int slotsAvailable() {
        return softwareSlots.stream()
                .filter((t) -> ( t instanceof EmptySoftwareThing ))
                .map((_item) -> 1)
                .reduce(0, Integer::sum);
    }
    
    private  void setNumRamSlots(int nSlots){
        // Fill the softwareSlots with EmptySoftwareThing placeholders.
        for ( int i=0; i< nSlots; i++ ) {
           ramSlots.add(new EmptyRamThing());
        }
    }
    
    public int getNumRamSlots() {
        return ramSlots.size();
    }
    
    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canGive() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean canRepair(Character p) {
        // Check if Player has repair skill
        if ( p instanceof Player ) return true;
        
        // TODO:  It's possible some other characters can repair this.
        
        
        return false;
    }

    @Override
    public boolean needsRepair() {
        return condition < .95*CONDITION_DEFAULT;
    }

    @Override
    public int getCondition() {
        return condition;
    }
    
    public void setCondition(Integer condition) {
        this.condition = condition;
        //conditionGauge.setValue(condition);
    }

    public int getShield() {
        int sh = shield;
        // TODO:  Add shield buffs from installed software.
        
        return sh;
    }
    
    public ListIterator<RamThing> getRamModules() {
        return ramSlots.listIterator();
    }
    
    public int getRam() {
        int cap = ram;
        // TODO: Return total of base + installed RAM things.
        for ( RamThing mod: ramSlots ) {
            cap += mod.getCapacity();
        }
        
        return cap;
    }

    @Override
    public String getPackage() {
        return "deck";
    }
    
    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
        p.setProperty(PROPERTY_CONDITION, condition.toString());
        p.setProperty(PROPERTY_BASE_RAM, ram.toString());
        
        for ( int i=0; i<ramSlots.size(); i++ ) {
            LOG.log(Level.INFO, "Save RAM Slot " + i);
            String key = PROPERTY_RAM_SLOT + "." + i;
            RamThing t = ramSlots.get(i);
            if (!(t instanceof EmptyRamThing)) {
                ramSlots.get(i).saveState(key, p);
            }
//            Properties rtp = ramSlots.get(i).saveProperties();
//            rtp.forEach((k, v) -> {
//                p.setProperty(key+"."+k.toString(), v.toString());
//            });
        }
        for ( int i=0; i<softwareSlots.size(); i++ ) {
            LOG.log(Level.INFO, "Save Software Slot " + i);
            String key = PROPERTY_SOFTWARE_SLOT + "." + i;
            SoftwareThing t = softwareSlots.get(i);
            if ( !(t instanceof EmptySoftwareThing) ) {
                t.saveState(key, p);
            }

//            Properties rtp = softwareSlots.get(i).saveProperties();
//            rtp.forEach((k, v) -> {
//                p.setProperty(key+"."+k.toString(), v.toString());
//            });
        }
        
        return p;
    }
    
    @Override
    public void loadProperties(Properties p) {
        LOG.log(Level.INFO, "    Load DeckThing properties...");
        setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
        setCondition(Integer.valueOf(p.getProperty(PROPERTY_BASE_RAM, String.valueOf(RAM_DEFAULT))));

        LOG.log(Level.INFO, "There are {0} RAM slots to process.", ramSlots.size());
        for ( int i=0; i< ramSlots.size(); i++ ) {   // Load Ram slots
            String key = PROPERTY_RAM_SLOT + "." + i;
            String itemClass = p.getProperty(key + ".class");
            if ( itemClass != null ) {
                LOG.log(Level.INFO, "    Try to load key: " + key + "  item class: " + itemClass);
                LOG.log(Level.INFO, "     ram slot props:" + p.toString());
                try {
                    LOG.log(Level.INFO, "        Process RamSlot." + i + " class:" + itemClass);
                    Class<?> c = Class.forName(Engine.class.getPackageName() + ".content.things." + itemClass);
                    Constructor<?> cons = c.getConstructor();
                    RamThing object = (RamThing) cons.newInstance();
                    // If we got this far then we made the RAM object.
                    // Add it to the slots.
                    ramSlots.set(i, object);
                    // load it's current state properties.
                    object.loadState(p, key);
                } catch (ClassNotFoundException | InstantiationException | 
                        IllegalAccessException | IllegalArgumentException | 
                        InvocationTargetException | NoSuchMethodException | 
                        SecurityException ex
                ) {
                    // If any of these exceptions happen, something is really broken.
                    LOG.log(Level.SEVERE, null, ex);
                }                
            }
        }
        LOG.log(Level.INFO, "There are {0} software slots to process.", softwareSlots.size());
        for ( int i=0; i< softwareSlots.size(); i++ ) {   // Load Ram slots
            String key = PROPERTY_SOFTWARE_SLOT + "." + i;
            String itemClass = p.getProperty(key + ".class");
            if ( itemClass != null ) {
                LOG.log(Level.INFO, "         Try to load key: " + key + "  item class: " + itemClass);
                LOG.log(Level.INFO, "     software slot props: " + p.toString());
                try {
                    LOG.log(Level.INFO, "        Process SoftwareSlot." + i + " class:" + itemClass);
                    Class<?> c = Class.forName(Engine.class.getPackageName() + ".content.things." + itemClass);
                    Constructor<?> cons = c.getConstructor();
                    SoftwareThing object = (SoftwareThing) cons.newInstance();
                    // If we got this far then we made the software object.
                    // Add it to the slots.
                    softwareSlots.set(i, object);
                    // load it's current state properties.
                    object.loadState(p, key);
                } catch (ClassNotFoundException | InstantiationException | 
                        IllegalAccessException | IllegalArgumentException | 
                        InvocationTargetException | NoSuchMethodException | 
                        SecurityException ex
                ) {
                    // If any of these exceptions happen, something is really broken.
                    LOG.log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }

}
