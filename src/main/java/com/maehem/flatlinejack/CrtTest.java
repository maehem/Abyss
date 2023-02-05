/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maehem.flatlinejack;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.view.ViewPane;
import com.maehem.flatlinejack.engine.Vignette;
import com.maehem.flatlinejack.engine.gui.CrtTextPane;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author mark
 */
public class CrtTest extends Application {
    public static final Logger log = Logger.getLogger("flatline");

    private double SCALE = 2.0;

    private Stage window;
    private CrtTextPane narrationPane;
    private final VBox gamePane = new VBox();
    private final HBox bottomArea = new HBox();  // gui and naration
    private final StackPane root = new StackPane(gamePane);
    private final Scene scene = new Scene(root);

    @Override
    public void start(Stage window) {
        ConsoleHandler handler  = new ConsoleHandler();
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


        //gamePane.setBottom(bottomArea);
        gamePane.getChildren().add(bottomArea);
        GameState gameState = new GameState();
        this.narrationPane = new CrtTextPane(gameState, ViewPane.WIDTH*SCALE/2);
        this.bottomArea.getChildren().addAll(narrationPane );
        
        root.layout();

        window.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}
