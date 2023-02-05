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
package com.maehem.flatlinejack.engine;

import static com.maehem.flatlinejack.Engine.LOGGER;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * Doors and other transitions to other Vignettes.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class TriggerShape extends Pane {

    public static final Color TRIGGER_FILL_DEFAULT = Color.GOLD;
    public static final Color TRIGGER_FILL_ACTIVE = Color.RED;

    private double rawX = 0.0;
    private double rawY = 0.0;
    private double rawW = 0.1;
    private double rawH = 0.1;
    
    private final Rectangle trigger;
    private Color triggerColorDefault = TRIGGER_FILL_DEFAULT;
    private Color triggerColorActive = TRIGGER_FILL_ACTIVE;
    private Text label = new Text(getClass().getSimpleName());
    private ImageView icon = null;

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
        this.setPrefSize(w, h);
        
        label.setFill(Color.WHITE);
        label.setTranslateY(-4.0);
        this.trigger = new Rectangle(0, 0, w, h);
        getChildren().addAll(trigger,label);
        
        this.rawX = x;
        this.rawY = y;
        this.rawW = w;
        this.rawH = h;

        updateTriggerState(false);
    }

    public final void updateTriggerState(boolean tActive) {

        if (tActive) {
            trigger.setFill(triggerColorActive);
        } else {
            trigger.setFill(triggerColorDefault);
        }
    }

    /**
     * 
     * @param scaleX usually the width of the @Scene
     * @param scaleY usually the height of the @Scene
     */
    public void setScale( double scaleX, double scaleY ) {

        this.setLayoutX(scaleX * rawX);
        this.setLayoutY(scaleY * rawY);
        
        this.setWidth( scaleX * rawW);
        this.setHeight(scaleY * rawH);
        
        this.setMinWidth(scaleX*rawW);
        this.setMinHeight(scaleY*rawH);
        
        // Trigger is the size of out pane.
        trigger.setWidth(scaleX * rawW);
        trigger.setHeight(scaleY * rawH);
    }
    
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

    public void setClickIcon(String iconPath, double offX, double offY) {
        icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setPreserveRatio(true);
        icon.setFitWidth(50);
        icon.setX(offX);
        icon.setY(offY);
        
        getChildren().add(icon);        
        
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
