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
    
    public DialogSheet2(DialogPane dialogPane) {
        this.dialogPane = dialogPane;        
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

    /**
     * Updates the current dialog sheet to this sheet.
     */
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

    public void clearReponses() {
        response.clear();
    }
}
