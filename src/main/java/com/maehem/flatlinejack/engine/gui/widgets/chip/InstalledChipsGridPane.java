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
package com.maehem.flatlinejack.engine.gui.widgets.chip;

import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.SkillChipThing;
import com.maehem.flatlinejack.engine.gui.ChipsConfigurationListener;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class InstalledChipsGridPane extends GridPane {

    ChipsConfigurationListener configListener;
    Player player;
    InstalledChipStatsPane chip1;
    InstalledChipStatsPane chip2;
    InstalledChipStatsPane chip3;
    InstalledChipStatsPane chip4;

    public InstalledChipsGridPane(double w, double h, Player p, ChipsConfigurationListener listener) {
        this.configListener = listener;
        this.player = p;

        setBackground(new Background(new BackgroundFill(
                new Color(0.1, 0.1, 0.1, 1.0),
                new CornerRadii(20),
                Insets.EMPTY
        )));
        setPadding(new Insets(10));
        setPrefSize(w, 166);
        setHgap(8);
        setVgap(8);

        chip1 = new InstalledChipStatsPane(player, 0);
        chip2 = new InstalledChipStatsPane(player, 1);
        chip3 = new InstalledChipStatsPane(player, 2);
        chip4 = new InstalledChipStatsPane(player, 3);

        GridPane.setHgrow(chip1, Priority.ALWAYS);
        GridPane.setHgrow(chip2, Priority.ALWAYS);
        GridPane.setHgrow(chip3, Priority.ALWAYS);
        GridPane.setHgrow(chip4, Priority.ALWAYS);

        GridPane.setVgrow(chip1, Priority.ALWAYS);
        GridPane.setVgrow(chip2, Priority.ALWAYS);
        GridPane.setVgrow(chip3, Priority.ALWAYS);
        GridPane.setVgrow(chip4, Priority.ALWAYS);

        addRow(0, chip1, chip2);
        addRow(1, chip3, chip4);

    }

    public void refresh(SkillChipThing t) {
        chip1.refresh();
        chip2.refresh();
        chip3.refresh();
        chip4.refresh();
    }

}
