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
package com.maehem.flatlinejack.engine.gui;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.view.ViewPane;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class TerminalPane extends StackPane implements GameStateListener {

    private static final GameState.Display DISPLAY = GameState.Display.TERMINAL;

    private BBSTerminal terminal;

    public TerminalPane(GameState gs) { //, int w, int h) {
        gs.addListenter(this);
        this.setPrefSize(ViewPane.WIDTH, ViewPane.HEIGHT);

        setBackground(new Background(
                new BackgroundFill(Color.BLACK,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)
        ));
        terminal = new BBSTerminal(gs); //, w, h, 25, 80);
        getChildren().add(terminal);
        setVisible(false);
    }

    public BBSTerminal getTerminal() {
        return terminal;
    }

    public void setTerminal(BBSTerminal t, boolean showTransistion) {
        this.terminal = t;
        getChildren().clear();
        getChildren().add(t);
    }

//    private void animateTerminalOpen() {
//        double div = 50.0;
//        Rectangle r = new Rectangle(getWidth()/div, getHeight()/div, Color.GREEN);
//        getChildren().clear();
//        getChildren().add(r);
//
//        ScaleTransition st = new ScaleTransition(new Duration(2000), r);
//        st.setCycleCount(1);
//        st.setToX(div);
//        st.setToY(div);
//        st.setInterpolator(Interpolator.LINEAR);
//        st.play();
//
//        st.setOnFinished((tt) -> {
//            getChildren().remove(r);
//            getChildren().add(terminal);
//        });
//    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {
    }

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {
    }

    @Override
    public void gameStateDisplayChanged(GameState gs, GameState.Display d) {
        setVisible(d == DISPLAY);
        if (d == DISPLAY) {
            //updateItemGrid();
            //animateTerminalOpen();
        }
    }

    @Override
    public void gameStateMatrixSiteChanged(GameState gs, int newAddr) {}
}
