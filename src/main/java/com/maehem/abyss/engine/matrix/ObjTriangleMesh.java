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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 *
 * @author mark
 */
public class ObjTriangleMesh extends TriangleMesh {

    /**
     * Create a triangle mesh from a Wavefront OBJ file.
     * 
     * @param is 
     */
    public ObjTriangleMesh( InputStream is ) {
        super(VertexFormat.POINT_TEXCOORD);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        ArrayList<String> vertices = new ArrayList();
        ArrayList<String> vertexTextures = new ArrayList();
        ArrayList<String> vertexNormals = new ArrayList();
        ArrayList<String> faces = new ArrayList();
        
        try {
            while(reader.ready()) {
                String line = reader.readLine();
                if ( line.startsWith("v ") ) {
                    // v 0.000000 35.000000 -35.000000
                    vertices.add(line.substring(2));
                }
                if ( line.startsWith("vt ") ) {
                    // vt 1.750000 3.250000 0.000000
                    vertexTextures.add(line.substring(3));
                }
                if ( line.startsWith("vn ") ) {
                    // vn 0.000000 0.100000 0.000000
                    vertexNormals.add(line.substring(3));
                }
                if ( line.startsWith("f ") ) {
                    // f 2/1/1 3/2/2 1/3/3
                    faces.add(line.substring(2));
                }
                if ( line.startsWith("#")) {
                    continue;
                }
                if ( line.startsWith("mtllib ")) {
                    // mtllib db884de5-0e39-40ce-8be5-9f5ba6497907.mtl
                    continue;
                }
                if ( line.startsWith("usemtl ")) {
                    // usemtl Plastic_-_Glossy_(Grey)
                    continue;
                }
                if ( line.startsWith("g ")) {  // Group
                    // g Body1
                    continue;
                }
                if ( line.startsWith("l ")) {  // Line Element
                    // g 1 2 3 4 5
                    continue;
                }

            }
            reader.close();
            if ( vertexNormals.isEmpty() ) {
                setVertexFormat(VertexFormat.POINT_TEXCOORD);
            } else {
                setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
            }
            processVertices(vertices);
            processVertexTextures(vertexTextures);
            processVertexNormals(vertexNormals);
            processFaces(faces);
            
            
        } catch (IOException ex) {
            Logger.getLogger(ObjTriangleMesh.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * 
     * @param l list of Vertices as:   0.000000 35.000000 -35.000000
     */
    private void processVertices( List<String> l ) {
        for ( String s: l ) {
            String[] pt = s.split(" ");
            getPoints().addAll(
                    Float.valueOf(pt[0]),
                    Float.valueOf(pt[1]),
                    Float.valueOf(pt[2])
            );
        }
    }
    
    /**
     * 
     * @param l list of VertexTextures as: -1.750000 -3.250000 0.000000 (ignore third)
     */
    private void processVertexTextures( List<String> l) {
        if ( !l.isEmpty() ) {
            for ( String s: l ) {
                String[] pt = s.split(" ");
                getTexCoords().addAll(
                        Float.valueOf(pt[0]),
                        Float.valueOf(pt[1]) //,
                        //Float.valueOf(pt[2])
                );
            }
        } else  {
            // No Textures so make a filler.
            ObservableFloatArray texCoords = getTexCoords();
            for ( int i=0; i<getPoints().size()/3;i++) {
                // For each point ad add 2D texture coord
                texCoords.addAll(0.00f, 0.00f);
            }
        }
    }

    /**
     * 
     * @param l list of VertexNormals as: 0.000000 0.100000 0.000000
     */
    private void processVertexNormals( List<String> l ) {
        for ( String s: l ) {
            String[] pt = s.split(" ");
            getNormals().addAll(
                    Float.valueOf(pt[0]),
                    Float.valueOf(pt[1]),
                    Float.valueOf(pt[2])
            );
        }
    }
    
    /**
     * 
     * @param l list of faces as: 1/11/11 7/10/10 6/12/12
     */
    private void processFaces( List<String> l ) {
        // Skip Normals ( third number in a/b/c )
        for ( String s: l ) {
            String[] chunks = s.split(" ");
            for ( int i=0; i<3; i++ ) {
                String[] pt = chunks[i].split("/");
                switch ( pt.length ) {
                    case 1:
                        getFaces().addAll(Integer.parseInt(pt[0])-1);
                        //getFaces().addAll(Integer.parseInt(pt[0])-1);
                        //getFaces().addAll(Integer.parseInt(pt[0])-1);
                        break;
                    case 2:
                        getFaces().addAll(Integer.parseInt(pt[0])-1);
                        getFaces().addAll(Integer.parseInt(pt[1])-1);
                        break;
                    case 3:
                        getFaces().addAll(Integer.parseInt(pt[0])-1);
                        getFaces().addAll(Integer.parseInt(pt[2])-1);
                        getFaces().addAll(Integer.parseInt(pt[1])-1);
                        break;
                }
//                for ( int j=0; j<pt.length; j++) {
//                    if ( j < 2 ) {
//                        getFaces().addAll(Integer.parseInt(pt[j])-1);
//                    }
//                }
//                getFaces().addAll(
//                        Integer.parseInt(pt[0])-1,
//                        Integer.parseInt(pt[1])-1//,
//                        //Integer.parseInt(pt[0])-1
//                );
            }
        }
    }
}
