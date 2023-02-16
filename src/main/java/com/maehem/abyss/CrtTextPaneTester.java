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
package com.maehem.abyss;

import com.maehem.abyss.engine.gui.CrtTextPane2;
import com.maehem.abyss.engine.gui.NarrationPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author mark
 */
public class CrtTextPaneTester extends Application {

    private static final double SIZE = 600.0;

    //private Stage window;
    StackPane centerPane = new StackPane();
    private final BorderPane root = new BorderPane(centerPane);
    private final Scene scene = new Scene(root);

    @Override
    public void start(Stage window) {

//        CrtTextPane2 crtPane = new CrtTextPane2(SIZE);
//        centerPane.getChildren().add(crtPane);
        NarrationPane crtPane = new NarrationPane(SIZE);
        centerPane.getChildren().add(crtPane);

        
        
        window.setScene(this.scene);
        window.setResizable(true);
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
