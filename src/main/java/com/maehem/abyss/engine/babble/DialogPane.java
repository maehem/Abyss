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

import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.Character;
import com.maehem.abyss.engine.Vignette;
import com.maehem.abyss.engine.VignetteTrigger;
import com.maehem.abyss.engine.view.ViewPane;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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

    public final static String FONT_PATH = "/fonts/PatrickHand-Regular.ttf";
    public final static double FONT_SIZE = 30;
    private final static Color DROP_COLOR = new Color(0.0, 0.0, 0.0, 0.6);
    private final static Color DROP_COLOR_AB = new Color(0.0, 0.0, 0.0, 0.4);
    private final static double DROP_SPREAD = 50.0;
    private static final double CAMEO_H = 210;
    private static final String CLOSE_X_PATH = "/ui/panel-close-x.png";
    // Bubble SVG tuned with: https://svg-path.com/   Wonderful tool!
    private static final String ROUND_BUBBLE
            //= "M175,59h-96.82L47.5,11l7.92,48H23c-8.8,0-16,7.2-16,16v98c0,8.8,7.2,16,16,16h152c8.8,0,16-7.2,16-16v-98c0-8.8-7.2-16-16-16Z";
            = "M175,39 h-10.82 L110.5,11 l27.92,28 H23 c-8.8,0,-16,7.2,-16,16 "
            + "v98 c0,8.8,7.2,16,16,16 h152 c8.8,0,16,-7.2,16,-16 "
            + "v-98 c0,-8.8,-7.2,-16,-16,-16 Z";
    private static final String BUBBLE_STYLE = "-fx-background-color: white; -fx-padding: 20;"
            + "-fx-border-color: black; -fx-border-width: 4px; "
            + "-fx-shape: \"" + ROUND_BUBBLE + "\";";
    private static final String ALERT_STYLE = "-fx-background-color: #020; "
            + "-fx-border-color: #373; -fx-border-width: 5px; ";
    public final static String RESP_FONT_PATH = "/fonts/Modenine-2OPd.ttf";
    private static final int RESPONSE_OK = 900;
    //private static final int RESPONSE_NEXT = 901;

    private final Vignette vignette;
    private final ArrayList<DialogSheet2> dialogList = new ArrayList<>();
    private DialogSheet2 currentDialogSheet;
    private VignetteTrigger port = null;

    private boolean actionDone;
    private final Character npc;
    private final Character npc2;
    private Character currentNpc;

    private final Font DIALOG_FONT = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
    private final Font DIALOG_NAME_FONT = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE * 1.3);
    private final Font ANSWER_FONT = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE * 0.75);
    private final Font ALERT_BTN_FONT = Font.loadFont(getClass().getResourceAsStream(RESP_FONT_PATH), FONT_SIZE);
    private final Font ALERT_TXT_FONT = Font.loadFont(getClass().getResourceAsStream(RESP_FONT_PATH), FONT_SIZE * 0.8);

    private final Text nameText = new Text("Character Name");
    private final Text dialogText = new Text(
            "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
            + "Dialog Text. Hello. I am a pretty pony. "
    );
    private final VBox answerButtonsBox = new VBox();
    private final ImageView cameoView;
    private final Pane cameoViewPane;
    private String[] vars = null;
    private ArrayList<BabbleNode> dialogChain;
    private final StackPane bubble;
    private final StackPane alert;
    private final TextFlow dialogTextFlow;
    private final ImageView closeX;

    /**
     *
     * @param vignette
     * @param npc
     */
    public DialogPane(Vignette vignette, Character npc) {
        this(vignette, npc, null);
    }

    /**
     *
     * @param vignette
     * @param npc
     */
    public DialogPane(Vignette vignette, Character npc, Character npc2) {
        this.vignette = vignette;
        this.npc = npc;
        this.npc2 = npc2;
        currentNpc = npc;

        setPrefSize(ViewPane.WIDTH * 0.84, ViewPane.HEIGHT * 0.9);
        setMinSize(ViewPane.WIDTH * 0.84, ViewPane.HEIGHT * 0.8);
        setLayoutX(ViewPane.WIDTH * 0.08);
        setLayoutY(ViewPane.HEIGHT * 0.05);
        //setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(20), Insets.EMPTY)));
        setEffect(new DropShadow(DROP_SPREAD, DROP_COLOR));

        HBox nameBox = new HBox(nameText);
        nameBox.setPadding(new Insets(FONT_SIZE / 3));
        nameText.setFont(DIALOG_NAME_FONT);
        nameText.setText(npc.getName());
        nameBox.setAlignment(Pos.BASELINE_LEFT);

        dialogText.setFont(DIALOG_FONT);

        dialogTextFlow = new TextFlow(dialogText);
        //dialogTextFlow.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        // Wrap the text flow in a Vbox to center align the text.
        VBox dialogCenteringBox = new VBox(dialogTextFlow);
        dialogCenteringBox.setPadding(new Insets(0, 0, FONT_SIZE * 2.0, 0));
        //dialogCenteringBox.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        dialogCenteringBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(dialogCenteringBox, Priority.ALWAYS);

        VBox nameDialogElementsBox = new VBox(nameBox, dialogCenteringBox);
        VBox.setMargin(nameBox, new Insets(0, 0, 0, 30));
        nameDialogElementsBox.setAlignment(Pos.TOP_CENTER);
        nameDialogElementsBox.setFillWidth(true);

        // Ensure a minimum size for our bubble.
        // Includes the pointy bit going up.
        Region bubbleReg = new Region();
        bubbleReg.setMinSize(200, 200);

        bubble = new StackPane(bubbleReg);
        //bubble.setPadding(new Insets(20));
        bubble.setStyle(BUBBLE_STYLE);
        bubble.setEffect(new DropShadow(DROP_SPREAD, DROP_COLOR));
        StackPane.setMargin(bubble, new Insets(4, 30, 30, 30));

        Region alertReg = new Region();
        alertReg.setMinSize(200, 100);

        alert = new StackPane(alertReg);
        alert.setPadding(new Insets(20));
        alert.setStyle(ALERT_STYLE);
        StackPane.setMargin(alert, new Insets(80, 30, 30, 30));

        setBubbleAlertMode(true);

        StackPane layout = new StackPane(alert, bubble, nameDialogElementsBox);
        layout.setBackground(new Background(new BackgroundFill(
                Color.GRAY,
                new CornerRadii(20, 0, 0, 20, false),
                Insets.EMPTY
        )));

        // Clip the buttons area so that the drop shadow only shades the left edge.
        Rectangle r = new Rectangle(ViewPane.WIDTH * 0.84 + 40, ViewPane.HEIGHT * 0.8);
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
        answerButtonsBox.setPadding(new Insets(FONT_SIZE / 3.0));
        answerButtonsBox.setSpacing(FONT_SIZE / 3.0);
        answerButtonsBox.setAlignment(Pos.CENTER);
        //answerButtonsBox.setEffect(new DropShadow(DROP_SPREAD, 0, 0, DROP_COLOR));
        answerButtonsBox.setEffect(new DropShadow(DROP_SPREAD, DROP_COLOR_AB));

        cameoView = new ImageView();
//        Image cameo = npc.getCameo();
//        // TODO: Use setCameo()
//        if (cameo == null) {
//            cameoView = new ImageView();
//            LOGGER.log(Level.INFO, "NPC cameo was null.");
//        } else {
//            cameoView = new ImageView(cameo);
//        }
        cameoView.setFitHeight(CAMEO_H);
        cameoView.setPreserveRatio(true);

        InnerShadow innerShadow = new InnerShadow(
                14.0f,
                0.0f, -8.0f,
                new Color(0.0, 0.0, 0.0, 0.33)
        );

        DropShadow dropShadow = new DropShadow(DROP_SPREAD, 20, 20, DROP_COLOR);

        Blend blendEffect = new Blend(BlendMode.SRC_OVER);
        blendEffect.setTopInput(innerShadow);
        blendEffect.setBottomInput(dropShadow);

        cameoViewPane = new StackPane(cameoView);
        //cameoViewPane.setEffect(innerShadow);
        //cameoViewPane.setPrefSize(CAMEO_H, CAMEO_H);
        cameoViewPane.setEffect(blendEffect);

        AnchorPane.setLeftAnchor(cameoViewPane, 20.0);
        AnchorPane.setBottomAnchor(cameoViewPane, 0.0);

        closeX = new ImageView(new Image(getClass().getResourceAsStream(CLOSE_X_PATH)));
        // Close Dialog control 'X' (upper right of pane.)
        //Rectangle closeRect = new Rectangle(40, 40, Color.RED);
        AnchorPane.setRightAnchor(closeX, 0.0);
        AnchorPane.setBottomAnchor(closeX, 0.0);
        closeX.setOnMouseClicked((event) -> {
            if (closeX.isVisible()) {
                event.consume();
                doCloseDialog();
            }
        });

        //cameoViewPane.getChildren().add(cameoFrame);
        AnchorPane topArea = new AnchorPane(cameoViewPane, closeX);
        setTop(topArea); // Make sure center is closest Z so we can tune cameo overlap.
        //setCenter(leftArea);
        setCenter(layout);
        setRight(answerButtonsBox);
    }

    public void setVars(String[] vars) {
        this.vars = vars;
    }

    private void setCurrentNpc(Character c) {
        currentNpc = c;
        setCameo(c.getCameo());
    }

    public void setCameo(Image iv) {
        cameoView.setImage(iv);
        cameoView.setViewport(new Rectangle2D(0, 0, iv.getWidth(), iv.getHeight()));
        //cameoView.setPreserveRatio(true);
        //cameoView.setFitHeight(CAMEO_H);

        //cameoViewPane.getChildren().clear();
        //cameoViewPane.getChildren().add(cameoView);
    }

    private void setBubbleAlertMode(boolean asBubble) {
        bubble.setVisible(asBubble);
        alert.setVisible(!asBubble);
        if (asBubble) {
            dialogText.setFont(DIALOG_FONT);
            dialogText.setFill(Color.BLACK);
            dialogTextFlow.setPadding(new Insets(0, FONT_SIZE / 2, 0, FONT_SIZE / 2));
            dialogTextFlow.setTextAlignment(TextAlignment.CENTER);
            dialogTextFlow.setLineSpacing(-FONT_SIZE * 0.33);
            //dialogTextFlow.setEffect(new DropShadow(20, 10, 10, Color.BLACK));
            VBox.setMargin(dialogTextFlow, new Insets(30));
        } else {
            dialogText.setFont(ALERT_TXT_FONT);
            dialogText.setFill(Color.GREEN);
            dialogTextFlow.setPadding(new Insets(FONT_SIZE));
            dialogTextFlow.setTextAlignment(TextAlignment.CENTER);
            dialogTextFlow.setLineSpacing(FONT_SIZE * 0.33);
            //dialogTextFlow.setEffect(new DropShadow(20, 10, 10, Color.BLACK));
            VBox.setMargin(dialogTextFlow, new Insets(30));
        }
    }

    public void setCameoTranslate(double x, double y) {
        cameoView.setTranslateX(x);
        cameoView.setTranslateY(y);
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
    public ArrayList<DialogSheet2> getDialogList() {
        return dialogList;
    }

    public void addDialogSheet(DialogSheet2 ds) {
        dialogList.add(ds);
        if (currentDialogSheet == null) {
            setCurrentDialogSheet(ds);
        }
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
        dialogText.setText(processText(ds.getDialogText()));
        rebuildResponsePane(ds.getResponse());

        this.currentDialogSheet = ds;
    }

    public void setCurrentDialog(int num) {
        BabbleNode node = dialogChain.get(num);
        LOGGER.log(Level.CONFIG, "Build BabbleNode for: {0}", num);
        answerButtonsBox.getChildren().clear();

        if (node instanceof DialogBabbleNode) {  // Dialog
            LOGGER.log(Level.CONFIG, "Build NPC dialog...");
            // Show NPC Bubble and Hide Alert Bubble
            setBubbleAlertMode(true);
            setCurrentNpc(npc);

            // Text goes into NPC bubble.
            dialogText.setText(processText(node.getText()));
            node.getNumbers().forEach((t) -> {
                // Each 't' must be less than dialog chain length
                // and each referenced item must be a OptionBabbleNode.
                try {
                    BabbleNode optionNode = dialogChain.get(t);
                    if (optionNode instanceof OptionBabbleNode) {
                        Button b = responseButton(t);
                        answerButtonsBox.getChildren().add(b);
                    } else if (optionNode instanceof DialogBabbleNode) {
                        Button b = nextButton(" >>> ");
                        answerButtonsBox.getChildren().add(b);
                        b.setOnAction((tt) -> {
                            // Set next dialog to t
                            setCurrentDialog(t);
                        });
                    } else {
                        throw new IndexOutOfBoundsException("Only OptionBabbleNode or DialogCommand allowed here.");
                    }
                } catch (IndexOutOfBoundsException ex) {
                    if (t >= DialogCommand.CMD_BASE) {
                        processCommand(num, t);
                    } else {
                        LOGGER.log(Level.SEVERE,
                                "Dialog Chain element at #{0} Syntax Error: "
                                + "Unknown referenced Dialog Chain Index number: {1}",
                                new Object[]{num, t});
                    }
                }
            });
        } else if (node instanceof NarrationBabbleNode) {
            // Narration
            LOGGER.log(Level.CONFIG, "Build Narration dialog...");
            vignette.getGameState().getNarrationQue().add(node.getText());

            // Execute commands here
            node.getNumbers().forEach((t) -> {
                if (t < DialogCommand.DESC.num) {
                    // Set next dialog index to this.
                    setCurrentDialog(t);
                } else {
                    // Execute command.
                    // No OptionBabbleNode elements allowed here.
                    processCommand(num, t);
                }
            });
        } else if (node instanceof AlertBabbleNode) { // Syntax Error. Not allowed.
            LOGGER.log(Level.SEVERE, "Build Alert dialog...");
            setBubbleAlertMode(false); // Hide NPC text bubble. Show alert bubble.

            dialogText.setText(processText(node.getText()));
            Button b = alertResponseButton(true, node.getNumbers().getLast());
            answerButtonsBox.getChildren().add(b);

            b.setOnAction((bb) -> {
                node.getNumbers().forEach((t) -> {
                    if (t < DialogCommand.DESC.num) {
                        // Set next dialog index to this.
                        setCurrentDialog(t);
                    } else {
                        // Execute command.
                        // No OptionBabbleNode elements allowed here.
                        processCommand(num, t);
                    }
                });
            });

        } else if (node instanceof OptionBabbleNode) { // Syntax Error. Not allowed.
            LOGGER.log(Level.SEVERE, "Syntax Error? :: OptionBabbleNode found at {0}. Expected Dialog or Narration node", num);
        }

        //
        //
        //
        //
        //LOGGER.log(Level.SEVERE, "     ----> Text: " + node.getText());
//        dialogText.setText(processText(node.getText()));
//        answerButtonsBox.getChildren().clear();
//        node.getNumbers().forEach((t) -> {
//            if (t < DESC.num) {
//                if (node instanceof OptionBabbleNode) {
//                    Button b = responseButton(t);
//                    answerButtonsBox.getChildren().add(b);
//                } else if (node instanceof NarrationBabbleNode) {
//                    LOGGER.log(Level.SEVERE, "Do narration action.");
//                    LOGGER.log(Level.SEVERE, "Place text for {0} into narration window.", num);
//                    // Execute items in numbers.
//                    List<Integer> numbers = n.getNumbers();
//                    if (numbers.size() == 1) {
//                        setCurrentDialog(numbers.getFirst());
//                    } else {
//                        LOGGER.log(Level.SEVERE, "Multiple");
//                    }
//                }
//            } else { // command
//                processCommand(num, t);
//                switch (DialogCommand.getCommand(t)) {
//                   case ITEM_BUY -> {
//                        LOGGER.log(Level.FINER, "Build Vend Widget for: " + num);
//                        VendWidget vendWidget = new VendWidget(
//                                npc, vignette.getPlayer(),
//                                vignette.getVendItems(),
//                                answerButtonsBox.getHeight()
//                        );
//                        VBox.setMargin(vendWidget, new Insets(FONT_SIZE / 2));
//                        VBox.getVgrow(vendWidget);
//                        answerButtonsBox.getChildren().add(vendWidget);
//
//                        vendWidget.setOnAction((tt) -> {
//                            tt.consume();
//                            ///  do these action(s)
//
//                            vignette.onVendItemsFinished();
//                        });
//                    }
//                    case DIALOG_NO_MORE -> {
//                        vignette.getCharacterList().get(0).setAllowTalk(false);
//                        doCloseDialog();
//                    }
//                    default -> {
//                        LOGGER.log(Level.SEVERE, "DialogChain: item " + num + " defines unknown or unhandled command: " + t);
//                    }
//                }
//            }
//        });
    }

    private void rebuildResponsePane(ArrayList<DialogResponse2> responseList) {
        answerButtonsBox.getChildren().clear();
        responseList.forEach((t) -> {
            Button b = responseButton(t);
            answerButtonsBox.getChildren().add(b);
        });
    }

    private Button alertResponseButton(boolean typeOk, int response) {
        Text bText;
        if (response == RESPONSE_OK) {
            bText = new Text("OK");
        } else {
            bText = new Text("NEXT");
        }
        bText.setTextAlignment(TextAlignment.CENTER);
        bText.setFont(ALERT_BTN_FONT);
        CornerRadii cornerRadii = new CornerRadii(ALERT_BTN_FONT.getSize() / 4);
        Button b = new Button("", bText);
        b.setBorder(new Border(
                new BorderStroke(Color.BLACK.brighter(),
                        BorderStrokeStyle.SOLID,
                        cornerRadii,
                        new BorderWidths(3)
                )));
        b.setBackground(new Background(new BackgroundFill(Color.web("373"), cornerRadii, Insets.EMPTY)));

        return b;
    }

    private Button nextButton(String txt) {
        Text bText = new Text(processText(txt));
        //bText.setWrappingWidth(ViewPane.WIDTH * 0.18);
        bText.setTextAlignment(TextAlignment.CENTER);
        bText.setFont(ALERT_BTN_FONT);
        CornerRadii cornerRadii = new CornerRadii(ALERT_BTN_FONT.getSize() / 4);

        Button b = new Button("", bText);
        b.setBorder(new Border(
                new BorderStroke(Color.DARKGREEN.darker(),
                        BorderStrokeStyle.SOLID,
                        cornerRadii,
                        new BorderWidths(3)
                )));
        b.setBackground(new Background(new BackgroundFill(Color.PALEGREEN.darker(), cornerRadii, Insets.EMPTY)));

        return b;
    }

    private Button responseButton(String txt) {
        Text bText = new Text(processText(txt));
        bText.setWrappingWidth(ViewPane.WIDTH * 0.38);
        bText.setTextAlignment(TextAlignment.CENTER);
        bText.setFont(ANSWER_FONT);
        CornerRadii cornerRadii = new CornerRadii(ANSWER_FONT.getSize() / 2);

        Button b = new Button("", bText);
        b.setBorder(new Border(
                new BorderStroke(Color.BLACK.brighter(),
                        BorderStrokeStyle.SOLID,
                        cornerRadii,
                        new BorderWidths(2)
                )));
        b.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, cornerRadii, Insets.EMPTY)));

        return b;
    }

    private Button responseButton(int response) {
        LOGGER.log(Level.CONFIG, "Build Response Button for: {0}", response);
        BabbleNode node = dialogChain.get(response);
        if (node instanceof OptionBabbleNode obn) {
            Button b = responseButton(processText(obn.getText()));
//            Text bText = new Text(processText(obn.getText()));
//            //LOGGER.log(Level.SEVERE, "     ----> Text: " + obn.getText());
//            bText.setWrappingWidth(ViewPane.WIDTH * 0.38);
//            bText.setTextAlignment(TextAlignment.CENTER);
//            bText.setFont(ANSWER_FONT);
//            CornerRadii cornerRadii = new CornerRadii(ANSWER_FONT.getSize() / 2);
//
//            Button b = new Button("", bText);
//            b.setBorder(new Border(
//                    new BorderStroke(Color.BLACK.brighter(),
//                            BorderStrokeStyle.SOLID,
//                            cornerRadii,
//                            new BorderWidths(2)
//                    )));
//            b.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, cornerRadii, Insets.EMPTY)));
            b.setOnAction((tt) -> {
                tt.consume();
                ///  do these action(s)
                obn.getNumbers().forEach((t) -> {
                    // Apply each command.
                    if (t < DialogCommand.DESC.num) {
                        // Set new dialog to final number.
                        setCurrentDialog(t);
                    } else {
                        // Command
                        processCommand(response, t);
                    }
                });
            });

            return b;
        } else {
            String msg = "Error:  Node is not a OptionBabbleNode.";
            LOGGER.log(Level.SEVERE, msg);

            return new Button(msg);
        }

    }

    private Button responseButton(DialogResponse2 response) {
        Text bText = new Text(processText(response.getText()));
        bText.setWrappingWidth(ViewPane.WIDTH * 0.38);
        bText.setTextAlignment(TextAlignment.CENTER);
        bText.setFont(ANSWER_FONT);
        CornerRadii cornerRadii = new CornerRadii(FONT_SIZE / 2);

        Button b = new Button("", bText);
        b.setBorder(new Border(
                new BorderStroke(Color.BLACK.brighter(),
                        BorderStrokeStyle.SOLID,
                        cornerRadii,
                        new BorderWidths(2)
                )));
        b.setBackground(Background.EMPTY);
        b.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, cornerRadii, Insets.EMPTY)));
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

    /**
     * Replace var markers ($0 - $9) with the pre-set var value.
     *
     * @param text containing var markers $0 - $9.
     * @return text with substituted var[n] String values.
     */
    private String processText(String text) {
        if (vars == null) {
            return text;
        }
        String pText = text;
        for (int i = 0; i < vars.length; i++) {
            String pVar = "$" + Integer.toString(i);
            if (pText.contains(pVar)) {
                // Need to escape with double back-slashes because $ is a regex character.
                pText = pText.replaceAll(('\\' + pVar), vars[i]);
            }
        }

        return pText;
    }

    private void processCommand(int dcNum, int command) {
        LOGGER.log(Level.INFO,
                "DialogPane: Process Command for Dialog Chain#{0}  => {1}",
                new Object[]{dcNum, DialogCommand.getCommand(command).name()}
        );
        switch (DialogCommand.getCommand(command)) {
            case DESC -> { // Depricated
                LOGGER.log(Level.SEVERE, "Process Description Command. TODO!");
            }
            case DESC_NEXT -> { // Depricated
                LOGGER.log(Level.SEVERE, "Process Description Next Command. TODO!");
            }
            case NPC2 -> {
                LOGGER.log(Level.SEVERE, "Process NPC2 command.");
                setCurrentNpc(npc2);
            }
            case EXIT_T -> {
                LOGGER.log(Level.CONFIG, "Process Exit Top Command.");
                Button b = nextButton(" >>> ");
                answerButtonsBox.getChildren().add(b);
                b.setOnAction((ba) -> {
                    for (VignetteTrigger t : vignette.getDoors()) {
                        if (t.getLocation().equals(VignetteTrigger.Location.TOP)) {
                            vignette.getGameState().setNextRoom(t);
                            return;
                        }
                    }
                    LOGGER.log(Level.SEVERE, "No exit found for: EXIT_B");
                });
            }
            case EXIT_R -> {
                LOGGER.log(Level.CONFIG, "Process Exit Right Command.");
                Button b = nextButton(" >>> ");
                answerButtonsBox.getChildren().add(b);
                b.setOnAction((ba) -> {
                    for (VignetteTrigger t : vignette.getDoors()) {
                        if (t.getLocation().equals(VignetteTrigger.Location.RIGHT)) {
                            vignette.getGameState().setNextRoom(t);
                            return;
                        }
                    }
                    LOGGER.log(Level.SEVERE, "No exit found for: EXIT_R");
                });
            }
            case EXIT_B -> {
                LOGGER.log(Level.CONFIG, "Process Exit Bottom Command.");
                Button b = nextButton(" >>> ");
                answerButtonsBox.getChildren().add(b);
                b.setOnAction((ba) -> {
                    for (VignetteTrigger t : vignette.getDoors()) {
                        if (t.getLocation().equals(VignetteTrigger.Location.BOTTOM)) {
                            vignette.getGameState().setNextRoom(t);
                            return;
                        }
                    }
                    LOGGER.log(Level.SEVERE, "No exit found for: EXIT_B");
                });
            }
            case EXIT_L -> {
                LOGGER.log(Level.CONFIG, "Process Exit Left Command.");
                Button b = nextButton(" >>> ");
                answerButtonsBox.getChildren().add(b);
                b.setOnAction((ba) -> {
                    for (VignetteTrigger t : vignette.getDoors()) {
                        if (t.getLocation().equals(VignetteTrigger.Location.LEFT)) {
                            vignette.getGameState().setNextRoom(t);
                            return;
                        }
                    }
                    LOGGER.log(Level.SEVERE, "No exit found for: EXIT_L");
                });
            }
            case EXIT_GENERIC -> {
                LOGGER.log(Level.CONFIG, "Process Exit to Generic Location (NONE).");

                // TODO: Replace with glyph for justiace.
                Button b = nextButton(" >>> ");
                answerButtonsBox.getChildren().add(b);
                b.setOnAction((ba) -> {
                    for (VignetteTrigger t : vignette.getDoors()) {
                        if (t.getLocation().equals(VignetteTrigger.Location.NONE)) {
                            vignette.getGameState().setNextRoom(t);
                            return;
                        }
                    }
                    LOGGER.log(Level.SEVERE, "No exit found for: EXIT_GENERIC");
                });
            }
            case TO_JAIL -> {
                LOGGER.log(Level.CONFIG, "Process Go to Jail Command.");

                // TODO: Replace with glyph for justiace.
                Button b = nextButton(" >>> ");
                answerButtonsBox.getChildren().add(b);
                b.setOnAction((ba) -> {
                    for (VignetteTrigger t : vignette.getDoors()) {
                        if (t.getLocation().equals(VignetteTrigger.Location.JAIL)) {
                            vignette.getGameState().setNextRoom(t);
                            return;
                        }
                    }
                });
            }
            case DEATH -> {
                LOGGER.log(Level.CONFIG, "Process Death Command.");

                // TODO: Replace with glyph for Death.
                Button b = nextButton(" >>> ");
                answerButtonsBox.getChildren().add(b);
                b.setOnAction((ba) -> {
                    for (VignetteTrigger t : vignette.getDoors()) {
                        if (t.getLocation().equals(VignetteTrigger.Location.DEATH)) {
                            vignette.getGameState().setNextRoom(t);
                            vignette.getPlayer().setConstitution(0); // Kill
                            return;
                        }
                    }
                });
            }
            case ITEM_GET -> {
                LOGGER.log(Level.INFO, "Process Item Get Command.");
                vignette.onItemGet();
            }
            case ITEM_BUY -> {
                LOGGER.log(Level.FINER, "Build Vend Widget for dialogChain item: {0}", dcNum);
                VendWidget vendWidget = new VendWidget(
                        npc, vignette.getPlayer(),
                        answerButtonsBox.getHeight()
                );
                int vendState = vignette.onVendItemsStart(vendWidget);
                if (vendState < 0) { // Perform any pre-actions in custom vignette.
//                    VendWidget vendWidget = new VendWidget(
//                            npc, vignette.getPlayer(),
//                            vignette.getVendItems(),
//                            answerButtonsBox.getHeight()
//                    );
                    vendWidget.init();
                    VBox.setMargin(vendWidget, new Insets(FONT_SIZE / 2));
                    VBox.getVgrow(vendWidget);
                    answerButtonsBox.getChildren().add(vendWidget);

                    vendWidget.setOnAction((tt) -> {
                        tt.consume();
                        ///  do these action(s) in the custom Vingette.
                        vignette.onVendItemsFinished();
                    });
                } else {
                    // Set the dialog to this index.
                    setCurrentDialog(vendState);
                }
            }
            case DIALOG_CLOSE -> {
                LOGGER.log(Level.CONFIG, "Process Close command.");
                doCloseDialog();
            }
            case DIALOG_NO_MORE -> {
                LOGGER.log(Level.CONFIG, "Process Dialog-No-More command.");
                vignette.getCharacterList().get(0).setAllowTalk(false);
                doCloseDialog();
            }
            case DIALOG_NO_X -> { // Hide the "X" that allows clossing dialog.
                LOGGER.log(Level.CONFIG, "Process Dialog-No-X Command.");
                closeX.setVisible(false);
            }
            default -> {
                LOGGER.log(Level.SEVERE,
                        "DialogChain: item {0} defines unknown or unhandled command: {1}",
                        new Object[]{dcNum, command});
            }
        }

    }

    public void setDialogChain(ArrayList<BabbleNode> dChain) {
        this.dialogChain = dChain;
    }
}
