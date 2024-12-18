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

/**
 * Tokens that act as a game progress metric. Subclasses are defined in game
 * sub-package.
 *
 * Examples: BankRobbed, MuffinGiven, ShovelReceived ...
 *
 * Kept in a GameState list for Vignettes logic to check if they are present.
 * Saved as a list of Class names. i.e. goals = BankeRobbed,MuffinGiven
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class GameGoal {
    public static final String PROP_PREFIX = "goals";
    //private String userData; // TODO
    //public String getUserData() { return userData; }
    //public void setUserData(String data){ userData = data }
    // public String toSaveString() { return geClass.getSimplename() + "." + userData; )

}
