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

import static com.maehem.abyss.Engine.LOGGER;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Inventory extends ArrayList<Thing> {

    public Inventory(int size) {
        super(size);

        // Fill the inventory with EmptyThing placeholders.
        for (int i = 0; i < size; i++) {
            super.add(new EmptyThing());
        }

    }

    @Override
    public boolean add(Thing t) {
        // Get index of first EmptyThing
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i) instanceof EmptyThing) {
                this.set(i, t);

                LOGGER.log(Level.CONFIG, "Placed {0} Thing at Inventory slot: {1}", new Object[]{t.getName(), i});
                return true;
            }
        }

        LOGGER.log(Level.SEVERE, "No empty slots in Inventory! Could not store Thing.");
        return false;
    }

    public boolean hasItemType(String cName) {
        for (Thing t : this) {
            if (t.getClass().getSimpleName().equals(cName)) {
                return true;
            }
        }

        return false;
    }

    public Thing getFirst(String cName) {
        for (Thing t : this) {
            if (t.getClass().getSimpleName().equals(cName)) {
                return t;
            }
        }

        return null;
    }

}
