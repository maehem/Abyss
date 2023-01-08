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
package com.maehem.flatlinejack.engine.gui.bbs;

import static com.maehem.flatlinejack.Engine.LOGGER;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author mark
 */
public class BBSPasswordBody extends StackPane {

    private final Font font;

    public BBSPasswordBody(Font f) { //, int rows, int cols) {
        this.font = f;

        String fillHex = toHex(BBSText.FILL_COLOR);
        GridPane gp = new GridPane();
        BBSText userLabel = new BBSText(f, "Username:");
        TextField userField = new TextField();
        userField.setFont(font);
        userField.setBackground(Background.EMPTY);
        userField.setBorder(Border.EMPTY);               
        // TextField must use style to change text color.
        userField.setStyle("-fx-text-fill: " + fillHex + ";");
        userField.setPrefColumnCount(10);

        BBSText passLabel = new BBSText(f, "Password:");
        TextField passField = new TextField();
        passField.setFont(font);
        passField.setBackground(Background.EMPTY);
        passField.setBorder(Border.EMPTY);
        passField.setStyle("-fx-text-fill: " + fillHex + ";");
        passField.setPrefColumnCount(10);

        BBSText loginButtonText = new BBSText(font, "LOGIN");
        StackPane loginButton = new StackPane(loginButtonText);
        loginButton.setPadding(new Insets(8,12,8,12));
        loginButton.setAlignment(Pos.CENTER);
        loginButton.setBorder(new Border(new BorderStroke(
                BBSText.FILL_COLOR, 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, 
                new BorderWidths(3)
        )));
        Pane lSpacer = new Pane();
        HBox.setHgrow(lSpacer, Priority.ALWAYS);
        Pane rSpacer = new Pane();
        HBox.setHgrow(rSpacer, Priority.ALWAYS);
        HBox loginBox = new HBox(lSpacer, loginButton, rSpacer);
        loginBox.setPadding(new Insets(20));
        
        loginButton.setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, 
                    "User Clicked Login user: {0}    pass: {1}", 
                    new Object[]{userField.getText(), passField.getText()}
            );
        });
        
        gp.add(userLabel, 0, 0);
        gp.add(userField, 1, 0);
        gp.add(passLabel, 0, 1);
        gp.add(passField, 1, 1);

        getChildren().add(new VBox(gp, loginBox));
    }

    /**
     * Convert a JavaFX Color to web hex.
     * 
     * @param c Color
     * @return web style hex string for color (i.e #FFFFFF )
     */
    private static String toHex(Color c) {
        int red = (int) (c.getRed() * 0xFF);
        String rr = Integer.toHexString(red);
 
        int green = (int) (c.getGreen() * 0xFF);
        String gg = Integer.toHexString(green);

        int blue = (int) (c.getBlue() * 0xFF);
        String bb = Integer.toHexString(blue);

        return "#" + rr + gg + bb;
    }
}
