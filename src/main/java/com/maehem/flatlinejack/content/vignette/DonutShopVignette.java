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
package com.maehem.flatlinejack.content.vignette;

import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.PoseSheet;
import com.maehem.flatlinejack.engine.Vignette;
import com.maehem.flatlinejack.engine.VignetteTrigger;
import java.util.Properties;
import javafx.geometry.Point2D;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class DonutShopVignette extends Vignette {

    public  static final String PROP_NAME = "donutshop";    
    private static final String CONTENT_BASE = "/content/vignette/donut-shop/";
    public  static final Point2D PLAYER_START = new Point2D(0.5, 0.86);
    private static final double[] WALK_BOUNDARY = new double[] {
                0.24, 0.52,   0.74, 0.52,
                0.99, 0.9,   0.60, 0.9,   
                0.59, 1.0,    0.38, 1.0,
                0.38, 0.9,   0.08, 0.9
    };
    private static final double[] EXIT_POINTS = { 
        0.38, 0.93,   // exit location
        0.21, 0.03,   // exit size
        0.43, 0.50    // player position at destination
    };
    private static final VignetteTrigger exitPort = new VignetteTrigger(
        0.38, 0.93,   // exit location
        0.21, 0.03,   // exit size
        0.43, 0.50,   // player position at destination
        PoseSheet.Direction.TOWARD, "StreetVignette2");
    
    public DonutShopVignette(int w, int h, VignetteTrigger prevPort, Player player) {
        super(w, h, CONTENT_BASE,prevPort, player,WALK_BOUNDARY);        
    }

    @Override
    protected void init() {        
        addPort(exitPort);
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
