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
import java.util.Properties;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class DonutShopVignette extends Vignette {

    private static final String PROP_NAME = "donutshop";    
    private static final String CONTENT_BASE = "/content/vignette/donut-shop/";
    private static final Polygon WALK_AREA = new Polygon(
                300, 374,   950, 374,
               1270, 700,   770, 700,   
                760, 720,   490, 720,
                480, 700,   100, 700
        );
    
    private static final Port frontDoor = new Port(
            490, 720,   270, 40,
            550, 360,   PoseSheet.Direction.TOWARD,            
            "StreetVignette2");    
    
    public DonutShopVignette(int w, int h, Port prevPort, Player player) {
        super(w, h, CONTENT_BASE,prevPort, player);        
    }

    @Override
    protected void init() {
        // Character
        getPlayer().setLayoutX( getWidth() * 0.5);
        getPlayer().setLayoutY(getHeight() * 0.5);
        
        setWalkArea(WALK_AREA);
        
        addPort(frontDoor); // Doors
    }

    @Override
    public void loop() {}

    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
        
        // example
        // p.setProperty(PROPERTY_CONDITION, condition.toString());
        
        return p;
    }

    @Override
    public void loadProperties(Properties p) {
        // example
        //setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
    }

    @Override
    public String getPropName() {
        return PROP_NAME;
    }

}
