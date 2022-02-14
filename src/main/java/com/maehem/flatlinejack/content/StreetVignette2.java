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
package com.maehem.flatlinejack.content;

import com.maehem.flatlinejack.engine.Patch;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Port;
import com.maehem.flatlinejack.engine.PoseSheet;
import com.maehem.flatlinejack.engine.Vignette;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class StreetVignette2 extends Vignette {

    private ResourceBundle bundle;
    //private static final String NAME = "Street 2";
    private static final String PROP_NAME = "street2";
    private static final String CONTENT_BASE = "/content/vignette/street2/";
    private static final String DOOR_PATCH_IMAGE_FILENAME = CONTENT_BASE + "door-right-wing.png";
    private static final Polygon WALK_AREA = new Polygon(
            0, 370,
            530, 370,
            540, 320,
            620, 320,
            630, 370,
            870, 370,
            960, 440,
            1200, 440,
            1200, 482,
            1100, 482,
            1200, 700,
            0, 700
    );

    private static final Patch p = new Patch(1140, 0, 430, StreetVignette2.class.getResourceAsStream(DOOR_PATCH_IMAGE_FILENAME));
    
    private static final Port rightDoor = new Port(
            1140, 440, // Location
            22, 50, // Size
            PawnShopVignette.PLAYER_START_X, PawnShopVignette.PLAYER_START_Y, PoseSheet.Direction.RIGHT, // Player position and orientation at destination
            "PawnShopVignette" // Destination
    );
    private static final Port topDoor = new Port(
            540, 320,
            80, 10,
            550, 500, PoseSheet.Direction.AWAY,
            "DonutShopVignette");

    public StreetVignette2(int w, int h, Port prevPort, Player player) {
        super(w, h, CONTENT_BASE,prevPort, player);
    }

    /**
     *
     * @param input
     * @return
     */
    @Override
    public Port processEvents(ArrayList<String> input) {
        // You must pass the nextRoom value on.
        Port nextRoom = super.processEvents(input);

        // Simulate perspective when player walks closer/further from view.
        //getPlayer().setScale((8 * getPlayer().getLayoutY() / getHeight()) - 2.4);
        getPlayer().setScale((4.2 * getPlayer().getLayoutY() / getHeight()) - 1.15);

        // Test the gauges.
        //getPlayer().setMoney(getPlayer().getMoney()-1);
        //getPlayer().setHealth(getPlayer().getHealth()-1);
        //getPlayer().setConstitution(getPlayer().getConstitution()-1);
        
        
        return nextRoom;
    }

    @Override
    protected void init() {
        //setName(NAME);
        bundle = ResourceBundle.getBundle("content.messages.StreetVignette2");
        setName(bundle.getString("title"));
        getNarrationPane().setText(bundle.getString("narration"));
        
        //initBackground();
        initMainArea();
        initPatches();
        initForeground();
    }

    private void initMainArea() {
        // TODO:   Check that file exists.  The current exception message is cryptic.

        // Character
        getPlayer().setLayoutX(getWidth() / 2); //  Near middle
        //getPlayer().setLayoutY(2.3 * getHeight() / 4);  // Head about a 1/3 down
        getPlayer().setLayoutY(2*getHeight()/4);  // Head about a 1/3 down
    //getPlayer().setLayoutX(250); //  Near middle
    //getPlayer().setLayoutY(540);  // Head about a 1/3 down

        getMainGroup().getChildren().addAll(
                getPlayer()
        );

        setWalkArea(WALK_AREA);

        // Doors
        getDoors().add(rightDoor);
        getMainGroup().getChildren().add(rightDoor.getTrigger());

        getDoors().add(topDoor);
        getMainGroup().getChildren().add(topDoor.getTrigger()); // makes target visible when debug collision enabled.        
    }

    private void initForeground() {

        // Depth of field
        //fgGroup.setEffect(new BoxBlur(10, 10, 3));
    }

    private void initPatches() {

        getPatchList().add(p);
        getFgGroup().getChildren().add(p);
        getFgGroup().getChildren().add(p.getBox());
    }

    @Override
    public String getPropName() {
        return PROP_NAME;
    }

    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
//        p.setProperty(PROPERTY_CONDITION, condition.toString());
        
        return p;
    }

    @Override
    public void loadProperties(Properties p) {
        // Empty for now
        //setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
    }
}
