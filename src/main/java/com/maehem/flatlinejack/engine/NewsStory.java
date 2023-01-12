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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author mark
 */
public class NewsStory {
    public static final String PROP_PREFIX = "news.";
    public static final String READ_FLAG = "read";
    public static final String SHOW_FLAG = "show";
    
    private final String uid;
    private final String date;
    private final String headline;
    private final String body;
    
    private boolean read = false;
    private boolean show = false;
    
    public NewsStory( ResourceBundle bundle, String propkey ) {
        this.uid        = propkey.split("\\.")[1];
        this.date       = bundle.getString(propkey + ".date");
        this.headline   = bundle.getString(propkey + ".headline");
        this.body       = bundle.getString(propkey + ".body");
        try {
            String replyable = bundle.getString(propkey + ".show");
            this.show = replyable.equals("true");
        } catch (MissingResourceException ex) {}
    }
    
    public NewsStory( String uid, String date, String headline, String body, boolean read ) {
        this.uid = uid;
        this.date = date;
        this.headline = headline;
        this.body = body;
        this.read = read;
        this.show = true;
    }
    
    public String getUid() {
        return uid;
    }
    
    public String getDate() {
        return date;
    }
    
    public String getHeadline() {
        return headline;
    }
    
    public String getBody() {
        return body;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead( boolean state ) {
        this.read = state;
    }
    
    public boolean canShow() {
        return show;
    }
    
    public void setShow( boolean show ) {
        this.show = show;
    }
    
}
