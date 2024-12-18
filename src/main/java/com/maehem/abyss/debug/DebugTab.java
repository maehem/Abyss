/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

*/
package com.maehem.abyss.debug;

import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.logging.LogListener;
import com.maehem.abyss.logging.LoggingMessageList;
import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javafx.animation.AnimationTimer;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;

/**
 *
 * @author maehem
 */
public class DebugTab extends Group implements LogListener {

    private final static double SIZE = 10;
    private final Font FONT = Font.loadFont(
            getClass().getResourceAsStream("/fonts/OxygenMono-Regular.ttf"), SIZE);

    public static final double CORNER_ARC = 10;
    public static final double HEIGHT = 700;
    public static final double WIDTH = 100;

    final Font REGULAR = FONT;
    final Font ITALIC = Font.font(FONT.getFamily(), FontPosture.ITALIC, SIZE);
    final Font BOLD = Font.font(FONT.getFamily(), FontWeight.BOLD, SIZE);
    TextFlow tf = new TextFlow();

    private Formatter formatter = null;

    BorderPane panel = new BorderPane();
    private boolean showing = false;
    private final LoggingMessageList messageLog;
    private final Slider slider = new Slider(0, 6, 3);
    private ScrollPane logMessagePane;

    public DebugTab(LoggingMessageList messageLog, GameState gs) {
        this.messageLog = messageLog;
        messageLog.addListener(this);

        panel.setRight(initControlsPane(gs));
        panel.setCenter(getMessagePane());

        panel.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(CORNER_ARC), Insets.EMPTY)));
        panel.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.FULL, new Insets(CORNER_ARC / 2))));

        getChildren().addAll(panel );
    }

    public void setShowing(boolean newValue) {
        showing = newValue;

        // Animate the panel to slide in or out.
        int steps = 15;
        double endAmount;
        double startAmount = getTranslateY();
        double stepAmount;

        if (showing) {
            endAmount = -CORNER_ARC / 2;
            //setTranslateY(-CORNER_ARC);
        } else {
            endAmount = -panel.getHeight();
            //setTranslateY(-panel.getHeight());
        }
        stepAmount = (startAmount - endAmount) / steps;

        AnimationTimer at = new AnimationTimer() {
            @Override
            public void handle(long l) {

                setTranslateY(getTranslateY() - stepAmount);
                if (showing) {
                    if (getTranslateY() >= endAmount) {
                        setTranslateY(endAmount);
                        stop();
                    }
                } else {
                    if (getTranslateY() <= endAmount) {
                        setTranslateY(endAmount);
                        stop();
                    }
                }
            }
        };

        at.start();
    }

    private Node getMessagePane() {
        logMessagePane = new ScrollPane();
        logMessagePane.setPrefSize(640, 200);
        logMessagePane.setContent(tf);

        // The scrollpane view port is not created until later, so we
        // trigger off the widthProperty event to chaneg the bacground color,
        // which happens once the window is realized.
        logMessagePane.widthProperty().addListener((o) -> {
            Node vp = logMessagePane.lookup(".viewport");
            vp.setStyle("-fx-background-color:#333333;");
        });

        tf.setLineSpacing(10 / SIZE);
        // Cause the scroll to go to bottom whenever a new message happens.
        tf.getChildren().addListener((ListChangeListener<? super Node>) change -> {
            logMessagePane.layout();
            logMessagePane.setVvalue(logMessagePane.getVmax());
        });

        return logMessagePane;
    }

    private Node initControlsPane(GameState gs) {
        HBox cp = new HBox();
        cp.setPadding(new Insets(4));

        //VBox leftPane = new VBox();
        //GridPane centerPane = new GridPane();
        cp.getChildren().addAll(
                initDebugLevelSliderPane(),
                new DebugTogglesPanel(gs)
        );
        cp.setBorder(new Border(new BorderStroke(new Color(1, 0, 0, 1), BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(6))));

        return cp;
    }

    private VBox initDebugLevelSliderPane() {
        Text sliderLabel = new Text("Logging\nLevel");
        sliderLabel.setFont(new Font(10));
        sliderLabel.setTextAlignment(TextAlignment.CENTER);
        //slider = new Slider(0, 6, 6);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.setLabelFormatter(new StringConverter<Double>() {
            private final static String ERR = "Errors Only";
            private final static String WRN = "Warning";
            private final static String INF = "Info";
            private final static String CNF = "Config";
            private final static String FIN = "Fine";
            private final static String FNR = "Finer";
            private final static String FNT = "I am Neo";

            @Override
            public String toString(Double n) {
                if (n < 0.5) {
                    return ERR;
                }
                if (n < 1.5) {
                    return WRN;
                }
                if (n < 2.5) {
                    return INF;
                }
                if (n < 3.5) {
                    return CNF;
                }
                if (n < 4.5) {
                    return FIN;
                }
                if (n < 5.5) {
                    return FNR;
                }

                return FNT;
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case ERR:
                        return 0d;
                    case WRN:
                        return 1d;
                    case INF:
                        return 2d;
                    case CNF:
                        return 3d;
                    case FIN:
                        return 4d;
                    case FNR:
                        return 5d;
                    case FNT:
                        return 6d;

                    default:
                        return 6d;
                }
            }
        });
        slider.valueProperty().addListener((o) -> {
            reloadDebugLog();
        });
        //LOGGER.log(Level.CONFIG, "Slider value is: {0}", slider.getValue());

        VBox p = new VBox();
        p.getChildren().addAll(sliderLabel, slider);
        p.setSpacing(4);
        p.setAlignment(Pos.CENTER);

        return p;
    }

    public void reloadDebugLog() {
            // Clear and regenerate the TextFlow for the log messages.
            tf.getChildren().clear();
            messageLog.forEach((t) -> {
                messageAdded(t);
                //LOGGER.config("Log Record Level: " + t.getLevel().intValue());

            });
    }

    public void setFormatter(Formatter f) {
        this.formatter = f;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    @Override
    public void messageAdded(LogRecord record) {
        int val = (int) (slider.getMax() - slider.getValue());
        switch (val) {
            case 0:
                break;// Level 300.  Always continue. FINEST
            case 1:
                if (record.getLevel().intValue() >= 400)  break;
                return;
            case 2:
                if (record.getLevel().intValue() >= 500) break;
                return;
            case 3:
                if (record.getLevel().intValue() >= 700) break;
                return;
            case 4:
                if (record.getLevel().intValue() >= 800) break;
                return;
            case 5:
                if (record.getLevel().intValue() >= 900) break;
                return;
            case 6:
                if (record.getLevel().intValue() >= 1000) break;
                return;
        }

        String message;
        if (formatter != null) {
            message = MessageFormat.format(getFormatter().format(record), record.getParameters());
        } else {
            message = record.getMessage();
        }
        Text messageText = new Text(message);
        messageText.setFont(REGULAR);
        if (record.getLevel() == Level.SEVERE) {
            messageText.setFill(Color.RED.desaturate());
            messageText.setFont(BOLD);
        } else if (record.getLevel() == Level.WARNING) {
            messageText.setFill(Color.ORANGE);
            messageText.setFont(BOLD);
        } else if (record.getLevel() == Level.INFO) {
            messageText.setFill(Color.SPRINGGREEN);
        } else if (record.getLevel() == Level.CONFIG) {
            messageText.setFill(Color.LIGHTGREEN);
        } else if (record.getLevel() == Level.FINE) {
            messageText.setFill(Color.LIGHTGRAY);
            messageText.setFont(ITALIC);
        } else if (record.getLevel() == Level.FINER) {
            messageText.setFill(Color.DARKGREY);
            messageText.setFont(ITALIC);
        } else {
            messageText.setFill(Color.DARKGREY.darker());
            messageText.setFont(ITALIC);
        }

        tf.getChildren().add(messageText);
    }
}
