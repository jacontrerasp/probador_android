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

package org.zeroxlab.aster.operations;

import javax.script.SimpleBindings;

/**
 * In general, AsterOperation describe an operation to GUI, such as
 * Drag or Click.
 */
public interface AsterOperation{

    /**
     * To get the Name of this Operation
     *
     * @return The name of this Operation
     */
    public String getName();

    /**
     * To record necessary information to AsterCommand
     */
    public void record(OperationListener listener);

    /**
     * Get settings which is record by this operation
     */
    public SimpleBindings getSettings();

    public interface OperationListener {
        public void operationFinished(AsterOperation op);
    }
}
