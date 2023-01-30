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
package com.maehem.flatlinejack.content.matrix.site;

import com.maehem.flatlinejack.content.matrix.sitenode.HeatsinkNode;
import com.maehem.flatlinejack.content.matrix.sitenode.SliceNode;
import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.matrix.MatrixSite;
import java.util.ArrayList;

/**
 *
 * @author mark
 */
public class DefaultSitesList extends ArrayList {

    public DefaultSitesList( GameState gs ) {
        add(new MatrixSite(gs, 0x00201, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00205, SliceNode.class, "" ));
        add(new MatrixSite(gs, 0x0020B, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00304, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x0031A, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00401, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00406, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00503, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x0050A, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00702, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00A01, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00B05, HeatsinkNode.class, "" ));
        add(new MatrixSite(gs, 0x00B0F, HeatsinkNode.class, "" ));
    }
    
}
