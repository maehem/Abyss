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

import java.util.Properties;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public abstract class MiscThing extends Thing {

    private static final String THING_PKG = "misc";

    public MiscThing(String name) {
        super(name);
    }

    @Override
    public void saveState(String key, Properties p) {
    }

    @Override
    public String getPackage() {
        return THING_PKG;
    }


}
