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
package com.maehem.abyss.engine.gui.widgets;

import com.maehem.abyss.engine.EmptyThing;
import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.Player;
import com.maehem.abyss.engine.Thing;
import com.maehem.abyss.engine.view.ViewPane;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class ThingDetailPane extends VBox {

    private final ImageView detailImageView = new ImageView();
    private final Text name = new Text();

    //private Thing currentThing = null;

    private Pane thingCustomDetailsPane;
    private final Button useButton;
    private final Button giveButton;
    private final Button deleteButton;
    private final Button repairButton;
    private final Player player;
    private final GameState gameState;

    public ThingDetailPane(GameState gs, Player p) {
        this.player = p;
        this.gameState = gs;

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

        detailImageView.setFitWidth(ViewPane.WIDTH/4);
        detailImageView.setPreserveRatio(true);
        

        name.setTextAlignment(TextAlignment.CENTER);
        name.setFill(new Color(0, 0, 0, 0.7));

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

        Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.DARKGREY)};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        setBackground(new Background(new BackgroundFill(lg1, CornerRadii.EMPTY, Insets.EMPTY)));
        clearThing();
    }

    private static javafx.scene.control.Button createButton( String text ) {
        Button b = new javafx.scene.control.Button(text);
        b.setFont(Font.font(ViewPane.HEIGHT * 0.028));
        return b;
    }
    
    public void showThing(Thing t) {
        //this.currentThing = t;
        String iconPath = t.getIconPath();
        if (iconPath != null) {
            InputStream imgStream;
            if ( iconPath.startsWith("/content") ) {
                // Use class loader from content JAR.
                imgStream = gameState.getContentLoader().getStream(iconPath);
            } else {
                // Use class loader from engine JAR.
                imgStream = getClass().getResourceAsStream(iconPath);
            }
            detailImageView.setImage(new Image(imgStream));
            detailImageView.setEffect(new DropShadow(50, t.getColor()));
        } else {
            detailImageView.setImage(null);
            detailImageView.setEffect(null);
        }
        String nameString  = t.getName();
        if ( nameString.length() > 20 ) {
            name.setFont(new Font(ViewPane.HEIGHT*0.04));
        } else {
            name.setFont(new Font(ViewPane.HEIGHT*0.06));            
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
        //currentThing = null;
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
