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
import java.util.logging.Level;

/**
 * Holds state of all edges for a zone
 *
 * @author mark
 */
public class EdgeMap {

    public enum Edge { TOP, RIGHT, BOTTOM, LEFT }
    
    private final int[][] cell;  // int == T L  T = High L = Low
    private final int w;
    private final int h;

    public EdgeMap(int rows, int cols) {
        this.cell = new int[rows+1][cols+1]; //  T L shorts 
        
        this.w = cols;
        this.h = rows;

        initMap();
    }
    
    public void setEdge( int r, int c, Edge e, int val ) {
        int temp = cell[r][c];
        
        switch ( e ) {
            case TOP:
                temp &= 0x0000FFFF;
                temp |= val<<16;
                cell[r][c] = temp;
                break;
            case RIGHT:
                temp &= 0xFFFF0000;
                temp |= val;
                cell[r][c+1] = temp;
                break;
            case BOTTOM:
                temp &= 0x0000FFFF;
                temp |= val<<16;
                cell[r+1][c] = temp;
                break;
            case LEFT:
                temp &= 0xFFFF0000;
                temp |= val;
                cell[r][c] = temp;
                break;
        }
    }

    public int getEdge( Edge e, int r, int c ) {
        switch ( e ) {
            case TOP:
                return (cell[r][c]&0xFFFF0000)>>16;
            case RIGHT:
                return cell[r][c+1]&0xFFFF;
            case BOTTOM:
                return (cell[r+1][c]&0xFFFF0000)>>16;
            case LEFT:
                return cell[r][c]&0xFFFF;
        }
        return 0;
    }
    
//    public void setEdge( int r, int c, Edge e, int val ) {
//        long ll = val & 0xFFFFl;
//        long temp = 0l;
//        switch ( e ) {
//            case TOP:
//                temp = siteEdges[r][c] & ( ~ 0x000000000000FFFFl ); // clear current bit range
//                siteEdges[r][c] = temp & ( ll << 0 );  // set the new value.                
//                break;
//            case RIGHT:
//                temp = siteEdges[r][c] & ( ~ 0x00000000FFFF0000l ); // clear current bit range
//                siteEdges[r][c] = temp & ( ll << 16 );  // set the new value.                
//                break;
//            case BOTTOM:
//                temp = siteEdges[r][c] & ( ~ 0x0000FFFF00000000l ); // clear current bit range
//                siteEdges[r][c] = temp & ( ll << 32 );  // set the new value.                
//                break;
//            case LEFT:
//                temp = siteEdges[r][c] & ( ~ 0xFFFF000000000000l ); // clear current bit range
//                siteEdges[r][c] = temp & ( ll << 48 );  // set the new value.                
//                break;
//        }
//    }

    private void initMap() {
        //long startTime = System.nanoTime();

        // Zero Evertything
        for (int r = 0; r <= h; r++) {
            for (int c = 0; c <= w; c++) {
                cell[r][c] = 0;
            }
        }
        // Sparse 1
        for (int r = 1; r < h; r++) {
            for (int c = 1; c < w; c++) {
                cell[r][c] = sparseShort(4, 3) | (sparseShort(4, 3)<<16);
            }
        }
        // Sparse 2
        for (int r = 5; r < h-6; r++) {
            for (int c = 5; c < w-6; c++) {
                cell[r][c] = sparseShort(8, 4) | (sparseShort(8, 4)<<16);
            }
        }
        
 
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime);
        //LOGGER.log(Level.INFO, "Init Edge map took: {0}mS", duration/1000000);
    }

    /**
     * Fill a long with random n-bits and +/- per 16 bits.
     *
     * @param bits
     * @return sparse long
     */
    static public int sparseShort(int bits, int variance) {
        //int l = 0;
        //LOGGER.log(Level.INFO, "Make a sparse-long with {0} bits and +/-{1}", new Object[]{bits, variance});
        if (bits > 12) {
            bits = 12;
        }
        if (variance > 4) {
            variance = 4;
        }

            int n = (bits - variance) + (int) (Math.random() * 2.0 * variance);
            int edge = 0;
            int bit = (int) (Math.random() * 16);  // 0-15
            //        LOGGER.log(Level.INFO, "Bit: {0}", bit);
            while (n > 0) {
                //LOGGER.log(Level.INFO, "masked edge: " + (edge & (1 << bit)));
                if ((edge & (1 << bit)) == 0) {
                    edge |= (1 << bit);
                    n--;
                }
                bit = (int) (Math.random() * 16);  // 0-15
            }
//            LOGGER.log(Level.INFO, "edge {0} = {1}", 
//                    new Object[]{
//                        chunk, 
//                        String.format("%064d", new BigInteger(Long.toBinaryString(edge)))
//                    }
//            );
//    //        LOGGER.log(Level.INFO, "Gonna OR this: " + String.format("%064d", new BigInteger(Long.toBinaryString((edge<<(16*chunk))))));
            //l |= (edge << (16 * chunk));
            //        LOGGER.log(Level.INFO, "       Result: " + String.format("%064d", new BigInteger(Long.toBinaryString(l))));
        return edge;
    }

}
