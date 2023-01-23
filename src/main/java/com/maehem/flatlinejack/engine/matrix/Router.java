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
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * A tool for generating site floor circuit patterns.
 *
 * @author mark
 */
public class Router extends Pane {

    private final Group drawArea = new Group();
    private final int nBits;

    private static final double CORNER_TRIM = 0.707;

    private final static int T = 0;
    private final static int R = 1;
    private final static int B = 2;
    private final static int L = 3;

    private int side[] = {0, 0, 0, 0};

    private final double grid;
    private final char[][] matrix; // .= none, s=stub, v=via, c=connected
    private final char X_NONE = '.';
    private final char X_CON = 'C';
    private final char X_VIA = 'V';
    private final char X_STUB = 'S';
    private final Color traceColor;
    private final double traceWidth;
    private final Color viaPadColor;
    private final double viaPadRadius;
    private final Color viaHoleColor;
    private final double viaHoleRadius;

    public Router(int nBits, double size,
            Color traceColor, double traceWidth,
            Color viaPadColor, double viaPadRadius,
            Color viaHoleColor, double viaHoleRadius
    ) {
        this.nBits = nBits;
        this.traceColor = traceColor;
        this.traceWidth = traceWidth;
        this.viaPadColor = viaPadColor;
        this.viaPadRadius = viaPadRadius;
        this.viaHoleColor = viaHoleColor;
        this.viaHoleRadius = viaHoleRadius;

        this.matrix = new char[2 * nBits][2 * nBits];
        clearMatrix();

        setPrefSize(size, size);
        getChildren().add(drawArea);

        setClip(new Rectangle(size, size));

//        setBorder(new Border(new BorderStroke(Color.RED,
//                BorderStrokeStyle.SOLID,
//                CornerRadii.EMPTY,
//                new BorderWidths(5)
//        )));
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

        //dumpMatrix();
        // Stub remaining pins
        for (int i = 3; i < (nBits + 1) / 2; i++) {
            genStub(i);
        }

        //dumpMatrix();
        //int nH = nBits*2;
        //build a stub list (side, pin)
        ArrayList<Edge> stubList = new ArrayList<>();
        for (int b = 1; b < nBits - 1; b++) {
            if (matrix[0][2 * b] == X_STUB) {
                stubList.add(new Edge(T, b));
            }
            if (matrix[2 * b][2 * (nBits - 1)] == X_STUB) {
                stubList.add(new Edge(R, b));
            }
            if (matrix[2 * (nBits - 1)][2 * b] == X_STUB) {
                stubList.add(new Edge(B, b));
            }
            if (matrix[2 * b][0] == X_STUB) {
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

        //LOGGER.log(INFO, "All routed!");
        // genCornerVias
        //genCornerVias();
    }

    private void route(Edge e) {
        //LOGGER.log(INFO, "Route: e={0}   bit={1}", new Object[]{e.side, e.bit});
        int dir = 0;
        if (e.bit < nBits / 2) {
            dir = 1;
        } else if (e.bit > (nBits / 2)) {
            dir = -1;
        }
        //LOGGER.log(INFO, "Dir:: bit: " + e.bit + "  direction:" + dir);
        switch (e.side) {
            case T:
                routeTop(e.bit, dir);
                break;
            case R:
                routeRight(e.bit, dir);
                break;
            case B:
                routeBottom(e.bit, dir);
                break;
            case L:
                routeLeft(e.bit, dir);
                break;
        }
    }

    private void routeTop(int bit, int dir) {
        Coord root = new Coord(bit, 0);
        matrix[2 * root.y][2 * root.x] = X_CON;
        for (int y = 1; y < ((nBits + 1) / 2); y++) {
            ArrayList<Coord> choices = new ArrayList<>();
            if (matrix[2 * (root.y + 1)][2 * root.x] == X_NONE) {
                choices.add(new Coord(root.x, root.y + 1));
            }
            if (matrix[2 * (root.y + 1)][2 * (root.x + dir)] == X_NONE
                    && matrix[2 * root.y + 1][2 * root.x + dir] == X_NONE) { // Check for crossing line
                choices.add(new Coord(root.x + dir, root.y + 1));
            }
            if (choices.isEmpty()) {
                break; // end of the line
            } else {
                Coord c = choices.get((int) (Math.random() * choices.size()));
                if ((root.x - c.x) != 0) {
                    matrix[2 * root.y + 1][2 * root.x + dir] = X_CON; // diag
                } else {
                    matrix[2 * root.y + 1][2 * root.x] = X_CON;  // straight                        
                }
                matrix[2 * c.y][2 * c.x] = X_CON;
                Node l = genTrace(root.x * grid, root.y * grid, c.x * grid, c.y * grid);
                l.setLayoutY(grid);
                l.setLayoutX(grid);
                drawArea.getChildren().add(l);
                root = c;
            }
        }
        //dumpMatrix();
        matrix[2 * root.y][2 * root.x] = X_VIA;
        addVia(grid + grid * root.x, grid + grid * root.y);
    }

    private void routeRight(int bit, int dir) {
        Coord root = new Coord(nBits - 1, bit);
        matrix[2 * root.y][2 * root.x] = X_CON;
        //for (int y = 1; y < ((nBits + 1) / 2); y++) {
        for (int x = nBits - 1; x > ((nBits + 1) / 2) - 1; x--) {
            ArrayList<Coord> choices = new ArrayList<>();
            if (matrix[2 * root.y][2 * (root.x - 1)] == X_NONE) {
                choices.add(new Coord(root.x - 1, root.y));
            }
            if (matrix[2 * (root.y + dir)][2 * (root.x - 1)] == X_NONE
                    && matrix[2 * (root.y + dir) - dir][2 * (root.x - 1) - 1] == X_NONE) { // Check for crossing line
                choices.add(new Coord(root.x - 1, root.y + dir));
            }

            if (choices.isEmpty()) {
                break; // end of the line
            } else {
                Coord c = choices.get((int) (Math.random() * choices.size()));

                if ((root.y - c.y) != 0) {
                    matrix[2 * root.y + dir][2 * root.x - 1] = X_CON; // diag
                } else {
                    matrix[2 * root.y][2 * root.x - 1] = X_CON;  // straight                        
                }

                matrix[2 * c.y][2 * c.x] = X_CON;

                Node l = genTrace(root.x * grid, root.y * grid, c.x * grid, c.y * grid);

                l.setLayoutY(grid);
                l.setLayoutX(grid);
                drawArea.getChildren().add(l);
                root = c;
            }
        }
        //dumpMatrix();
        matrix[2 * root.y][2 * root.x] = X_VIA;
        addVia(grid + grid * root.x, grid + grid * root.y);
    }

    private void routeBottom(int bit, int dir) {
        Coord root = new Coord(bit, nBits - 1);

        matrix[2 * root.y][2 * root.x] = 'R';

        for (int y = nBits - 1; y > ((nBits + 1) / 2) - 1; y--) {
            ArrayList<Coord> choices = new ArrayList<>();
            if (matrix[2 * (root.y - 1)][2 * root.x] == X_NONE) {
                choices.add(new Coord(root.x, root.y - 1));
            }
            if (matrix[2 * (root.y - 1)][2 * (root.x + dir)] == X_NONE
                    && matrix[2 * (root.y - 1) + 1][2 * (root.x + dir) - dir] == X_NONE) { // Check for crossing line
                choices.add(new Coord(root.x + dir, root.y - 1));
            }
            if (choices.isEmpty()) {
                break; // end of the line
            } else {
                Coord c = choices.get((int) (Math.random() * choices.size()));
                if ((root.x - c.x) != 0) {
                    matrix[2 * root.y - 1][2 * root.x + dir] = 'x'; // diag
                } else {
                    matrix[2 * root.y - 1][2 * root.x] = 's';  // straight                        
                }
                matrix[2 * c.y][2 * c.x] = 'c';
                Node l = genTrace(root.x * grid, root.y * grid, c.x * grid, c.y * grid);
                l.setLayoutY(grid);
                l.setLayoutX(grid);
                drawArea.getChildren().add(l);
                root = c;
            }
        }
        //dumpMatrix();
        matrix[2 * root.y][2 * root.x] = X_VIA;
        addVia(grid + grid * root.x, grid + grid * root.y);
    }

    private void routeLeft(int bit, int dir) {
        Coord root = new Coord(0, bit);
        matrix[2 * root.y][2 * root.x] = X_CON;
        //for (int y = 1; y < ((nBits + 1) / 2); y++) {
        for (int x = 1; x < ((nBits + 1) / 2); x++) {
            ArrayList<Coord> choices = new ArrayList<>();
            if (matrix[2 * root.y][2 * (root.x + 1)] == X_NONE) {
                choices.add(new Coord(root.x + 1, root.y));
            }
            if (matrix[2 * (root.y + dir)][2 * (root.x + 1)] == X_NONE
                    && matrix[2 * (root.y + dir) - dir][2 * (root.x + 1) - 1] == X_NONE) { // Check for crossing line
                choices.add(new Coord(root.x + 1, root.y + dir));
            }

            if (choices.isEmpty()) {
                break; // end of the line
            } else {
                Coord c = choices.get((int) (Math.random() * choices.size()));

                if ((root.y - c.y) != 0) {
                    matrix[2 * root.y + dir][2 * root.x + 1] = X_CON; // diag
                } else {
                    matrix[2 * root.y][2 * root.x + 1] = X_CON;  // straight                        
                }

                matrix[2 * c.y][2 * c.x] = X_CON;

                Node l = genTrace(root.x * grid, root.y * grid, c.x * grid, c.y * grid);

                l.setLayoutY(grid);
                l.setLayoutX(grid);
                drawArea.getChildren().add(l);
                root = c;
            }
        }
        //dumpMatrix();
        matrix[2 * root.y][2 * root.x] = X_VIA;
        addVia(grid + grid * root.x, grid + grid * root.y);
    }

    private boolean checkIntersect(Bounds b) {
        for (Node n : drawArea.getChildren()) {
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
        Circle c = new Circle(viaPadRadius, viaPadColor);
        c.setLayoutX(x);
        c.setLayoutY(y);

        Circle c2 = new Circle(viaHoleRadius, viaHoleColor);
        c2.setLayoutX(x);
        c2.setLayoutY(y);

        drawArea.getChildren().addAll(c, c2);
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
                matrix[bitL * 2][i * 2] = X_CON;
                matrix[2 * i][bitL * 2] = X_CON;
                if (i > 0) {
                    matrix[2 * i - 1][bitL * 2] = X_CON;
                    matrix[bitL * 2][i * 2 - 1] = X_CON;
                }
            }
            //matrix[bitL][bitL] = X_CON;
            //Line l = new Line( 0.5*gL,   gL, gL,    0.5*gL );
            Node p = genPolyLine(
                    0, gL,
                    CORNER_TRIM * gL, gL,
                    gL, CORNER_TRIM * gL,
                    gL, 0
            );
            //p.setLayoutX( g );
            drawArea.getChildren().add(p);
        } else if (isSet(side[T], bitL)) {  // Stub
            matrix[0][bitL * 2] = X_STUB;
            matrix[1][bitL * 2] = X_VIA;
            Node l = genTrace(gL, 0, gL, g);
            drawArea.getChildren().add(l);
            if (bitL == 0) {
                addVia(gL, gL);
            }
        } else if (isSet(side[L], bitL)) {  // Stub
            matrix[bitL * 2][0] = X_STUB;
            matrix[bitL * 2][1] = X_CON;
            Node l = genTrace(0, gL, g, gL);
            drawArea.getChildren().add(l);
            if (bitL == 0) {
                addVia(gL, gL);
            }
        }

        // Tn-2, R1
        if (isSet(side[T], bitH) && isSet(side[R], bitL) /*&& doMaybe(probability)*/) {
            for (int i = 0; i < bitL + 1; i++) {
                matrix[2 * bitL][2 * (last - i)] = X_CON;
                if (i < bitL) {
                    matrix[2 * bitL][2 * (last - i) - 1] = X_CON;
                }
                matrix[2 * i][2 * bitH] = X_CON;
                if (i > 0) {
                    matrix[2 * i - 1][2 * bitH] = X_CON;
                }
            }
            //matrix[bitL][bitH] = X_CON;
            Node p = genPolyLine(
                    0, 0,
                    0, CORNER_TRIM * gL,
                    (1.0 - CORNER_TRIM) * gL, gL,
                    gL, gL
            );
            p.setLayoutX(gH);
            drawArea.getChildren().add(p);
        } else if (isSet(side[T], bitH)) { // Stub
            matrix[0][2 * bitH] = X_STUB;
            matrix[1][2 * bitH] = X_CON;
            Node l = genTrace(0, 0, 0, g);
            l.setLayoutX(gH);
            drawArea.getChildren().add(l);
            if (bitH == last) {
                addVia(gH, gL);
            }
        } else if (isSet(side[R], bitL)) { // Stub
            matrix[2 * bitL][2 * last] = X_STUB;
            matrix[2 * bitL][2 * last - 1] = X_VIA;
            Node l = genTrace(0, gL, g, gL);
            l.setLayoutX(nBits * g);
            drawArea.getChildren().add(l);
            if (bitH == last) {
                addVia(gH, gL);
            }
        }

        // B0, Ln
        if (isSet(side[B], bitL) && isSet(side[L], bitH) /*&& doMaybe(probability)*/) {
            for (int i = 0; i < bitL + 1; i++) {
                matrix[2 * bitH][2 * i] = X_CON;
                matrix[2 * (last - i)][2 * bitL] = X_CON;
                if (i > 0) {
                    matrix[2 * bitH][2 * i - 1] = X_CON;
                }
                if (i < bitL) {
                    matrix[2 * (last - i) - 1][2 * bitL] = X_CON;
                }

            }
            Node p = genPolyLine(
                    0, 0,
                    CORNER_TRIM * gL, 0,
                    gL, (1.0 - CORNER_TRIM) * gL,
                    gL, gL
            );
            //p.setLayoutX( g );
            p.setLayoutY(gH);
            drawArea.getChildren().add(p);
        } else if (isSet(side[B], bitL)) { // Stub
            matrix[2 * (nBits - 1)][2 * bitL] = X_STUB;
            //matrix[2*(nBits - 1)-1][2*bitL] = X_CON;
            Node l = genTrace(gL, 0, gL, g);
            //l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
            if (bitH == last) {
                addVia(gL, gH);
            }
        } else if (isSet(side[L], bitH)) { // Stub
            matrix[2 * bitH][0] = X_STUB;
            matrix[2 * bitH][1] = X_CON;
            Node l = genTrace(0, 0, g, 0);
            //l.setLayoutX(gH);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
            if (bitH == last) {
                addVia(gL, gH);
            }
        }

        // Bn, Rn
        if (isSet(side[B], bitH) && isSet(side[R], bitH) /*&& doMaybe(probability)*/) {
            for (int i = 0; i < bitL + 1; i++) {
                matrix[2 * bitH][2 * (last - i)] = X_CON;
                matrix[2 * (last - i)][2 * bitH] = X_CON;
                if (i < bitL) {
                    matrix[2 * bitH][2 * (last - i) - 1] = X_CON;
                    matrix[2 * (last - i) - 1][2 * bitH] = X_CON;
                }
            }
            //matrix[bitH][bitH] = X_CON;
            Node p = genPolyLine(
                    0, (bitL + 1) * g,
                    0, (1.0 - CORNER_TRIM) * (bitL + 1) * g,
                    (1.0 - CORNER_TRIM) * (bitL + 1) * g, 0,
                    (bitL + 1) * g, 0
            );
            p.setLayoutX(gH);
            p.setLayoutY(gH);
            drawArea.getChildren().add(p);
        } else if (isSet(side[B], bitH)) { // Stub
            matrix[2 * (nBits - 1)][2 * bitH] = X_STUB;
            //matrix[2*(nBits - 1)-1][2*bitH] = X_CON;
            Node l = genTrace(0, 0, 0, g);
            l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
            if (bitH == last) {
                addVia(gH, gH);
            }
        } else if (isSet(side[R], bitH)) { // Stub
            matrix[2 * bitH][2 * (nBits - 1)] = X_STUB;
            matrix[2 * bitH - 1][2 * (nBits - 1)] = X_CON;
            Node l = genTrace(0, 0, g, 0);
            l.setLayoutX(nBits * g);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
            if (bitH == last) {
                addVia(gH, gH);
            }
        }
    }

    private void genStub(int bitL) {
        double g = grid;
        int bitH = nBits - 1 - bitL;
        double gL = (bitL + 1) * g;
        double gH = (bitH + 1) * g;

        // Top
        if (isSet(side[T], bitL)) {  // Stub
            matrix[0][2 * bitL] = X_STUB;
            Node l = genTrace(gL, 0, gL, g);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[T], bitH)) { // Stub
            matrix[0][2 * bitH] = X_STUB;
            Node l = genTrace(0, 0, 0, g);
            l.setLayoutX(gH);
            drawArea.getChildren().add(l);
        }

        // Right
        if (isSet(side[R], bitL)) { // Stub
            matrix[2 * bitL][2 * (nBits - 1)] = X_STUB;
            Node l = genTrace(0, gL, g, gL);
            l.setLayoutX(nBits * g);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[R], bitH)) { // Stub
            matrix[2 * bitH][2 * (nBits - 1)] = X_STUB;
            Node l = genTrace(0, 0, g, 0);
            l.setLayoutX(nBits * g);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
        }

        // Bottom
        if (isSet(side[B], bitL)) { // Stub
            matrix[2 * (nBits - 1)][2 * bitL] = X_STUB;
            Node l = genTrace(gL, 0, gL, g);
            //l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[B], bitH)) { // Stub
            matrix[2 * (nBits - 1)][2 * bitH] = X_STUB;
            Node l = genTrace(0, 0, 0, g);
            l.setLayoutX(gH);
            l.setLayoutY(nBits * g);
            drawArea.getChildren().add(l);
        }

        // Left
        if (isSet(side[L], bitL)) {  // Stub
            matrix[2 * bitL][0] = X_STUB;
            Node l = genTrace(0, gL, g, gL);
            drawArea.getChildren().add(l);
        }
        if (isSet(side[L], bitH)) { // Stub
            matrix[2 * bitH][0] = X_STUB;
            //Line l = new Line(0, 0, g, 0);
            Node l = genTrace(0, 0, g, 0);
            //l.setLayoutX(gH);
            l.setLayoutY(gH);
            drawArea.getChildren().add(l);
        }

    }

    private Node genTrace(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(traceColor);
        l.setStrokeWidth(traceWidth);
        l.setStrokeLineCap(StrokeLineCap.ROUND);
        return l;
    }

    private Node genPolyLine(double... doubles) {
        Polyline p = new Polyline(doubles);
        p.setStroke(traceColor);
        p.setStrokeWidth(traceWidth);

        return p;
    }
//    private Node genVia( double x, double y ) {
//        Circle c = new Circle(viaSize, viaColor);
//        
//        return c;
//    }

    private boolean isSet(int edge, int bit) {
        return (edge & (1 << bit)) > 0;
    }

    private void clearMatrix() {
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                matrix[y][x] = ' ';
            }
        }
        for (int y = 0; y < matrix.length - 1; y++) {
            for (int x = 0; x < matrix[y].length - 1; x++) {
                matrix[y][x] = X_NONE;
            }
        }
    }

    public Image snap() {
        final SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        WritableImage snapshot = new WritableImage((int)getPrefWidth(), (int)getPrefHeight());
        snapshot = this.snapshot(sp, snapshot);
        return snapshot;
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
