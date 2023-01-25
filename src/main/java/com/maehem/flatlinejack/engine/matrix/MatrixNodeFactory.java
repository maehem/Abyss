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

import com.maehem.flatlinejack.engine.matrix.MatrixSite;
import static com.maehem.flatlinejack.Engine.LOGGER;

import com.maehem.flatlinejack.Engine;
import com.maehem.flatlinejack.engine.matrix.EmptyMatrixNode;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 *
 * @author mark
 */
public class MatrixNodeFactory {

    public static final MatrixNode getNewMatrixNode(MatrixSite site, double size) {
        if ( site.getNodeName().isEmpty() ) {
            return new EmptyMatrixNode(site, size);
        }
        try {
            Class<?> c = Class.forName(Engine.class.getPackageName() + ".content.matrix.sitenode." + site.getNodeName() );
            Constructor<?> cons = c.getConstructor(MatrixSite.class, double.class);
            Object object = cons.newInstance(site, size);
            LOGGER.log(Level.FINER, "MatrixNodeFactory: Loaded Matrix Node: {0}", site.getNodeName());
            return (MatrixNode) object;
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return null;
        
        // TODO: catch classNotFound and return a distinctive site node.
    }
}