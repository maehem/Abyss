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
package com.maehem.abyss.engine.babble;

import java.util.ArrayList;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class DialogSheet2 implements DialogResponseAction {

    private final DialogPane dialogPane;
    private String dialogText = "Dialog text.";
    private final ArrayList<DialogResponse2> response = new ArrayList<>();
    
//    private final Rectangle dialogTextRect = new Rectangle();
//    //private final ArrayList<DialogResponse> responses = new ArrayList<>();
//    private double width;
//    private double height;
//    private final double MARGIN = 30;
//    private final FlowPane responsePane = new FlowPane(10, 10);

    public DialogSheet2(DialogPane dialogPane) {
        this.dialogPane = dialogPane;
        
        //dialogText = new Text("404\n");
//        dialogText.setX(boxX+30);
//        dialogText.setY(boxY+30);
        //dialogText.setFont(new Font(20.0));
        
        //dialogTextRect.setFill(Color.TRANSPARENT);
        
        // Debug rect
        //dialogTextRect.setStrokeWidth(2.0);
        //dialogTextRect.setStroke(Color.RED);
        
        //setAlignment(Pos.CENTER);
        //responsePane.setAlignment(Pos.CENTER);
        
        //StackPane dialogTextPane = new StackPane(dialogTextRect,dialogText);
                
        //getChildren().addAll(dialogTextPane, responsePane);
    }
    
    public String getDialogText(){
        return dialogText;
    }
    
    public void setDialogText(String dialog) {
        this.dialogText = dialog;
    }
    
    public ArrayList<DialogResponse2> getResponse() {
        return response;
    }
    
    public boolean addResponse( DialogResponse2 d) {
        return response.add(d);
    }
    
    public boolean removeResponse( DialogResponse2 d) {
        return response.remove(d);
    }

//    void setGeometry(double x, double y, double width, double height) {
//        this.width = width;
//        this.height = height;
//        //setLayoutX(x+width*0.1);
//        //setLayoutY(y+height*0.1);
//        
//        dialogTextRect.setWidth(width*0.8);
//        dialogTextRect.setHeight(height/2);
//        //dialogText.setX(MARGIN);
//        //dialogText.setY(MARGIN);
//        dialogText.setFont(new Font(MARGIN));
//        
////        double w = width*0.7;
////        double h = height/9;
////        responses.forEach((r) -> {
////            r.setGeometry(w, h);
////        });        
//    }

    @Override
    public void doResponseAction() {
        dialogPane.setCurrentDialogSheet(this);
    }

    /**
     * @return the dialogScreen
     */
    public DialogPane getDialogPane() {
        return dialogPane;
    }
}
