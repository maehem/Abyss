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
package com.maehem.abyss.engine.matrix;

import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.SoftwareThing;
import com.maehem.abyss.engine.EdgeMap;
import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.SoftwareUser;
import com.maehem.abyss.engine.bbs.BBSTerminal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Holds information and game state for a Matrix location.
 * 
 * @author mark
 */
public class MatrixSite implements SoftwareUser {
    
    private static final int EDGE_BITS = 16; // traces per edge

    private final GameState gameState;

    private final int addrCol;  // Col
    private final int addrRow;  // Row
    private final int zone;  // 0=bottom(starting),  5=top

    //private final String nodeName; // Change to Class<? extends MatrixNode>
    private final Class<? extends MatrixNode> nodeClass;
    private final String nodeProperties; // A string with comma separated properties. "foo=12345,bar=aa:bb:cc:dd"
    
    private final ArrayList<Shield> shields = new ArrayList<>();
    
    // Add to this list for tools to attack player with.
    // Usually added by MatrixNode at init().
    private final ArrayList<SoftwareThing> attackTools = new ArrayList<>();
    
    private Class<? extends BBSTerminal> terminal = null;
    
    private int topBits = 0;
    private int rightBits = 0;
    private int bottomBits = 0;
    private int leftBits = 0;
    
    private boolean terminalAvailable = false;
    

    public MatrixSite( GameState gs, int zone, int row, int col, Class<? extends MatrixNode> nodeClass, String nodeProperties) {
        this.gameState = gs;
        this.addrCol = col;
        this.addrRow = row;
        this.zone = zone;
        this.nodeClass = nodeClass;
        this.nodeProperties = nodeProperties;
        
        EdgeMap map = gs.getMatrixMap();
        this.topBits = map.getEdge(EdgeMap.Edge.TOP, row, col);
        this.rightBits = map.getEdge(EdgeMap.Edge.RIGHT, row, col);
        this.bottomBits = map.getEdge(EdgeMap.Edge.BOTTOM, row, col);
        this.leftBits = map.getEdge(EdgeMap.Edge.LEFT, row, col);
        
        LOGGER.log(Level.FINER, "Created new Site: {0}", getAddress());
    }
    
    public MatrixSite( GameState gs, int addr, Class<? extends MatrixNode> nodeClass, String nodeProperties ) {
        this(gs, decodeZone(addr), decodeRow(addr), decodeCol(addr), nodeClass, nodeProperties);
    }
    
    public MatrixSite( GameState gs, int addr) {
        this(gs, addr, null, "");
    }
    
    //  Level:X:Y  ==>  F:FF:FF
    // Recovery time (after cracking)  
    //      ROM remembers best warez to use if player needs to return.
    
    
    // Terminal Site (after cracking)
    public boolean terminalAvailable() {
        return terminalAvailable;
    }
    
    public void setTerminalAvailable( boolean avail ) {
        this.terminalAvailable = avail;
    }
    
    public Class<? extends BBSTerminal> getTerminal() {
        return terminal;
    }
    
    public void setTerminal( Class<? extends BBSTerminal> t ) {
        terminal = t;
    }
    
    // Data state of the site.
    
    // Shield
    public List<Shield> getShields() {
        return shields;
    }
    
    public int getShieldValue() {
        int val = 0;
        for ( Shield s: shields ) {
            val += s.getCondition();
        }
        
        return val;
    }
    
    
    // Attack Warez
    public List<SoftwareThing> getAttackTools() {
        return attackTools;
    }
    
    public boolean isAttackable() {
        LOGGER.log(Level.INFO, "Site is attackable: {0}", getAttackTools().isEmpty()?"no":"yes");
        return !getAttackTools().isEmpty();
    }
                
    // load()
    
    // save()
    
    public final int getRow() {
        return addrRow;
    }
    
    public final int getCol() {
        return addrCol;
    }
    
    public final int getEdgeBits() {
        return EDGE_BITS;
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
                .append(".").append(String.format("%02X", addrRow))
                .append(".").append(String.format("%02X", addrCol));
        
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
    
    /**
     * Name of MatrixNode class to load and attach to node.
     * 
     * @return 
     */
    public Class<? extends MatrixNode> getNodeClass() {
        return nodeClass;
    }
    
    public String getNodeProperties() {
        return nodeProperties;
    }
    
    @Override
    public void attack(SoftwareUser enemy, SoftwareThing tool) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
