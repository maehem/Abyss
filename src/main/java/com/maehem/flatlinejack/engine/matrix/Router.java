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
package com.maehem.flatlinejack.engine.matrix;

import static com.maehem.flatlinejack.Engine.LOGGER;
import java.util.ArrayList;
import static java.util.logging.Level.INFO;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

/**
 * A tool for generating site floor circuit patterns.
 *
 * @author mark
 */
public class Router extends Pane {

    private final Group drawArea = new Group();
    private final int nBits;

    private final static int T = 0;
    private final static int R = 1;
    private final static int B = 2;
    private final static int L = 3;

    private int side[] = {0, 0, 0, 0};

//    private int top = 0;
//    private int right = 0;
//    private int bottom = 0;
//    private int left = 0;
    private final double grid;
    private final char[][] matrix; // .= none, s=stub, v=via, c=connected
    private final char X_NONE = '.';
    private final char X_CON = 'C';
    private final char X_VIA = 'V';
    private final char X_STUB = 'S';

    public Router(int nBits, double size) {
        this.nBits = nBits;

        this.matrix = new char[2*nBits][2*nBits];
        clearMatrix();

        setPrefSize(size, size);
        getChildren().add(drawArea);

        setClip(new Rectangle(size, size));

        setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(5)
        )));
        grid = size / (nBits + 1);
    }

    public void setTop(int bits) {
        this.side[T] = bits;
    }

    public void setRight(int bits) {
        this.side[R] = bits;
    }

    public void setBottom(int bits) {
        this.side[B] = bits;
    }

    public void setLeft(int bits) {
        this.side[L] = bits;
    }

    public void generate() {
        drawArea.getChildren().clear();
        clearMatrix();

        // Corners
        genCorners(0, 1.0); // First and last pins
        genCorners(1, 0.8); // Second and second from last pins. 
        genCorners(2, 0.4); // Third and third from last pins. 

        dumpMatrix();

        // Stub remaining pins
        for (int i = 3; i < (nBits + 1) / 2; i++) {
            genStub(i);
        }

        dumpMatrix();

        //int nH = nBits*2;
        
        //build a stub list (side, pin)
        ArrayList<Edge> stubList = new ArrayList<>();
        for (int b = 1; b < nBits-1; b++) {
            if (matrix[0][2*b] == X_STUB) {
                stubList.add(new Edge(T, b));
            }
            if (matrix[b][2*(nBits-1)] == X_STUB) {
                stubList.add(new Edge(R, b));
            }
            if (matrix[2*(nBits-1)][2*b] == X_STUB) {
                stubList.add(new Edge(B, b));
            }
            if (matrix[2*b][0] == X_STUB) {
                stubList.add(new Edge(L, b));
            }
        }

        while (!stubList.isEmpty()) {
            // Pick a random stub and go forth.
            Edge e = stubList.get((int) (Math.random() * stubList.size()));
            // Traverse toward center (straight or diag) until reached or end(via).
            route(e);
            // Remove the stub.
            stubList.remove(e);
        }

        LOGGER.log(INFO, "All routed!");
        // genCornerVias
        //genCornerVias();
    }

    private void route(Edge e) {
        LOGGER.log(INFO, "Route: e={0}   bit={1}", new Object[]{e.side, e.bit});
        int dir = 0;
        if ( e.bit < nBits/2 ) {
            dir = 1;
        } else if ( e.bit > (nBits/2)) {
            dir = -1;
        }
        LOGGER.log(INFO, "Dir:: bit: " + e.bit + "  direction:" + dir);
        switch (e.side) {
            case T:
                Coord root = new Coord(e.bit, 0);
                matrix[2*root.y][2*root.x] = X_CON;
                for (int y = 1; y < ((nBits + 1) / 2); y++) {
                    ArrayList<Coord> choices = new ArrayList<>();
                    if (matrix[2*(root.y+1)][2*root.x] == X_NONE) {
                        
                        choices.add(new Coord(root.x, root.y+1));
                    }
                    if ( matrix[2*(root.y+1)][2*(root.x+dir)] == X_NONE &&
                         matrix[2*root.y + 1][2*root.x + dir] == X_NONE    ) { // Check for crossing line
                        choices.add(new Coord(root.x+dir, root.y+1));                        
                    }
                    if ( choices.isEmpty() ) {
                        //addVia(root.x, root.y);
                        break; // end of the line
                    } else {
                        Coord c = choices.get((int)(Math.random()*choices.size()));
                        if ((root.x-c.x) != 0 ){
                            matrix[2*root.y+1][2*root.x+dir] = 'a'; // diag
                        } else {
                            matrix[2*root.y+1][2*root.x] = 'b';  // straight                        
                        }
                        matrix[2*c.y][2*c.x] = 'f';
                        Line l = new Line(root.x*grid, root.y*grid, c.x*grid, c.y*grid);
                        l.setLayoutY(grid);
                        l.setLayoutX(grid);
                        
                        drawArea.getChildren().add(l);
                        root = c;
                    }
                }
                dumpMatrix();
                addVia(grid+grid*root.x, grid+grid*root.y);
                
                break;
            case R:
                break;
            case B:
                break;
            case L:
                break;
        }
    }

    private boolean checkIntersect( Bounds b ) {
        for ( Node n: drawArea.getChildren() ) {
            return n.intersects(b);
        }
        
        return false;
    }
    
    private void dumpMatrix() {
        // dump matrix
        StringBuilder sb = new StringBuilder("Matrix Dump:\n");
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                sb.append(matrix[y][x]).append(' ');
            }
            sb.append("\n");
        }
        LOGGER.log(INFO, sb.toString());

    }

    private boolean doMaybe(double probability) {
        return Math.random() < probability;
    }

//    private void genCornerVias() {
//        int bH = nBits-1;
//        
//        if ( !(matrix[0][0]) ) {
//            addVia(0.707*grid, 0.707*grid);
//        }
//        if ( !(matrix[0][bH]) ) {
//            addVia(nBits*grid, grid);
//        }
//        if ( !(matrix[bH][0]) ) {
//            addVia(grid, nBits*grid);
//        }
//        if ( !(matrix[bH][bH]) ) {
//            addVia(nBits*grid, nBits*grid);
//        }
//        
//    }
//    
    private void addVia(double x, double y) {
        // draw circle
        Circle c = new Circle(10, Color.GREEN);
        c.setLayoutX(x);
        c.setLayoutY(y);

        drawArea.getChildren().add(c);
    }

    private void genCorners(int bitL, double probability) {
        int last = nBits - 1;
        double g = grid;
        int bitH = last - bitL;
        double gL = (bitL + 1) * g;
        double gH = (bitH + 1) * g;

        // T1, L1
        if (isSet(side[T], bitL) && isSet(side[L], bitL) /*&& doMaybe(probability) */) {
            for (int i = 0; i < bitL + 1; i++) {
                matrix[bitL*2][i*2] = X_CON;
                matrix[2*i][bitL*2] = X_CON;
                if ( i>0 ) {
                    matrix[2*i-1][bitL*2] = X_CON;
                    matrix[bitL*2][i*2-1] = X_CON;
                }
            }
            //matrix[bitL][bitL] = X_CON;
            //Line l = new Line( 0.5*gL,   gL, gL,    0.5*gL );
            Polyline p = new Polyline(
                    0, gL,
                    0.5 * gL, gL,
                    gL, 0.5 * gL,
                    gL, 0
            );
            //p.setLayoutX( g );
            drawArea.getChildren().add(p);
        } else if (isSet(side[T], bitL)) {  // Stub
            matrix[0][bitL*2] = X_STUB;
            matrix[1][bitL*2] = X_CON;
            Line l = new Line(gL, 0, gL, g);
            drawArea.getChildren().add(l);
        } else if (isSet(side[L], bitL)) {  // Stub
            matrix[bitL*2][0] = X_STUB;
            matrix[bitL*2][1] = X_CON;
            Line l = new Line(0, gL, g, gL);
            drawArea.getChildren().add(l);
        }

        // Tn-2, R1
        if (isSet(side[T], bitH) && isSet(side[R], bitL) /*&& doMaybe(probability)*/) {
            for (int i = 0; i < bitL + 1; i++) {
                matrix[2*bitL][2*(last - i)] = X_CON;
                if ( i < bitL) matrix[2*bitL][2*(last - i)-1] = X_CON;
                matrix[2*i][2*bitH] = X_CON;
                if ( i>0 ) matrix[2*i-1][2*bitH] = X_CON;
            }
            //matrix[bitL][bitH] = X_CON;
            Polyline p = new Polyline(
                    0, 0,
                    0, 0.5 * gL,
                    0.5 * gL, gL,
                    gL, gL
            );
            p.setLayoutX(gH);
            drawArea.getChildren().add(p);
        } else if (isSet(side[T], bitH)) { // Stub
            matrix[0][2*bitH] = X_STUB;
            matrix[1][2*bitH] = X_CON;
            Line l = new Line(0, 0, 0, g);
            l.setLayoutX(gH);
            drawArea.getChildren().add(l);
        } else if (isSet(side[R], bitL)) { // Stub
            matrix[2*bitL][2*last] = X_STUB;
            matrix[2*bitL][2*last-1] = X_VIA;
            Line l = new Line(0, gL, g, gL);
            l.setLayoutX(nBits * g);
            drawArea.getChildren().add(l);
        }

        // B0, Ln
        if (isSet(side[B], bitL) && isSet(side[L], bitH) /*&& doMaybe(probability)*/) {
            for (int i = 0; i < bitL + 1; i++) {
                matrix[2*bitH][2*i] = X_CON;
                matrix[2*(last - i)][2*bitL] = X_CON;
                if ( i > 0 ) {
                    matrix[2*bitH][2*i-1] = X_CON;
                }
                if ( i < bitL) matrix[2*(last - i)-1][2*bitL] = X_CON;

            }
            Polyline p = new Polyline(
                    0, 0,
                    0.5 * gL, 0,
                    gL, 0.5 * gL,
                    gL, gL
            );
            //p.setLayoutX( g );
            p.setLayoutY(gH);
            drawArea.getChildren().add(p);
        } else if (isSet(side[B], bitL)) { // Stub
            matrix[2*(nBits - 1)][2*bitL] = X_STUB;
            matrix[2*(nBits - 1)][2*bitL+1] = X_CON;
            Line l = new Line(gL, 0, gL, g);
            //l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
        } else if (isSet(side[L], bitH)) { // Stub
            matrix[2*bitH][0] = X_STUB;
            matrix[2*bitH][1] = X_CON;
            Line l = new Line(0, 0, g, 0);
            //l.setLayoutX(gH);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
        }

        // Bn, Rn
        if (isSet(side[B], bitH) && isSet(side[R], bitH) /*&& doMaybe(probability)*/) {
            for (int i = 0; i < bitL + 1; i++) {
                matrix[2*bitH][2*(last - i)] = X_CON;
                matrix[2*(last - i)][2*bitH] = X_CON;
                if ( i < bitL ) {
                    matrix[2*bitH][2*(last - i)-1] = X_CON;
                    matrix[2*(last - i)-1][2*bitH] = X_CON;                    
                }
            }
            //matrix[bitH][bitH] = X_CON;
            Polyline p = new Polyline(
                    0, (bitL + 1) * g,
                    0, 0.5 * (bitL + 1) * g,
                    0.5 * (bitL + 1) * g, 0,
                    (bitL + 1) * g, 0
            );
            p.setLayoutX(gH);
            p.setLayoutY(gH);
            drawArea.getChildren().add(p);
        } else if (isSet(side[B], bitH)) { // Stub
            matrix[2*(nBits - 1)][2*bitH] = X_STUB;
            matrix[2*(nBits - 1)][2*bitH-1] = X_CON;
            Line l = new Line(0, 0, 0, g);
            l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
        } else if (isSet(side[R], bitH)) { // Stub
            matrix[2*bitH][2*(nBits - 1)] = X_STUB;
            matrix[2*bitH-1][2*(nBits - 1)] = X_CON;
            Line l = new Line(0, 0, g, 0);
            l.setLayoutX(nBits * g);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
        }
    }

    private void genStub(int bitL) {
        double g = grid;
        int bitH = nBits - 1 - bitL;
        double gL = (bitL + 1) * g;
        double gH = (bitH + 1) * g;

        // Top
        if (isSet(side[T], bitL)) {  // Stub
            matrix[0][2*bitL] = X_STUB;
            Line l = new Line(gL, 0, gL, g);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[T], bitH)) { // Stub
            matrix[0][2*bitH] = X_STUB;
            Line l = new Line(0, 0, 0, g);
            l.setLayoutX(gH);
            drawArea.getChildren().add(l);
        }

        // Right
        if (isSet(side[R], bitL)) { // Stub
            matrix[2*bitL][2*(nBits - 1)] = X_STUB;
            Line l = new Line(0, gL, g, gL);
            l.setLayoutX(nBits * g);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[R], bitH)) { // Stub
            matrix[2*bitH][2*(nBits - 1)] = X_STUB;
            Line l = new Line(0, 0, g, 0);
            l.setLayoutX(nBits * g);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
        }

        // Bottom
        if (isSet(side[B], bitL)) { // Stub
            matrix[2*(nBits - 1)][2*bitL] = X_STUB;
            Line l = new Line(gL, 0, gL, g);
            //l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[B], bitH)) { // Stub
            matrix[2*(nBits - 1)][2*bitH] = X_STUB;
            Line l = new Line(0, 0, 0, g);
            l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
        }

        // Left
        if (isSet(side[L], bitL)) {  // Stub
            matrix[2*bitL][0] = X_STUB;
            Line l = new Line(0, gL, g, gL);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[L], bitH)) { // Stub
            matrix[2*bitH][0] = X_STUB;
            Line l = new Line(0, 0, g, 0);
            //l.setLayoutX(gH);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
        }

    }

    private boolean isSet(int edge, int bit) {
        return (edge & (1 << bit)) > 0;
    }

    private void clearMatrix() {
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                matrix[y][x] = ' ';
            }
        }
        for (int y = 0; y < matrix.length-1; y++) {
            for (int x = 0; x < matrix[y].length-1; x++) {
                matrix[y][x] = X_NONE;
            }
        }
    }

    private class Edge {

        public int side;
        public int bit;

        public Edge(int side, int bit) {
            this.side = side;
            this.bit = bit;
        }
    }

    private class Coord {

        public int x;
        public int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
