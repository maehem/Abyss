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

import com.maehem.abyss.engine.Character;
import com.maehem.abyss.engine.Player;
import com.maehem.abyss.engine.VignetteTrigger;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class DialogScreen extends Group {

    private final ArrayList<DialogSheet> dialogList = new ArrayList<>();
    private DialogSheet currentDialog;
    private final FlowPane dialogPane = new FlowPane();

    private boolean actionDone;
    private final Character npc;
    private Player player;
    private double width=0;
    private double height=0;
    private double boxX;
    private double boxY;
    private double boxW;
    private double boxH;
    private VignetteTrigger port=null;

    public DialogScreen( Character npc) {
        this.npc = npc;
    }
    
    /**
     * Initialize the geometry of the dialog screen.  
     * Called by Vignette once the screen geometry is known.
     * 
     * @param width
     * @param height 
     */
    public void init(double width, double height ) {
        this.width = width;
        this.height = height;
        
        this.boxX = width/3;
        this.boxY = height/3;
        
        this.boxW = width/3;
        this.boxH = height/3;
        
        Rectangle rect = new Rectangle(width, height);
        rect.setFill(Color.BLACK);
        rect.setOpacity(0.5);
                
//        Rectangle dialogRect = new Rectangle(boxW, boxH, Color.LIGHTSLATEGRAY);
//        dialogRect.setX(boxX);
//        dialogRect.setY(boxY);
        
        // Debug rect
        //dialogRect.setStrokeWidth(2.0);
        //dialogRect.setStroke(Color.MAGENTA);
        
        dialogPane.setAlignment(Pos.CENTER);
        
        StackPane dialogStackPane = new StackPane(/*dialogRect,*/ dialogPane );
        //dialogStackPane.setAlignment(Pos.CENTER);
        dialogStackPane.setLayoutX(boxX);
        dialogStackPane.setLayoutY(boxY);
        dialogStackPane.setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(8), new Insets(4))));
        
        ImageView npcView = npc.getPoseSheet().getCameo();
        npcView.setY(boxY);
        npcView.setX(boxX-npcView.getViewport().getWidth());
        
        Rectangle cameoFrame = new Rectangle(npcView.getX(), npcView.getY(), npcView.getViewport().getWidth(), npcView.getViewport().getHeight());
        cameoFrame.setStrokeWidth(10.0);
        cameoFrame.setStroke(Color.DARKGREEN);
        cameoFrame.setFill(Color.TRANSPARENT);
        
        Rectangle closeRect = new Rectangle(40, 40, Color.DARKSLATEGRAY);
        closeRect.setX(boxX+boxW-20);
        closeRect.setY(boxY);
        closeRect.setOnMouseClicked((event) -> {
            event.consume();
            npc.setTalking(false);
            setActionDone(true);
        });
        
        getDialogList().forEach((ds) -> {
            ds.setGeometry(boxX, boxY, boxW, boxH);
        });
        
        getChildren().addAll(rect, dialogStackPane, closeRect, npcView, cameoFrame);
    }
    
    /**
     * @return the actionDone
     */
    public boolean isActionDone() {
        return actionDone;
    }

    /**
     * @param actionDone the actionDone to set
     */
    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
    }

    /**
     * @return the dialogList
     */
    public ArrayList<DialogSheet> getDialogList() {
        return dialogList;
    }
    
    public void addDialogSheet( DialogSheet ds ) {
        dialogList.add(ds);
        if ( width > 0 && height > 0 ) {
            ds.setGeometry(boxX, boxY, boxW, boxH);
        }
        if ( currentDialog == null ) {
            setCurrentDialog(ds);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return the currentDialog
     */
    public DialogSheet getCurrentDialog() {
        return currentDialog;
    }

    /**
     * @param currentDialog the currentDialog to set
     */
    public void setCurrentDialog(DialogSheet currentDialog) {
        dialogPane.getChildren().remove(this.currentDialog);
        this.currentDialog = currentDialog;
        dialogPane.getChildren().add(currentDialog);
    }

    public void setExit( VignetteTrigger port) {
        this.port = port;
    }
    
    /**
     * @return the port
     */
    public VignetteTrigger getExit() {
        return port;
    }

}
