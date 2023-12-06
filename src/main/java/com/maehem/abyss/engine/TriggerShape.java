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
package com.maehem.abyss.engine;

import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.view.ViewPane;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * Doors and other transitions to other Vignettes.
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class TriggerShape extends Pane {

    public static final Color TRIGGER_FILL_DEFAULT = Color.GOLD;
    public static final Color TRIGGER_FILL_ACTIVE = Color.RED;

//    private double rawX = 0.0;
//    private double rawY = 0.0;
//    private double rawW = 0.1;
//    private double rawH = 0.1;

    private final Rectangle trigger;
    private Color triggerColorDefault = TRIGGER_FILL_DEFAULT;
    private Color triggerColorActive = TRIGGER_FILL_ACTIVE;
    private final Text label = new Text(getClass().getSimpleName());
    private final StackPane icon = new StackPane();

    //private double scaleX = 1.0; // ScreenW. Use setScale() to input actual value.
    //private double scaleY = 1.0; // ScreenH.  ^       ^        ^

    /**
     * Trigger shape as a x:1 ratio to the size of the scene.
     * It will be scaled later to the size of the actual scene.
     *
     * @param x positions relative to boundary  0.0-1.0
     * @param y position relative to boundary  0.0-1.0
     * @param w size relative to boundary  0.0-1.0
     * @param h size relative to boundary  0.0-1.0
     */
    public TriggerShape(double x, double y, double w, double h) {
        this.setPrefSize(ViewPane.WIDTH * w, ViewPane.HEIGHT * h);
        this.setLayoutX( ViewPane.WIDTH * x);
        this.setLayoutY( ViewPane.HEIGHT * y);

        label.setFill(Color.WHITE);
        label.setTranslateY(-4.0);
        this.trigger = new Rectangle(0, 0,
                ViewPane.WIDTH*w, ViewPane.HEIGHT*h
        );
        getChildren().addAll(trigger,label, icon);

//        this.rawX = x;
//        this.rawY = y;
//        this.rawW = w;
//        this.rawH = h;

        updateTriggerState(false);
    }

    public final void updateTriggerState(boolean tActive) {

        if (tActive) {
            trigger.setFill(triggerColorActive);
        } else {
            trigger.setFill(triggerColorDefault);
        }
    }

//    /**
//     *
//     * @param scaleX usually the width of the @Scene
//     * @param scaleY usually the height of the @Scene
//     */
//    public void setScale( double scaleX, double scaleY ) {
//        this.scaleX = scaleX;
//        this.scaleY = scaleY;
//
//        this.setLayoutX(scaleX * rawX);
//        this.setLayoutY(scaleY * rawY);
//
//        this.setWidth( scaleX * rawW);
//        this.setHeight(scaleY * rawH);
//
//        this.setMinWidth(scaleX*rawW);
//        this.setMinHeight(scaleY*rawH);
//
//        // Trigger is the size of out pane.
//        trigger.setWidth(scaleX * rawW);
//        trigger.setHeight(scaleY * rawH);
//    }

    public Shape getTriggerShape() {
        return trigger;
    }

    /**
     * @param c the triggerColorDefault to set
     */
    public void setTriggerColorDefault(Color c) {
        this.triggerColorDefault = c;
    }

    /**
     * @param c the triggerColorActive to set
     */
    public void setTriggerColorActive(Color c) {
        this.triggerColorActive = c;
    }

    public void setShowDebug( boolean show ) {
        trigger.setOpacity(show?1.0:0.0);
        label.setVisible(show);
    }

    public Pane getIcon() {
        return icon;
    }

    public void setClickIcon(String iconPath, double offX, double offY) {
        double rad = ViewPane.WIDTH * 0.01; // Pane arc and drop shadow rad+offset
        Color dropShadow = new Color(0.0,0.0,0.0,0.4);

        ImageView iconImg = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        //icon = new StackPane(iconImg);
        icon.getChildren().clear();
        icon.getChildren().add(iconImg);
        icon.setMinSize(ViewPane.WIDTH*0.10, ViewPane.HEIGHT*0.10);
        icon.setBackground(new Background(
                new BackgroundFill(Color.DARKGRAY, new CornerRadii(rad), Insets.EMPTY))
        );

        iconImg.setPreserveRatio(true);
        iconImg.setFitWidth(icon.getMinHeight()*0.85);
        icon.setTranslateX(offX * ViewPane.WIDTH);
        icon.setTranslateY(-offY * ViewPane.HEIGHT);
        icon.setEffect(new DropShadow(rad*4.0, rad/2.0, rad/2.0, dropShadow));

        //getChildren().addAll(icon);

        icon.setOnMouseClicked((event) -> {
            LOGGER.log(Level.INFO, "Opacity = {0}", icon.getOpacity());
            event.consume();
            if ( icon.isVisible() ) {
                //setJacking(true);
                onClick();
            }
        });

        showIcon(false);
    }

    /**
     * Sets visibility of the clickable icon. Informs sub-class via @onIconShowing() of state change.
     *
     * @param show
     */
    public void showIcon(boolean show) {
        icon.setVisible(show);
        onIconShowing(show); // Inform sub-class that icon visibility changed.
    }

    /**
     * Override this method to do something when user clicks visible trigger icon.
     *
     */
    public void onClick() {}

    /**
     * Inform sub-class that trigger icon is showing and clickable.
     * Override to be informed of the icon visibility state.
     *
     * @param showing
     */
    public void onIconShowing( boolean showing ) {}

}
