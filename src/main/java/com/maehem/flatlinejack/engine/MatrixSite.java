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

/**
 * Holds information and game state for a Matrix location.
 * 
 * @author mark
 */
public abstract class MatrixSite {
    private final int address; // Site Address

    public MatrixSite( int stratus, int col, int row ) {
        this.address = (stratus&0xF) << 9 | (col&0xFF) << 4 | (row&0xFF);
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
    
    public int getAddress() {
        return  address;
    }
    
    public static String toHex( int address ) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%01X", (address&0xF0000)>>16))
                .append(":")
                .append(String.format("%02X", (address&0xFF00)>>8))
                .append(":")
                .append(String.format("%02X", (address&0xFF)));
        
        return sb.toString();
    }
}
