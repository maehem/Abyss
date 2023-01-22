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
import com.maehem.flatlinejack.engine.EdgeMap.Edge;
import static com.maehem.flatlinejack.engine.MatrixSiteNeighbor.N;
import java.util.logging.Level;

/**
 * Holds information and game state for a Matrix location.
 * 
 * @author mark
 */
public class MatrixSite {
    
    private final GameState gameState;
    //private final int address; // Site Address Z:YY:XX
    private final int addrCol;  // Col
    private final int addrRow;  // Row
    private final int zone;  // 0=bottom(starting),  5=top
    
    private final int nBits = 16; // traces per edge
    private int topBits = 0;
    private int rightBits = 0;
    private int bottomBits = 0;
    private int leftBits = 0;
    

    public MatrixSite( GameState gs, int zone, int row, int col) {
        //this.address = (zone&0xF) << 16 | (col&0xFF) << 8 | (row&0xFF);
        this.gameState = gs;
        this.addrCol = col;
        this.addrRow = row;
        this.zone = zone;
        
        EdgeMap map = gs.getMatrixMap();
        this.topBits = map.getEdge(EdgeMap.Edge.TOP, row, col);
        this.rightBits = map.getEdge(EdgeMap.Edge.RIGHT, row, col);
        this.bottomBits = map.getEdge(EdgeMap.Edge.BOTTOM, row, col);
        this.leftBits = map.getEdge(EdgeMap.Edge.LEFT, row, col);
        
        LOGGER.log(Level.INFO, "Created new Site: {0}", getAddress());
    }
    
    public MatrixSite( GameState gs, int addr ) {
        this(gs, decodeZone(addr), decodeRow(addr), decodeCol(addr));
    }
    
    //  Level:X:Y  ==>  F:FF:FF
    // Recovery time (after cracking)  
    //      ROM remembers best warez to use if player needs to return.
    
    // Terminal Site (after cracking)
    // Data state of the site.
    // Health
    // Shield
    // Path to Base Shape
    
    
    // Attack Warez
    // Defense Warez
            
    // isOpen()  can be accessed immediatly
    
    // load()
    
    // save()
    
    public final int getEdgeBits() {
        return nBits;
    }
    
    public final int getTopBits() {
        return topBits;
    }
    
    public final int getRightBits() {
        return rightBits;
    }
    
    public final int getBottomBits() {
        return bottomBits;
    }
    
    public final int getLeftBits() {
        return leftBits;
    }
    
    public final String getAddress() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("%01X", zone))
                .append(":").append(String.format("%02X", addrRow))
                .append(":").append(String.format("%02X", addrCol));
        
        return sb.toString();
    }
    
    public static int getIntAddressFor( int z, int r, int c ) {
        return (z&0xF) << 16 | (r&0xFF) << 8 | (c&0xFF);
    }
    
    /**
     * Return address as an int Z ROW COL (used for index in arrays)
     * 
     * @return 
     */
    public int getIntAddress() {
        return getIntAddressFor(zone, addrRow, addrCol);
        //return (zone&0xF) << 16 | (addrRow&0xFF) << 8 | (addrCol&0xFF);
    }
    
    public int getNeighbor( MatrixSiteNeighbor e ) {
        return getIntAddressFor(zone, addrRow+e.row, addrCol+e.col );
    }
    
    public static final int decodeZone( int addr ) {
        return addr>>16 &0xF;
    }
    
    public static final int decodeRow( int addr ) {
        return addr>>8 &0xFF;
    }
    
    public static final int decodeCol( int addr ) {
        return addr &0xFF;
    }
    
//    public static String toHex( int address ) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("%01X", (address&0xF0000)>>16))
//                .append(":")
//                .append(String.format("%02X", (address&0xFF00)>>8))
//                .append(":")
//                .append(String.format("%02X", (address&0xFF)));
//        
//        return sb.toString();
//    }
}
