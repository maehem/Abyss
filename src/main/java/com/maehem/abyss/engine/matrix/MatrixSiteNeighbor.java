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

/**
 *
 * @author mark
 */
public enum MatrixSiteNeighbor {
    W(0, -1), E(0, 1), N(-1, 0), S(1, 0),
    NW(-1, -1), NE(-1, 1), SW(1, -1), SE(1, 1),
    
    WW(0, -2), EE(0, 2), NN(-2, 0), SS(2, 0),
    NWW(-1, -2), NEE(-1, 2), SWW(1, -2), SEE(1, 2),
    
    SSW(2, -1), SSE(2, 1), NNW(-2, -1), NNE(-2, 1);

    public final int col;
    public final int row;

    MatrixSiteNeighbor(int r, int c) {
        row = r;
        col = c;
    }

//    int getR() {
//        return row;
//    }
//    
//    int getC() {
//        return col;
//    }
}
