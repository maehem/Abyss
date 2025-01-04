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

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public enum DialogCommand {

    DESC(50), // show in room desc instead of dialog.
    LONG_DESC(51),
    SHORT_DESC(52),
    NPC(53), // Don't toggle to PLAYER after this dialog
    PLAYER(54), // Don't toggle to PLAYER after this dialog
    WORD1(55),
    WORD2(56),
    WHERE_IS(57), // Street Light Girl - Where is Lonny Zone?
    DISCOUNT(58), // Apply vendor discount (asano 20%)
    DESC_NEXT(59), // Place in narration of next room.
    LUNGS(60), // lungs removed at Hitachi
    BODY_SELL(61), // Bodyshop menu
    BODY_BUY(62), // Bodyshop menu
    SKILL_SELL(63), // ??? menu, maybe don't need
    SKILL_BUY(64), // Larry menu, TODO: move to ITEM_BUY
    SKILL_UPGRADE(65), // Larry menu, TODO: move to ITEM_BUY
    INFO_BUY(66), // Massage parlor buy info
    ITEM_BUY(67), // Player buys item from NPC
    ITEM_GET(68), // player receives NPC item directly
    SOFTWARE_BUY(69), // player buys software  (Metro Holo)
    EXIT_T(70), // Exit Top
    EXIT_R(71), // Exit Right
    EXIT_B(72), // Exit Bottom
    EXIT_L(73), // Exit Left
    EXIT_ST_CHAT(74), // Exit Outside Chatsubo
    EXIT_BDSHOP(75), // Exit to body shop.
    EXIT_SHUTTLE_FS(76), // Exit To Freeside Shuttle
    EXIT_SHUTTLE_ZION(77), // Exit To Zion Shuttle
    EXIT_X(78), // Exit determined by code.
    DEATH(79), // Go to jail action
    TO_JAIL(80), // Go to jail action
    DECK_WAIT(81), // Wait till user exit's deck or leaves room.
    UXB(90), // Shin gives UXB
    PASS(91), // Shiva gives Rest. Pass
    CAVIAR(92), // Edo gives ConLink 2.0 for Caviar
    CHIP(94), // n credits.
    FINE_BANK_500(95), // Fine bank
    FINE_BANK_20K(96), // Fine bank
    DIALOG_NO_MORE(97), // Like DIALOG_END but leave dialog open so next command can run.
    DIALOG_CLOSE(98),
    DIALOG_END(99),
    ON_FILTER_1(400), // Call onFilter1() and return the array of int reposnses.
    DESC_DIRECT(500), // Subtract 500 and put remainder(index) in DESC box.
    INVALID_COMMAND(999);

    public final int num;

    private DialogCommand(int num) {
        this.num = num;
    }

    public static DialogCommand getCommand(int num) {
        for (DialogCommand cmd : values()) {
            if (num == cmd.num) {
                return cmd;
            }
        }

        return INVALID_COMMAND;
    }
}
