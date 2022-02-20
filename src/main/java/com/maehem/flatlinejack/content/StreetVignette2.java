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
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class StreetVignette2 extends Vignette {

    //private ResourceBundle bundle;
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

    private static final Patch p = new Patch(
            1140, 0, 490, 
            StreetVignette2.class.getResourceAsStream(DOOR_PATCH_IMAGE_FILENAME));
    
    private static final Port rightDoor = new Port(
            1140, 440, // Location
            22, 50, // Size
            PawnShopVignette.PLAYER_START_X, 
            PawnShopVignette.PLAYER_START_Y, 
            PoseSheet.Direction.TOWARD, // Player position and orientation at destination
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

    @Override
    public void loop() {
        // Test the gauges.
        //getPlayer().setMoney(getPlayer().getMoney()-1);
        //getPlayer().setHealth(getPlayer().getHealth()-1);
        //getPlayer().setConstitution(getPlayer().getConstitution()-1);
                
    }

    @Override
    protected void init() {
        //setName(NAME);
        //bundle = ResourceBundle.getBundle("content.messages.StreetVignette2");
        setName(bundle.getString("title"));
        getNarrationPane().setText(bundle.getString("narration"));
        
        //initBackground();
        initMainArea();
        initPatches();
        initForeground();
        //return getClass().getSimpleName();
    }

    private void initMainArea() {
        // set player position
        getPlayer().setLayoutX( getWidth() * 0.5); // Near middle
        getPlayer().setLayoutY(getHeight() * 0.5); // Hallf way down

//        getMainGroup().getChildren().addAll(
//                getPlayer()
//        );

        // set custom walk area
        setWalkArea(WALK_AREA);

        // Doors
        getDoors().add(rightDoor);
        getDoors().add(topDoor);        
        getMainGroup().getChildren().addAll(rightDoor, topDoor);        
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
