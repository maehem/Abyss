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
import java.util.List;

/**
 * A basic node type for building dialog chains.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class BabbleNode {

    private String text = "";

    private final ArrayList<Integer> numbers;

    public BabbleNode(int... nums) {
        numbers = new ArrayList<>();
        for (int num : nums) {
            numbers.add(num);
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    protected List<Integer> getNumbers() {
        return numbers;
    }
}
