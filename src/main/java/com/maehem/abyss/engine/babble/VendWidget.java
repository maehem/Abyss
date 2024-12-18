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
package com.maehem.abyss.engine.babble;

import com.maehem.abyss.engine.*;
import com.maehem.abyss.engine.Character;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class VendWidget extends BorderPane {

    private final Player player;
    private final Character character;

    public VendWidget(Character character, Player player) {
        this.character = character;
        this.player = player;

        initTop();
        setCenter(new Label("Vend Pane"));

    }

    private void initTop() {
        Text title = new Text("Price List");
        Text crLabel = new Text("Credits: ");
        Text crVal = new Text("$" + player.getMoney());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        setTop(new HBox(title, spacer, crLabel, crVal));
    }
}
