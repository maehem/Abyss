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
package com.maehem.flatlinejack.engine.gui.widgets;

import com.maehem.flatlinejack.engine.EmptyThing;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Thing;
import com.maehem.flatlinejack.engine.view.ViewPane;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class ThingDetailPane extends VBox {

    private final ImageView detailImageView = new ImageView();
    private final Text name = new Text();

    private Thing currentThing = null;

    private Pane thingCustomDetailsPane;
    private final Button useButton;
    private final Button giveButton;
    private final Button deleteButton;
    private final Button repairButton;
    private final Player player;

    public ThingDetailPane(Player p) {
        this.player = p;

        //this.setOrientation(Orientation.VERTICAL);
        //this.setVgap(4);
        //this.setHgap(4);
        this.setSpacing(2);
        this.setPadding(new Insets(4));
        this.setBorder(new Border(new BorderStroke(
                new Color(0, 0, 0, 0.5),
                BorderStrokeStyle.SOLID,
                new CornerRadii(4),
                BorderWidths.DEFAULT
        )));
        this.setMaxHeight(ViewPane.HEIGHT);
        this.setPrefWidth(ViewPane.WIDTH*0.35);
        this.setAlignment(Pos.TOP_CENTER);
        //this.setColumnHalignment(HPos.CENTER);

        detailImageView.setFitWidth(ViewPane.WIDTH/4);
        detailImageView.setPreserveRatio(true);
        

        name.setFont(new Font(ViewPane.HEIGHT*0.04));
        name.setTextAlignment(TextAlignment.CENTER);
        name.setFill(new Color(0, 0, 0, 0.5));

        useButton = createButton("Use");
        giveButton = createButton("Give");
        deleteButton = createButton("Delete");
        repairButton = createButton("Repair");
        
        HBox buttonPane = new HBox(8, useButton, giveButton, deleteButton, repairButton);
        buttonPane.setAlignment(Pos.BOTTOM_CENTER);
        Pane spacer = new Pane();
        spacer.setPrefSize(10, 10);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        this.getChildren().addAll(detailImageView, name, spacer, buttonPane);

        clearThing();
    }

    private static javafx.scene.control.Button createButton( String text ) {
        Button b = new javafx.scene.control.Button(text);
        b.setFont(Font.font(ViewPane.HEIGHT * 0.028));
        return b;
    }
    
    public void showThing(Thing t) {
        this.currentThing = t;
        String iconPath = t.getIconPath();
        if (iconPath != null) {
            detailImageView.setImage(
                    new Image(getClass().getResourceAsStream(iconPath))
            );
        } else {
            detailImageView.setImage(null);
        }
        name.setText(t.getName());

        if (thingCustomDetailsPane != null) {
            getChildren().remove(thingCustomDetailsPane);
        }

        thingCustomDetailsPane = getDetailPane(t);
        //thingCustomDetailsPane.setPrefWidth(ViewPane.WIDTH/4.0);

        getChildren().add(getChildren().lastIndexOf(name) + 1, thingCustomDetailsPane);

        useButton.setVisible(t.canUse());
        giveButton.setVisible(t.canGive());
        deleteButton.setVisible(t.canDelete());
        repairButton.setVisible(t.canRepair(player));

        repairButton.setDisable(!t.needsRepair());

    }

    public final void clearThing() {
        currentThing = null;
        detailImageView.setImage(null);
        name.setText("");

        useButton.setVisible(false);
        giveButton.setVisible(false);
        deleteButton.setVisible(false);
        repairButton.setVisible(false);

        if (thingCustomDetailsPane != null) {
            getChildren().remove(thingCustomDetailsPane);
            thingCustomDetailsPane = null;
        }
    }

    private Pane getDetailPane(Thing t) {
        FlowPane detailPane = new FlowPane();
        detailPane.setAlignment(Pos.TOP_CENTER);
        if (!(t instanceof EmptyThing)) {
//            HBox gaugePane = new HBox(new Gauge(
//                    "Condition:",
//                    ViewPane.WIDTH*0.2, ViewPane.HEIGHT*0.06,
//                    t.getCondition(),
//                    t.getMaxCondition(),
//                    Gauge.ValueLabel.OVERLAY_CENTERED
//            ));
//            
//            detailPane.getChildren().add(gaugePane);
            
            detailPane.getChildren().add(new Gauge(
                    "Condition:",
                    ViewPane.WIDTH*0.2, ViewPane.HEIGHT*0.04,
                    t.getCondition(),
                    t.getMaxCondition(),
                    Gauge.ValueLabel.OVERLAY_CENTERED)
            );
            detailPane.getChildren().add(new Indicator("Value:",
                    ViewPane.HEIGHT*0.04,
                    "$" + t.getValue()
            ));
        }
        
        return detailPane;
    }

}
