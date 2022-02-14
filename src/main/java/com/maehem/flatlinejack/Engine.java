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

import com.maehem.flatlinejack.engine.Port;
import com.maehem.flatlinejack.engine.Loop;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Vignette;
import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.gui.GUI;
import com.maehem.flatlinejack.engine.gui.InventoryPane;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Engine extends Application {

    /**
     * @return the inventoryPane
     */
    public InventoryPane getInventoryPane() {
        return inventoryPane;
    }

    public static final Logger log = Logger.getLogger("flatline");

    private static final int PRELOADER_SHOWTIME_MILLIS = 2000;
    // This is a class name.
    private static final String STARTING_VIGNETTE = "StreetVignette2";

    private GUI gui;
    private InventoryPane inventoryPane;
    private Stage window;
    private Loop loop;   // Game logic Loop
    private final Group root = new Group();
    private final Player player = new Player();
    private final Scene scene = new Scene(root, 1280, 720);
    private final GameState gameState = new GameState();
    private Vignette currentVignette;
    static {
          System.setProperty("java.util.logging.SimpleFormatter.format",
                  "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s%n");
      }

    @Override
    public void start(Stage window) {
        ConsoleHandler handler
                    = new ConsoleHandler();

        // Add console handler as handler of logs
        log.addHandler(handler);

        log.setLevel(Level.FINER);
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.FINER);
        }

        this.window = window;
        window.setScene(this.scene);
        window.setResizable(false);
        //quit when the window is close().
        window.setOnCloseRequest(e -> Platform.exit());


        getGameState().load(STARTING_VIGNETTE);

        getPlayer().loadState(getGameState());

        this.gui = new GUI(this);
        //this.inventoryPane = new InventoryPane(player.getInventory());
        this.inventoryPane = new InventoryPane(player);
        
        String roomName = getGameState().getProperty(GameState.PROP_CURRENT_VIGNETTE);

        // Load the starting room.
        notifyVignetteExit(new Port(roomName));  // Just leveraging the Room Loading System here.
        root.getChildren().addAll(getInventoryPane(), this.getGui());


        initHotKeys();
        initKeyInput();

        window.show();
        getInventoryPane().setVisible(false);
        getInventoryPane().setLayoutX(getScene().getWidth()/2 - getInventoryPane().getWidth()/2);
        getInventoryPane().setLayoutY(getScene().getHeight()/2 - getInventoryPane().getHeight()/2);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void setVignette(Vignette v) {
        root.getChildren().remove(currentVignette);
        this.currentVignette = v;
        currentVignette.loadState(gameState);
        
        root.getChildren().add(v);
        v.toBack();

        window.setTitle(v.getName());

        loop = new Loop(this, v);
        loop.start();

        getGameState().setProperty(GameState.PROP_CURRENT_VIGNETTE, v.getClass().getSimpleName());
    }

    @Override
    public void init() throws Exception {
        long start = System.currentTimeMillis();

        // time consuming initializations
        long duration = System.currentTimeMillis() - start;
        long remainingShowTime = PRELOADER_SHOWTIME_MILLIS - duration;

        if (remainingShowTime > 0) {
            Thread.sleep(remainingShowTime);
        }
    }

    public void notifyVignetteExit(Port nextRoom) {
        if (loop != null) {
            loop.stop();
        }
        // Player - stop doing any movement/actions
        player.stopAnimating();
        
        // Save relevant game data/goals from scene?
        if ( currentVignette != null ) {
            currentVignette.saveState(getGameState());
        }
        
        try {
            Class<?> c = Class.forName(getClass().getPackageName()+ ".content." + nextRoom.getDestination());
            Constructor<?> cons = c.getConstructor(int.class, int.class, Port.class, Player.class);
            Object object = cons.newInstance(1280, 720, nextRoom, getPlayer());
            setVignette((Vignette) object);
            log.log(Level.CONFIG, "Loaded: {0}", nextRoom.getDestination());

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initHotKeys() {
        // Save Game   COMMAND+S
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.S,
                    KeyCombination.META_DOWN);

            @Override
            public void handle(KeyEvent ke) {
                //System.out.println("AAAA Pressed: " + ke.getCharacter());
                if (keyComb.match(ke)) {
                    ke.consume(); // <-- stops passing the event to next node

                    doSave();
                } else {
                    loop.addInputEvent(ke);
                }
            }
        });

    }

    /**
     * @return the gameState
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * @return the gui
     */
    public GUI getGui() {
        return gui;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    public void doSave() {
        // Push all state values into game state.
        currentVignette.saveState(getGameState());

        // Save the file.
        getGameState().quickSave();
    }

    public void doExit() {
        
        // To do.     Ask if user is sure.
        
        // Check if we need to save.
        
        
        
        
        
        log.warning("Exit called.  Quitting game.");
        window.close();
    }

    private void initKeyInput() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
              if(key.getCode()==KeyCode.ENTER) {
                  System.out.println("You pressed enter");
              }
        });
    }
}
