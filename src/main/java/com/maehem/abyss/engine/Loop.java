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

import com.maehem.abyss.Engine;
import static com.maehem.abyss.Engine.LOGGER;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class Loop extends AnimationTimer {

    private long lastTime = 0;
    private final long TWAIT = 40000000;

    private final ArrayList<String> input = new ArrayList<>();
    //private final Vignette vignette;
    private final Engine engine;

    public Loop(Engine engine) { //, Vignette vignette) {
        this.engine = engine;
        //this.vignette = vignette;
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
        GameState gs = engine.getGameState();
        if (engine.getMatrixPane().isVisible()) {
            engine.getMatrixPane().processEvents(input);
        } else if (engine.getVignetteGroup().isVisible()) {
            gs.getCurrentVignette().processEvents(input);
            VignetteTrigger nextRoom = gs.getNextRoom();
            //engine.getGui().refresh();
            if (nextRoom == null) { // Normal loop processing.
                if (!gs.getNarrationQue().isEmpty()) {
                    engine.getNarrationPane().appendCurrentMessages(gs.getNarrationQue());
                }
            } else { // old vingette requested new room.
                // Save scene state.
                LOGGER.config("[Loop] Load next room.");
                gs.setNextRoom(null);
                engine.notifyVignetteExit(nextRoom);
            }
        }

        lastTime = now;
    }

    public void addInputEvent(KeyEvent ke) {
        LOGGER.log(Level.FINEST, "Loop Input Event: {0}", ke.getCode());
        input.add(ke.getCode().toString());
    }
}
