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
package com.maehem.flatlinejack.engine.matrix;

import com.maehem.flatlinejack.engine.EmptySoftwareThing;
import com.maehem.flatlinejack.engine.SoftwareThing;
import com.maehem.flatlinejack.engine.gui.widgets.CooldownIndicator;
import com.maehem.flatlinejack.engine.gui.widgets.Gauge;
import com.maehem.flatlinejack.engine.gui.widgets.NumberIndicator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Tab that pops up to represent an attack Software when parked in front
 * of a MatrixSite.
 * 
 * @author mark
 */
public class SoftwareTabNode extends BorderPane {

    private final SoftwareThing software;
    private final double width = 300.0;
    private final double height = 110.0;

    public SoftwareTabNode( SoftwareThing software, char hotKey ) {
        this.software = software;
        
        if ( software instanceof EmptySoftwareThing) {
            setOpacity(0.33);
        }
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(width*0.05), Insets.EMPTY)));
        StackPane hotKeyPane = new StackPane();
        Text hotkeyText = new Text(String.valueOf((software instanceof EmptySoftwareThing)?' ':hotKey));
        hotkeyText.setFont(Font.font(height*0.2));
        hotKeyPane.getChildren().add(hotkeyText);
        hotKeyPane.setPrefSize(height*0.3, height*0.3);
        hotKeyPane.setMinSize(height*0.3, height*0.3);
        hotKeyPane.setBorder(new Border(new BorderStroke(
                Color.BLACK, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(4), 
                new BorderWidths(2))));
        
        Text titleText = new Text(software.getName().split("\\(")[0]);
        titleText.setFont(Font.font(height*0.2));
        HBox textArea = new HBox(titleText);
        textArea.setPadding(new Insets(4, 0, 0, 0));
        textArea.setAlignment(Pos.CENTER);
        
        VBox titleV = new VBox(textArea);
        HBox.setHgrow(titleV,Priority.ALWAYS);
        HBox marquee = new HBox( hotKeyPane, titleV);
        marquee.setSpacing(8);
        marquee.setPadding(new Insets(4));
        setBottom(marquee);
       
        double dim = width*0.16;
        double cornerRad = 6.0;
        Rectangle rectangle = new Rectangle(0, 0, dim, dim);
        rectangle.setArcWidth(cornerRad);
        rectangle.setArcHeight(cornerRad);

        ImagePattern pattern = new ImagePattern(
            new Image(getClass().getResourceAsStream(software.getIconPath()), 
                    dim, dim, false, false)
        );

        // In order to match the border rounding we fill a rounded rectangle with an ImagePattern.
        rectangle.setFill(pattern);
        rectangle.setEffect(new DropShadow(5, Color.BLACK));  // Shadow

        StackPane iconPane = new StackPane(rectangle);
        iconPane.setBorder(new Border(new BorderStroke(
                Color.BLUEVIOLET, BorderStrokeStyle.SOLID, 
                new CornerRadii(6), 
                new BorderWidths(2))));
        StackPane iconGroup = new StackPane(iconPane);
        iconGroup.setPadding(new Insets(6));
        setLeft(iconGroup);
        BorderPane.setAlignment(iconGroup, Pos.TOP_CENTER);
        
        Gauge conditionGauge = new Gauge(
                getClass().getResourceAsStream("/icons/repair.png"), 
                width*0.7, height*0.26, software.getCondition(), software.getMaxCondition(),
                Gauge.ValueLabel.OVERLAY_CENTERED
        );
        
        double indHeight = height*0.25;
        NumberIndicator attackNumber = new NumberIndicator(
                getClass().getResourceAsStream("/icons/attack-icon.png"), 
                indHeight, software.getAttackDamage(), 0
        );

        NumberIndicator slowNumber = new NumberIndicator(
                getClass().getResourceAsStream("/icons/turtle.png"), 
                indHeight, software.getSlowEffect(), 0
        );
        NumberIndicator recoveryNumber = new NumberIndicator(
                getClass().getResourceAsStream("/icons/computer-fan.png"), 
                indHeight, software.getRecoveryTime(), 2
        );

        CooldownIndicator cooldown = new CooldownIndicator(
                indHeight, 
                software.getRechargeStatus(), 
                SoftwareThing.MAX_RECHARGE
        );        

        HBox attackRecoveryArea = new HBox(
             //   attackPowerLabelText, attackPowerText, spacer1,
                slowNumber, //spacer1,
                attackNumber, //spacer2,
                recoveryNumber,     // spacer2,
                 cooldown);
        attackRecoveryArea.setSpacing(6);
        attackRecoveryArea.setAlignment(Pos.CENTER_RIGHT);
        
        VBox centerArea = new VBox( attackRecoveryArea, conditionGauge );
        centerArea.setSpacing(12);
        centerArea.setPadding(new Insets(6));
        setCenter(centerArea);
        
    }
    
}
