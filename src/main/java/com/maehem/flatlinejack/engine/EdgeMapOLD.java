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
import java.util.logging.Level;

/**
 * Holds state of all edges for a zone
 *
 * @author mark
 */
public class EdgeMapOLD {

    public enum Edge { TOP, RIGHT, BOTTOM, LEFT }
    
    private final long[][] siteEdges;  // Long == T R B L 4xints
    private int w;
    private int h;

    public EdgeMapOLD(int rows, int cols) {
        this.siteEdges = new long[rows][cols]; // Long T R B L ints
        this.w = cols;
        this.h = rows;

        initMap();
    }
    
    public void setEdge( int r, int c, Edge e, int val ) {
        long ll = val & 0xFFFFl;
        long temp = 0l;
        switch ( e ) {
            case TOP:
                temp = siteEdges[r][c] & ( ~ 0x000000000000FFFFl ); // clear current bit range
                siteEdges[r][c] = temp & ( ll << 0 );  // set the new value.                
                break;
            case RIGHT:
                temp = siteEdges[r][c] & ( ~ 0x00000000FFFF0000l ); // clear current bit range
                siteEdges[r][c] = temp & ( ll << 16 );  // set the new value.                
                break;
            case BOTTOM:
                temp = siteEdges[r][c] & ( ~ 0x0000FFFF00000000l ); // clear current bit range
                siteEdges[r][c] = temp & ( ll << 32 );  // set the new value.                
                break;
            case LEFT:
                temp = siteEdges[r][c] & ( ~ 0xFFFF000000000000l ); // clear current bit range
                siteEdges[r][c] = temp & ( ll << 48 );  // set the new value.                
                break;
        }
    }

    private void initMap() {
        long startTime = System.nanoTime();

        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                // If first or last, no edge bits.
                if (c == 0 || r == 0 || c == w - 1 || r == h - 1) {
                    siteEdges[r][c] = 0;
                } else {
                    if (c == 1 || r == 1 || c == w - 2 || r == h - 2) {
                        // Very sparse
                        siteEdges[r][c] = sparseLong(4, 3);
                        // Todo: match neighbor edge.
                    } else {
                        // Normal sparse
                        siteEdges[r][c] = sparseLong(8, 4);
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        LOGGER.log(Level.INFO, "Init Edge map took: {0}mS", duration/1000000);
    }

    /**
     * Fill a long with random n-bits and +/- per 16 bits.
     *
     * @param bits
     * @return sparse long
     */
    static public long sparseLong(int bits, int variance) {
        long l = 0;
        //LOGGER.log(Level.INFO, "Make a sparse-long with {0} bits and +/-{1}", new Object[]{bits, variance});
        if (bits > 12) {
            bits = 12;
        }
        if (variance > 4) {
            variance = 4;
        }

        for (int chunk = 0; chunk < 4; chunk++) {
            int n = (bits - variance) + (int) (Math.random() * 2.0 * variance);
            long edge = 0;
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
            l |= (edge << (16 * chunk));
            //        LOGGER.log(Level.INFO, "       Result: " + String.format("%064d", new BigInteger(Long.toBinaryString(l))));
        }
        return l;
    }

}
