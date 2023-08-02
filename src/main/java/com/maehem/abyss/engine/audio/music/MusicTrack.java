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
package com.maehem.abyss.engine.audio.music;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class MusicTrack {

    private final MediaPlayer mediaPlayer;

    public MusicTrack(Media media) {
        this.mediaPlayer = new MediaPlayer(media);
//        Button myButton = new Button("Press me for sound!");
//        myButton.setOnAction(event -> {
//            if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
//                mediaPlayer.play();
//            }
//        });
    }

    public void play() {
        if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer.getStatus() != MediaPlayer.Status.STOPPED) {
            mediaPlayer.stop();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
    }
    
    public void fadeAndStop(double seconds) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(mediaPlayer.volumeProperty(), 0)));
        timeline.setOnFinished((event) -> {
            stop();
        });
        
        timeline.play();
    }

}
