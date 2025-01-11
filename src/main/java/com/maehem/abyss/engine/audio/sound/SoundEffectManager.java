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
package com.maehem.abyss.engine.audio.sound;

import static com.maehem.abyss.Engine.LOGGER;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javafx.scene.media.AudioClip;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SoundEffectManager {

    public static enum Sound { // Use WAV. JavaFX/Mac bugs with mp3 and AAC.
        FAIL("fail.wav"),
        MONEY("money.wav"),
        SQUISH("money.wav"), // TODO: Organ squish sound.
        ICE_DOWN_1("ice-down-1.wav");

        private AudioClip clip;

        private Sound(String path) {
            LOGGER.log(Level.CONFIG, "Create sound for: " + path);
            try {
                clip = new AudioClip(getClass().getResource("/sounds/" + path).toURI().toString());
                clip.setCycleCount(1);
                //clip.setRate(0.125);
            } catch (URISyntaxException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        /**
         * Replace stock engine clip with custom clip from game content.
         *
         * @param clip
         */
        public void replaceClip(AudioClip clip) {
            LOGGER.log(Level.CONFIG, "Replace sound for {0} with custom clip", this.name());
            this.clip = clip;
        }

        public AudioClip getClip() {
            return clip;
        }
    }

    private static SoundEffectManager instance = null;

    private SoundEffectManager() {

    }

    public static SoundEffectManager getInstance() {
        if (instance == null) {
            instance = new SoundEffectManager();
        }

        return instance;
    }

    public void play(Sound s) {
        s.getClip().play();
        LOGGER.log(Level.SEVERE, "SoundEffect begin: {0}", s.name());
    }
}
