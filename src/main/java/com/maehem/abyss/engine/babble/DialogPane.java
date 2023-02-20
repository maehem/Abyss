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
import com.maehem.abyss.engine.view.ViewPane;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class DialogPane extends BorderPane {

    public final static double FONT_SIZE = 30;
    public final static Color DROP_COLOR = new Color(0.0, 0.0, 0.0, 0.7);

    private final ArrayList<DialogSheet2> dialogList = new ArrayList<>();
    private DialogSheet2 currentDialogSheet;
    private VignetteTrigger port = null;

    private boolean actionDone;
    private final Character npc;
    private Player player;

    //private final FlowPane dialogPane = new FlowPane();
//    private double width=0;
//    private double height=0;
//    private double boxX;
//    private double boxY;
//    private double boxW;
//    private double boxH;
    private final Text nameText = new Text("Character Name");
    private final Text dialogText = new Text(
            "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony."
    );
    private final VBox answerButtonsBox = new VBox();

    public DialogPane(Character npc) {
        this.npc = npc;
        setPrefSize(ViewPane.WIDTH * 0.7, ViewPane.HEIGHT * 0.8);
        setMinSize(ViewPane.WIDTH * 0.7, ViewPane.HEIGHT * 0.8);
        setLayoutX(ViewPane.WIDTH * 0.15);
        setLayoutY(ViewPane.HEIGHT * 0.1);
        //setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(20), Insets.EMPTY)));
        setEffect(new DropShadow(40, DROP_COLOR));

        HBox nameBox = new HBox(nameText);
        nameBox.setPadding(new Insets(FONT_SIZE / 2));
        nameText.setFont(Font.font(FONT_SIZE * 1.3));
        nameText.setText(npc.getName());

        dialogText.setFont(Font.font(FONT_SIZE));

        TextFlow dialogTextFlow = new TextFlow(dialogText);
        dialogTextFlow.setPadding(new Insets(FONT_SIZE));
        dialogTextFlow.setTextAlignment(TextAlignment.CENTER);
        //dialogTextFlow.setEffect(new DropShadow(20, 10, 10, Color.BLACK));
        VBox leftArea = new VBox(nameBox, dialogTextFlow);
        leftArea.setFillWidth(true);
        leftArea.setBackground(new Background(new BackgroundFill(
                Color.GRAY,
                new CornerRadii(20, 0, 0, 20, false),
                Insets.EMPTY
        )));

        // Clip the buttons area so that the drop shadow only shades the left edge.
        Rectangle r = new Rectangle(ViewPane.WIDTH * 0.7 + 40, ViewPane.HEIGHT * 0.8);
        r.setArcHeight(20);
        r.setArcWidth(20);
        r.setLayoutX(-40);
        answerButtonsBox.setClip(r);

        answerButtonsBox.setPrefWidth(getPrefWidth() * 0.5);
        answerButtonsBox.setMinWidth(getPrefWidth() * 0.5);
        answerButtonsBox.setBackground(new Background(new BackgroundFill(
                Color.DARKGRAY,
                new CornerRadii(0, 20, 20, 0, false),
                Insets.EMPTY)
        ));
        answerButtonsBox.setPadding(new Insets(FONT_SIZE / 2.0));
        answerButtonsBox.setSpacing(FONT_SIZE / 2.0);
        answerButtonsBox.setAlignment(Pos.CENTER);
        answerButtonsBox.setEffect(new DropShadow(30, 0, 0, DROP_COLOR));
        // 
//        BorderPane answerPane = new BorderPane(answerButtonsBox);
//        answerPane.setPrefSize(getPrefWidth()*0.4, getPrefHeight());
//        answerPane.setEffect(new DropShadow());
//        answerPane.setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));

        //HBox hBox = new HBox(leftArea, answerButtonsBox);
        setCenter(leftArea);
        setRight(answerButtonsBox);

        // Image of NPC as cameo cropped view
        ImageView npcView = npc.getPoseSheet().getCameo();
        npcView.setY(-npcView.getBoundsInLocal().getHeight());
        npcView.setX(0);

        Rectangle cameoFrame = new Rectangle(npcView.getX(), npcView.getY(), npcView.getViewport().getWidth(), npcView.getViewport().getHeight());
        cameoFrame.setStrokeWidth(10.0);
        cameoFrame.setStroke(Color.DARKGREEN);
        cameoFrame.setFill(Color.TRANSPARENT);
        AnchorPane.setLeftAnchor(cameoFrame, 0.0);
        AnchorPane.setBottomAnchor(cameoFrame, 0.0);

        // Close Dialog control 'X' (upper right of pane.)
        Rectangle closeRect = new Rectangle(40, 40, Color.RED);
        //closeRect.setX(getPrefWidth()-40);
        //closeRect.setY(0);
        AnchorPane.setRightAnchor(closeRect, 0.0);
        AnchorPane.setBottomAnchor(closeRect, 0.0);
        closeRect.setOnMouseClicked((event) -> {
            event.consume();
            doCloseDialog();
            //npc.setTalking(false);
            //setVisible(false);

            //setActionDone(false);
        });

        AnchorPane topArea = new AnchorPane(cameoFrame, closeRect);
        setTop(topArea);
    }

//    /**
//     * Initialize the geometry of the dialog screen.  
//     * Called by Vignette once the screen geometry is known.
//     * 
//     * @param width
//     * @param height 
//     */
//    public void init(double width, double height ) {
//        this.width = width;
//        this.height = height;
//        
//        this.boxX = width/3;
//        this.boxY = height/3;
//        
//        this.boxW = width/3;
//        this.boxH = height/3;
//        
//        Rectangle rect = new Rectangle(width, height);
//        rect.setFill(Color.BLACK);
//        rect.setOpacity(0.5);
//                
////        Rectangle dialogRect = new Rectangle(boxW, boxH, Color.LIGHTSLATEGRAY);
////        dialogRect.setX(boxX);
////        dialogRect.setY(boxY);
//        
//        // Debug rect
//        //dialogRect.setStrokeWidth(2.0);
//        //dialogRect.setStroke(Color.MAGENTA);
//        
//        dialogPane.setAlignment(Pos.CENTER);
//        
//        StackPane dialogStackPane = new StackPane(/*dialogRect,*/ dialogPane );
//        //dialogStackPane.setAlignment(Pos.CENTER);
//        dialogStackPane.setLayoutX(boxX);
//        dialogStackPane.setLayoutY(boxY);
//        dialogStackPane.setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(8), new Insets(4))));
//        
//        ImageView npcView = npc.getPoseSheet().getCameo();
//        npcView.setY(boxY);
//        npcView.setX(boxX-npcView.getViewport().getWidth());
//        
//        Rectangle cameoFrame = new Rectangle(npcView.getX(), npcView.getY(), npcView.getViewport().getWidth(), npcView.getViewport().getHeight());
//        cameoFrame.setStrokeWidth(10.0);
//        cameoFrame.setStroke(Color.DARKGREEN);
//        cameoFrame.setFill(Color.TRANSPARENT);
//        
//        Rectangle closeRect = new Rectangle(40, 40, Color.DARKSLATEGRAY);
//        closeRect.setX(boxX+boxW-20);
//        closeRect.setY(boxY);
//        closeRect.setOnMouseClicked((event) -> {
//            event.consume();
//            npc.setTalking(false);
//            setActionDone(true);
//        });
//        
//        getDialogList().forEach((ds) -> {
//            ds.setGeometry(boxX, boxY, boxW, boxH);
//        });
//        
//        //getChildren().addAll(rect, dialogStackPane, closeRect, npcView, cameoFrame);
//    }
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
    public ArrayList<DialogSheet2> getDialogList() {
        return dialogList;
    }

    public void addDialogSheet(DialogSheet2 ds) {
        dialogList.add(ds);
        if (currentDialogSheet == null) {
            setCurrentDialogSheet(ds);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return the currentDialogSheet
     */
    public DialogSheet2 getCurrentDialog() {
        return currentDialogSheet;
    }

    /**
     * @param ds the currentDialogSheet to set
     */
    public void setCurrentDialogSheet(DialogSheet2 ds) {
        dialogText.setText(ds.getDialogText());
        rebuildResponsePane(ds.getResponse());

        //dialogPane.getChildren().remove(this.currentDialogSheet);
        this.currentDialogSheet = ds;
        //dialogPane.getChildren().add(ds);
    }

    private void rebuildResponsePane(ArrayList<DialogResponse2> responseList) {
        answerButtonsBox.getChildren().clear();
        responseList.forEach((t) -> {
            Button b = responseButton(t);
            answerButtonsBox.getChildren().add(b);
//            b.setOnAction((tt) -> {
//                tt.consume();
//                t.getAction().doResponseAction();                
//            });
        });
    }

    private static Button responseButton(DialogResponse2 response) {
        Button b = new Button(response.getText());
        b.setFont(Font.font(FONT_SIZE * 0.7));
        b.setBorder(new Border(
                new BorderStroke(Color.RED,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(FONT_SIZE / 2),
                        new BorderWidths(2)
                )));
        b.setOnAction((tt) -> {
            tt.consume();
            response.getAction().doResponseAction();
        });

        return b;
    }

    public void setExit(VignetteTrigger port) {
        this.port = port;
    }

    /**
     * @return the port
     */
    public VignetteTrigger getExit() {
        return port;
    }

    public void doCloseDialog() {
        npc.setTalking(false);
        setVisible(false);
    }

}
