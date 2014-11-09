/*
 * Copyright (C) 2011 0xlab - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authored by Julian Chu <walkingice@0xlab.org>
 *             Kan-Ru Chen <kanru@0xlab.org>
 *             Wei-Ning Huang <azhuang@0xlab.org>
 */

package org.zeroxlab.aster;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.zeroxlab.aster.cmds.AsterCommand;
import org.zeroxlab.aster.cmds.Drag;
import org.zeroxlab.aster.cmds.Press;
import org.zeroxlab.aster.cmds.Recall;
import org.zeroxlab.aster.cmds.Touch;
import org.zeroxlab.aster.cmds.Type;
import org.zeroxlab.aster.cmds.Wait;

public class CmdSelector {

    static final Map<String, Class> commands = new LinkedHashMap<String, Class>()
    {
        {
            put("Touch", Touch.class);
            put("Drag", Drag.class);
            put("Press", Press.class);
            put("Type", Type.class);
            put("Recall", Recall.class);
            put("Wait", Wait.class);
        }
    };

    static public AsterCommand selectCommand(Component parent) {
        String s = (String)JOptionPane.showInputDialog(
            parent,
            "Seleccionando el siguiente comando",
            "Siguiente comando",
            JOptionPane.PLAIN_MESSAGE,
            null,
            commands.keySet().toArray(),
            null);
        try {
            if (s != null) {
                return (AsterCommand)commands.get(s).getConstructor().newInstance();
            }
        } catch (Exception e) {
            System.out.println("Warning: La clase no puede ser inicializada");
            e.printStackTrace();
        }
        return null;
    }
}
