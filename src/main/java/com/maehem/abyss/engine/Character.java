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

import com.maehem.abyss.engine.babble.DialogScreen;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *  // TODO:  Unlink Character model from view
 * 
 * @author Mark J Koch [@maehem on GitHub]
 */
public class Character extends Group {

    private static final String TALK_ICON_IMAGE_FILENAME = "/icons/talk-icon.png";

    public static final int INVENTORY_SIZE = 35;
    //public static final double SHEET_SCALE = 0.5;
    
    private final ArrayList<Thing> inventory = new ArrayList<>(INVENTORY_SIZE);

    private String name;
    private PoseSheet poseSheet;
    private Rectangle feetBoundary;
    private Ellipse hearingBoundary;
    private ImageView talkIcon = new ImageView();
    private boolean allowTalk = false;
    private boolean talking = false;

    
    private AnimationTimer aniTimer;
    private long lastTime = 0;
    private final long TWAIT = 40000000;
    
    private final DialogScreen dialogScreen;
    private double originX;
    private double originY;

    public Character() {
        this("???");        
    }
    
    public Character( String name ) {
        LOGGER.log(Level.CONFIG, "{0}: Create character: {1}", new Object[]{getClass().getSimpleName(), name});
        this.name = name;
        
        // Fill the inventory with EmptyThing placeholders.
        for ( int i=0; i< INVENTORY_SIZE; i++ ) {
            inventory.add(new EmptyThing());
        }
        
        this.poseSheet = new PoseSheet(200);
        getChildren().add(this.poseSheet);
        // set origin
        // Translate the image such that our image position 
        // is relative to the feet.
        setOrigin(0.5, 0.9);
        
        setDefaultHearingBoundary();
        initFeetBoundary();
        initTalkIcon();
        
        dialogScreen = new DialogScreen(this);    
    }

//    private Node bindBox( String label, DoubleProperty n1, DoubleProperty n2 ) {
//        Color fill = Color.WHITESMOKE;
//        Font f = new Font(4.0);
//        
//        Text labelText = new Text(label + ": ");
//        labelText.setFont(f);
//        labelText.setFill(fill);
//        
//        Text n1Text = new Text();
//        n1Text.textProperty().bind(n1.asString());
//        n1Text.setFont(f);
//        n1Text.setFill(fill);
//        
//        Text comma = new Text(",");
//        comma.setFill(fill);
//        comma.setFont(f);
//        
//        Text n2Text = new Text();
//        n2Text.textProperty().bind(n2.asString());
//        n2Text.setFill(fill);
//        n2Text.setFont(f);
//        
//        return new HBox(labelText, n1Text, comma, n2Text);
//    }
        
    /**
     * Translate the character image so that the center (layoutX/Y)
     * is at this place in the image. Usually near the feet of the character.
     * setOrigin(0.5, 0.8) is a good starting point.
     * 
     * @param x range 0.0-1.0 (left to right)
     * @param y range 0.0-1.0 (top to bottom(feet))
     */
    public final void setOrigin(double x, double y) {
        LOGGER.log(Level.FINE, "character set origin: {0},{1}", new Object[]{x, y});
        this.originX = x;
        this.originY = y;
        
        updateOrigin();
    }
    
    private void updateOrigin() {
        // Original width and height.
        double pH = getPoseSheet().getHeight();
        double pW = getPoseSheet().getWidth();
        
        // Scaled width and height.
        Bounds b = getBoundsInParent();
        double bW = b.getWidth();
        double bH = b.getHeight();
        //LOGGER.log(Level.CONFIG, "Parent Bounds for character: {0}x{1}\n", new Object[]{b.getWidth(), b.getHeight()});
        // After scaling, translate new X or Y position back to 0, then translate to desired originX/Y.
        setTranslateX( ((-pW + bW)/2 - bW * originX ));
        setTranslateY( ((-pH + bH)/2 - bH * originY ));
        
    }
    
    public final void setDefaultHearingBoundary() {
        double clipW = getPoseSheet().getWidth();
        double clipH = getPoseSheet().getHeight();
        
        getChildren().remove(getHearingBoundary());
        
        hearingBoundary = new Ellipse(clipW/2, clipW/4);
        getHearingBoundary().setCenterX(clipW/2);
        getHearingBoundary().setCenterY(clipH-getHearingBoundary().getRadiusY());
        
        getHearingBoundary().setStrokeWidth(2.0);
        getHearingBoundary().setStroke(Color.HONEYDEW);
        getHearingBoundary().setFill(Color.TRANSPARENT);
        
        getChildren().add(getHearingBoundary());        
    }
    
    final void initTalkIcon() {
        double clipW = getPoseSheet().getWidth();
        
        talkIcon = new ImageView();
        talkIcon.setImage(new Image(getClass().getResourceAsStream(TALK_ICON_IMAGE_FILENAME)));
        talkIcon.setPreserveRatio(true);
        talkIcon.setFitWidth(clipW/3);
        talkIcon.setX(clipW-talkIcon.getFitWidth());
        
        // Display the talk icon such that the pointy bit is at mouth level.
        talkIcon.setY(-talkIcon.getBoundsInLocal().getHeight()/2);
        
        getChildren().add(talkIcon);        
        
        talkIcon.setOnMouseClicked((event) -> {
            LOGGER.log(Level.WARNING, "Opacity = {0}", talkIcon.getOpacity());
            event.consume();
            if ( talkIcon.getOpacity() > 0.0 ) {
                setTalking(true);
            }
        });
        
        showTalkIcon(false);
    }
    
    public void moveRight(int i, Shape safeZone) {
        // Try moving our shape to the new location.
        setLayoutX(getLayoutX() + i);

        // See if we still intersect with the allowable walking area.
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.RIGHT);
            getPoseSheet().nextPose();
        } else {
            setLayoutX(getLayoutX() - i); // Nope. Put it back
        }
    }

    public void moveLeft(int i, Shape safeZone) {
        setLayoutX(getLayoutX() - i);
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.LEFT);
            getPoseSheet().nextPose();
        } else {
            setLayoutX(getLayoutX() + i); // Put it back
        }
    }

    public void moveUp(int i, Shape safeZone) {
        setLayoutY(getLayoutY() - i);
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.AWAY);
            getPoseSheet().nextPose();
        } else {
            setLayoutY(getLayoutY() + i); // Put it back
        }
    }

    public void moveDown(int i, Shape safeZone) {
        setLayoutY(getLayoutY() + i);
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.TOWARD);
            getPoseSheet().nextPose();
        } else {
            setLayoutY(getLayoutY() - i); // Put it back
        }
    }

    public void setScale(double scale) {
        // TODO:  refactor feet origin into new scale
        setScaleX(scale);
        setScaleY(scale);        
        updateOrigin();

//        Bounds b = getBoundsInParent();
//        LOGGER.log(Level.CONFIG, "scale: {0}  lay:{1},{2}  tran:{3},{4}  size:{5}x{6}", 
//                new Object[]{getScaleX(), 
//                    getLayoutX(),getLayoutY(), 
//                    getTranslateX(), getTranslateY(),
//                    b.getWidth(), b.getHeight()
//                }
//        );
    }

    public boolean colidesWith(Shape s) {
        //return feetBoundary.getBoundsInParent().intersects(s.getBoundsInParent());
        return Shape.intersect(feetBoundary, s).getBoundsInLocal().getWidth() > 0;
    }
    
    public boolean canHear(Shape s) {
        return Shape.intersect(getHearingBoundary(), s).getBoundsInLocal().getWidth() > 0;
    }

    public void showCollisionBounds(boolean show) {
        feetBoundary.setOpacity(show ? 0.5 : 0.0);
    }

    public void showHearingBounds( boolean show ) {
        getHearingBoundary().setOpacity(show ? 0.5 : 0.0);
    }
    
    public void showTalkIcon(boolean show) {
        talkIcon.setOpacity(show ? 1.0 : 0.0);
        if ( !show ) {
            setTalking(false);
        }
    }
    
    public void stopAnimating() {
        if (aniTimer != null) {
            aniTimer.stop();
        }
    }
    
    public void walkToward(double x, double y, Shape pBounds) {
        LOGGER.log(Level.FINEST,
                "Walk toward:   Start X,Y:  {0},{1}    Dest: {2},{3}",
                new Object[]{getLayoutX(), getLayoutY(), x, y}
        );
        // Set toward view.
        stopAnimating();
        aniTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now < lastTime + TWAIT) {
                    return;
                }
                //  If the movement would be less than the regular amount of
                // travel, then make them the same.
                if (Math.abs(getLayoutX() - x) < 12) {
                    setLayoutX(x);
                }
                if (Math.abs(getLayoutY() - y) < 4) {
                    setLayoutY(y);
                }

                // If both X and Y movements are at their final values, then
                // end the timer.
                if (getLayoutX() == x && getLayoutY() == y) {
                    aniTimer.stop();
                    aniTimer = null;
                }

                // Figure out if we are moving left or right
                if (getLayoutX() < x) {
                    LOGGER.finest("Moving Right");
                    moveRight(12, pBounds);
                } else if (getLayoutX() > x) {
                    LOGGER.finest("Moving Left");
                    moveLeft(12, pBounds);
                }

                // Figure out if we are moving up or down
                if (getLayoutY() < y) {
                    LOGGER.finest("Moving Down");
                    moveDown(4, pBounds);
                } else if (getLayoutY() > y) {
                    LOGGER.finest("Moving Up");
                    moveUp(4, pBounds);
                }

                lastTime = now;
            }
        };

        aniTimer.start();
    }


    public final void initFeetBoundary() {
        feetBoundary = new Rectangle();
        feetBoundary.setStroke(Color.LAWNGREEN);
        feetBoundary.setStrokeWidth(2.0);
        feetBoundary.setFill(Color.TRANSPARENT);
        
        setFeetBoundary();
        
        getChildren().add(feetBoundary);        
    }
    
    private void setFeetBoundary() {
        double clipW = getPoseSheet().getWidth();
        double clipH = getPoseSheet().getHeight();
        
        double feetW = clipW/4;
        double feetH = clipH/25;
        
        //double feetX = clipW/2 - feetW/2;        
        double feetX = clipW*originX - feetW/2;        
        //double feetY = clipH - feetH;
        double feetY = clipH*originY - feetH/2;
        

        feetBoundary.setX(feetX);
        feetBoundary.setY(feetY);
        feetBoundary.setWidth(feetW);
        feetBoundary.setHeight(feetH);        
    }

    /**
     * @return the talking
     */
    public boolean isTalking() {
        return talking;
    }

    /**
     * @param talking the talking to set
     */
    public void setTalking(boolean talking) {
        this.talking = talking;
    }

    /**
     * @return the hearingBoundary
     */
    public Ellipse getHearingBoundary() {
        return hearingBoundary;
    }

    /**
     * @return the poseSheetImage
     */
    public final PoseSheet getPoseSheet() {
        return poseSheet;
    }

    public void setSkin(InputStream resourceAsStream, int rows, int cols) {
        getPoseSheet().setSkin(resourceAsStream, rows, cols);
        setFeetBoundary();
        
        // todo reset listen bounds 
        setDefaultHearingBoundary();
        initTalkIcon();
    }

    void setDirection(PoseSheet.Direction playerDir) {
        getPoseSheet().setDirection(playerDir); 
    }

    public void nextPose() {
        getPoseSheet().nextPose(); 
    }
    
    public void useDefaultSkin() {
        getPoseSheet().setDefaultSheet();
        setFeetBoundary();
        initTalkIcon();
    }

    public DialogScreen getDialog() {
        return dialogScreen;                
    }
    
    /**
     * @return the name of this character
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the character name to set
     */
    public final void setName(String name) {
        this.name = name;
    }

//    /**
//     * @return the inventory
//     */
//    public Thing[] getInventory() {
//        return inventory;
//    }

    public ArrayList<Thing> getAInventory() {
        return inventory;
    }
    
    public void setAInventory(int index, Thing thing ) {
        inventory.set(index, thing);
    }
    
    public boolean isInventoryFull() {
        // Fill the inventory with EmptyThing placeholders.
        for ( int i=0; i < inventory.size(); i++ ) {
            if ( inventory.get(i) instanceof EmptyThing ) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Transfer an item from one character/player to another.
     * 
     * @param thing to give away
     * @param character to receive the item
     */
    public void give(Thing thing, Character character) {
        //getInventory().remove(thing);
        character.addInventoryItem(thing);
    }

    /**
     * Add a thing to inventory.
     * 
     * @param thing to be added
     */
    public void addInventoryItem(Thing thing) {
//        for ( int i=0; i<inventory.length; i++) {
//            if ( inventory[i] == null ) {
//                inventory[i] = thing;
//                break;
//            }
//        }
        for( int i=0;  i<inventory.size(); i++ ) {
            if ( inventory.get(i) instanceof EmptyThing) {
                LOGGER.log(Level.INFO, 
                        "Inventory Item: {0} changed from EmptyThing to {1}", 
                        new Object[]{i, thing.getClass().getSimpleName()}
                );
                inventory.set(i, thing);  
                break;
            }
        }
        //getInventory().add(thing);
    }

}
