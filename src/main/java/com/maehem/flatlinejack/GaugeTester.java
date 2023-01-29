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
package com.maehem.flatlinejack;

import com.maehem.flatlinejack.engine.gui.widgets.Gauge;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author mark
 */
public class GaugeTester extends Application {

    private static final double SIZE = 400.0;

    //private Stage window;
    StackPane centerPane = new StackPane();
    private final BorderPane root = new BorderPane(centerPane);
    private final Scene scene = new Scene(root);

    @Override
    public void start(Stage window) {
        VBox gaugePane = new VBox();
        gaugePane.setPadding(new Insets(10));
        gaugePane.setSpacing(4);
        root.setCenter(gaugePane);
        
        Gauge g1 = new Gauge(100, 16, 222, 888);
        Gauge g2 = new Gauge("Condition:", 100, 16, 222, 888, Gauge.ValueLabel.END);
        Gauge g3 = new Gauge("💩", 100, 16, 222, 888, Gauge.ValueLabel.OVERLAY_CENTERED);

        Gauge g6 = new Gauge(
                getClass().getResourceAsStream("/icons/repair.png"), 
                200, 24, 444, 888, Gauge.ValueLabel.END
        );
        Gauge g7 = new Gauge(
                getClass().getResourceAsStream("/icons/repair.png"), 
                200, 48, 444, 888, Gauge.ValueLabel.OVERLAY_CENTERED
        );

        gaugePane.getChildren().addAll(g1,g2, g3, g6, g7);
        
        window.setScene(this.scene);
        window.setResizable(false);
        window.setOnCloseRequest(e -> Platform.exit());
        window.show();
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
