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
package com.maehem.abyss.engine;

import static com.maehem.abyss.Engine.LOGGER;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Abstract Cyberspace Deck.
 * 
 * 
 * @author Mark J Koch [@maehem on GitHub]
 */
public abstract class DeckThing extends Thing implements SoftwareUser {
    //private final static Logger LOG = Logger.getLogger(Thing.class.getName());

    private static final String THING_PKG = "deck";
    
    private static final String PROPERTY_SHIELD = "shield";
    private static final String PROPERTY_BASE_RAM = "base-ram";
    private static final String PROPERTY_RAM_SLOT = "ram-slot";
    private static final String PROPERTY_SOFTWARE_SLOT = "software-slot";
    
    //private static final int SHIELD_MAX = 300;
    //private static final int RAM_DEFAULT = 128;
    
    //private Integer condition = CONDITION_DEFAULT;
    private final int baseShieldMax;
    private final int baseRam; // = RAM_DEFAULT;
    
    private int baseShield; // = SHIELD_MAX;
    
    private final ArrayList<SoftwareThing> softwareSlots = new ArrayList<>();
    private final ArrayList<RamThing> ramSlots = new ArrayList<>();
    //private final int[] softwareLoadout = {-1, -1, -1, -1}; // index into softwareSlots
    private final int[] softwareLoadout = {0, 1, -1, 2}; // index into softwareSlots
    
    private Player player = null;
    

    public DeckThing( String name, int baseRam, int baseShield, int softwareCapacity, int ramSlots ) {
        super(name);
        this.baseRam = baseRam;
        this.baseShieldMax = baseShield;
        this.baseShield = baseShieldMax;
        setNumSoftwareSlots(softwareCapacity);
        setNumRamSlots(ramSlots);
    }

    public List<SoftwareThing> getSoftware() {
        return softwareSlots;
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
                software.setUser(this);
                return true;
            }
        }
        return false;
    }
    
    public boolean removeSoftware( SoftwareThing software ) {
        if ( softwareSlots.remove(software) ) {
            software.setUser(null);
            return true;
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
    
    public int[] getSoftwareLoadout() {
        return softwareLoadout;
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
        return getCondition() < .95*CONDITION_MAX;
    }

    @Override
    public int getMaxCondition() {
        return CONDITION_MAX;
    }

    /**
     * The current base shield of this Deck device. Used for storing current
     * value.
     * 
     * @return 
     */
    public int getBaseShield() {
        return baseShield;
    }
    
    public void setBaseShield(int value) {
        this.baseShield = value;
        if ( value > getMaxBaseShield() ) {
            this.baseShield = getMaxBaseShield();
        }
        if ( value < 0 ) {
            this.baseShield = 0;
        }
    }
    
    public int getMaxBaseShield() {
        return baseShieldMax;
    }
    
    /**
     * The combined current baseShield value for this device and any sub-device
 buffs.
     * 
     * @return 
     */
    public int getCombinedShield() {
        int sh = baseShield;
        // TODO:  Add baseShield buffs from installed software.
        
        return sh;
    }
    
    public ListIterator<RamThing> getRamModules() {
        return ramSlots.listIterator();
    }
    
    public int getBaseRam() {
        return baseRam;
    }
    
    /**
     * Get total installed RAM.
     * 
     * @return total baseRam. Base + Slots
     */
    public int getTotalRam() {
        int cap = baseRam;
        for ( RamThing mod: ramSlots ) {
            cap += mod.getCapacity();
        }
        
        return cap;
    }

    @Override
    public String getPackage() {
        return THING_PKG;
    }
    
    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
        if ( getBaseShield() != getMaxBaseShield() ) {
            p.setProperty(PROPERTY_SHIELD, String.valueOf(getBaseShield()));
        }
        p.setProperty(PROPERTY_BASE_RAM, String.valueOf(baseRam));
        
        for ( int i=0; i<ramSlots.size(); i++ ) {
            LOGGER.log(Level.FINER, "Save RAM Slot {0}", i);
            String key = PROPERTY_RAM_SLOT + "." + i;
            RamThing t = ramSlots.get(i);
            if (!(t instanceof EmptyRamThing)) {
                ramSlots.get(i).saveState(key, p);
            }
        }
        for ( int i=0; i<softwareSlots.size(); i++ ) {
            LOGGER.log(Level.FINER, "Save Software Slot {0}", i);
            String key = PROPERTY_SOFTWARE_SLOT + "." + i;
            SoftwareThing t = softwareSlots.get(i);
            if ( !(t instanceof EmptySoftwareThing) ) {
                t.saveState(key, p);
            }
        }
        
        return p;
    }
    
    public abstract Class<?> getContentClass(String className );
    
    @Override
    public void loadProperties(Properties p) {
        LOGGER.log(Level.INFO, "    Load DeckThing properties...");
        setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
        setBaseShield(Integer.parseInt(p.getProperty(PROPERTY_SHIELD, String.valueOf(baseShieldMax))));

        LOGGER.log(Level.INFO, "There are {0} RAM slots to process.", ramSlots.size());
        for ( int i=0; i< ramSlots.size(); i++ ) {   // Load Ram slots
            String key = PROPERTY_RAM_SLOT + "." + i;
            String itemClass = p.getProperty(key + ".class");
            if ( itemClass != null ) {
                LOGGER.log(Level.INFO, "    Try to load key: {0}  item class: {1}", new Object[]{key, itemClass});
                LOGGER.log(Level.INFO, "     ram slot props:{0}", p.toString());
                try {
                    LOGGER.log(Level.INFO, "        Process RamSlot.{0} class:{1}", new Object[]{i, itemClass});
                    //Class<?> c = Class.forName(getClass().getPackageName() + ".content.things." + itemClass);
                    Class<?> c = getContentClass(".content.things." + itemClass);
                    if ( c == null ) {
                        throw new ClassNotFoundException("Class not found: " + itemClass);
                    }
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
                    LOGGER.log(Level.SEVERE, null, ex);
                }                
            }
        }
        LOGGER.log(Level.INFO, "There are {0} software slots to process.", softwareSlots.size());
        for ( int i=0; i< softwareSlots.size(); i++ ) {   // Load Ram slots
            String key = PROPERTY_SOFTWARE_SLOT + "." + i;
            String itemClass = p.getProperty(key + ".class");
            if ( itemClass != null ) {
                LOGGER.log(Level.INFO, "         Try to load key: {0}  item class: {1}", new Object[]{key, itemClass});
                LOGGER.log(Level.INFO, "     software slot props: {0}", p.toString());
                try {
                    LOGGER.log(Level.INFO, "        Process SoftwareSlot.{0} class:{1}", new Object[]{i, itemClass});
                    //Class<?> c = Class.forName(getClass().getPackageName() + ".content.things." + itemClass);
                    Class<?> c = getContentClass(".content.things." + itemClass);
                    if ( c == null ) {
                        throw new ClassNotFoundException("Class not found: " + itemClass);
                    }
                    Constructor<?> cons = c.getConstructor();
                    SoftwareThing object = (SoftwareThing) cons.newInstance();
                    // If we got this far then we made the software object.
                    // Add it to the slots.
                    softwareSlots.set(i, object);
                    object.setUser(this);
                    // load it's current state properties.
                    object.loadState(p, key);
                } catch (ClassNotFoundException | InstantiationException | 
                        IllegalAccessException | IllegalArgumentException | 
                        InvocationTargetException | NoSuchMethodException | 
                        SecurityException ex
                ) {
                    // If any of these exceptions happen, something is really broken.
                    LOGGER.log(Level.SEVERE, "Could not load SoftwareSlot:" + itemClass, ex);
                }
                
            }
        }
    }

    @Override
    public void attack(SoftwareUser enemy, SoftwareThing tool) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
