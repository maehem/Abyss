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

import com.maehem.flatlinejack.engine.gui.InventoryPane;
import java.io.InputStream;
import java.util.Properties;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Thing  -- an obect or NPC in the scene.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public abstract class Thing {

    private String name;
    protected Button slotButton;
    protected Image image;
    private static final int SIZE = InventoryPane.CELL_SIZE;

    public Thing() {
        slotButton = slotButton();
        slotButton.setPrefSize(SIZE, SIZE);
        slotButton.setAccessibleHelp(name);
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
        Tooltip tooltip = new Tooltip(name);
        //tooltip.setShowDelay(Duration.millis(500));  // uncomment me when Java 9 is a thing.
        slotButton.setTooltip(tooltip);
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

        // Process sub-class properties by filtering them and stripping
        // off the key prefiex.
        Properties sp = new Properties();

        p.forEach((k, v) -> {
            String itemKey = (String) k;
            if (itemKey.startsWith(key)
                    && !itemKey.startsWith(key + ".name")
                    && !itemKey.startsWith(key + ".class")) {
                String shortKey = itemKey.substring(key.length() + 1);  // Plus one is for dot-separator character.
                sp.setProperty(shortKey, (String) v);
            }
        });

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

    public static Node getSlotButton(Thing t) {
        if (t == null) {
            Button b = slotButton();

            return b;
        } else {
            return t.slotButton;
        }
    }

    public final void setGraphic(InputStream is) {
        image = new Image(is);
        ImageView iv = new ImageView(image);
        //iv.setViewport(new Rectangle2D(SIZE, SIZE, SIZE, SIZE));
        iv.setPreserveRatio(true);
        iv.setFitWidth(SIZE - 20);
        //iv.setFitHeight(SIZE-10);

        slotButton.setGraphic(iv);
        slotButton.setText(null);
    }

    private static Button slotButton() {
        Button b = new Button();

        b.setTooltip(new Tooltip("Empty"));
        b.setMinSize(InventoryPane.CELL_SIZE, InventoryPane.CELL_SIZE);
        //b.setBackground(new Background(new BackgroundFill(Color.SLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        b.setStyle("-fx-base: #666666;");
        return b;
    }

    public Image getImage() {
        return image;
    }

    public abstract Pane getDetailPane();

    public boolean canRepair( Player p) {
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
