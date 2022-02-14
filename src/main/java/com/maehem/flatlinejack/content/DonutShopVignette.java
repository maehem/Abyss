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

import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Port;
import com.maehem.flatlinejack.engine.PoseSheet;
import com.maehem.flatlinejack.engine.Vignette;
import java.util.ArrayList;
import java.util.Properties;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class DonutShopVignette extends Vignette {

    private static final String NAME = "Donut Shop";
    private static final String PROP_NAME = "donutshop";
    
    private static final String CONTENT_BASE = "/content/vignette/donut-shop/";
    //private static final String DOOR_PATCH_IMAGE_FILENAME = "donut-shop-patch-left.png";
    private static final Polygon WALK_AREA = new Polygon(
                300, 374,
                950, 374,
                1270, 700,
                770, 700,
                760, 720,
                490, 720,
                480, 700,
                100, 700
        );
    
    //private static final Patch p = new Patch(0, 0, 430, DonutShopVignette.class.getResourceAsStream(DOOR_PATCH_IMAGE_FILENAME));;
    private static final Port frontDoor = new Port(
            490, 720, 
            270, 40,
            550,360,PoseSheet.Direction.TOWARD,            
            "StreetVignette2");
    
    
    public DonutShopVignette(int w, int h, Port prevPort, Player player) {
        super(w, h, CONTENT_BASE,prevPort, player);        
    }

    @Override
    public Port processEvents(ArrayList<String> input) {
        Port nextRoom = super.processEvents(input);
        
        // Simulate perspective when player walks closer/further from view.
        //getPlayer().setScale((8 * getPlayer().getLayoutY() / getHeight()) -2.4);
        getPlayer().setScale((5.2 * getPlayer().getLayoutY() / getHeight()) - 1.15);
        
        return nextRoom;
    }

    @Override
    protected void init() {
        setName(NAME);
        //initBackground();
        initMainArea();
        initPatches();
        initForeground();
    }

//    private void initBackground() {
//        // Load images for BACKGROUND_IMAGE_FILENAME.
//        InputStream is = DonutShopVignette.class.getResourceAsStream(BACKGROUND_IMAGE_FILENAME);
//        if ( is == null ) {
//            log.severe("Cannot find image for: " + BACKGROUND_IMAGE_FILENAME);
//            return;
//        }
//        final ImageView bgv = new ImageView();
//        bgv.setImage(new Image(is));
//        getBgGroup().getChildren().add(bgv);
//    }
//
    private void initMainArea() {
        // TODO:   Check that file exists.  The current exception message is cryptic.

        // Character
        getPlayer().setLayoutX(getWidth() / 2); //  Near middle
        getPlayer().setLayoutY(2 * getHeight() / 4);  // Head about a 1/3 down
        
        setWalkArea(WALK_AREA);
        
        // Doors
        getDoors().add(frontDoor);
        
        getMainGroup().getChildren().addAll(
                getPlayer(),
                frontDoor.getTrigger()
        );

    }

    private void initForeground() {

        // Depth of field
        //bgGroup.setEffect(new BoxBlur(10, 10, 3));

    }
    
    private void initPatches() {
        
//        getPatchList().add(p);        
//        getFgGroup().getChildren().add(p);
//        getFgGroup().getChildren().add(p.getBox());
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

    @Override
    public String getPropName() {
        return PROP_NAME;
    }

}
