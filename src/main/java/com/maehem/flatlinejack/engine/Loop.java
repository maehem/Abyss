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
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Loop extends AnimationTimer {

    private long lastTime = 0;
    private final long TWAIT = 40000000;
    
    private final ArrayList<String> input = new ArrayList<>();
    private final Vignette vignette;
    private final Engine engine;

    public Loop(Engine engine, Vignette vignette) {
        this.engine = engine;
        this.vignette = vignette;

//        engine.getScene().setOnKeyPressed((KeyEvent e) -> {
//            String code = e.getCode().toString();
//            
//            // only add once... prevent duplicates
//            if (!input.contains(code)) {
//                //System.out.println("Add key code: " + code);
//                input.add(code);
//            }
//        });
//
//        engine.getScene().setOnKeyReleased((KeyEvent e) -> {
//            String code = e.getCode().toString();
//            input.remove(code);
//        });
    }

    @Override
    public void handle(long currentNanoTime) {
        long now = currentNanoTime;
        if (currentNanoTime < lastTime + TWAIT) {
            //lastTime = now;
            return;
        }

        // debug for gauge
        //scene.getPlayer().changeHealth(-1);
        
        Port nextRoom = vignette.processEvents(input);
        engine.getGui().refresh();
        if ( nextRoom != null ) {
            // Save scene state.
            engine.notifyVignetteExit(nextRoom);
        }

        lastTime = now;
    }
    
    public void addInputEvent(KeyEvent ke) {
        System.out.println("Add Loop Input Event: " + ke.getCode());
        input.add(ke.getCode().toString());
    }
}
