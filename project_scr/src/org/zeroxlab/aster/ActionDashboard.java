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

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ActionDashboard extends JPanel {

    private final static ActionDashboard sDashboard = new ActionDashboard();

    JButton mPlay;
    JButton mStep;
    JButton mStop;

    ClickListener mListener;

    public synchronized static ActionDashboard getInstance() {
        return sDashboard;
    }

    private ActionDashboard () {
        ButtonListener listener = new ButtonListener();
        mPlay = new JButton("Iniciar");
        mStep = new JButton("Paso a paso");
        mStop = new JButton("Parar");
        setLayout(new GridLayout(1, 3));
        Insets insets = new Insets(0, 0, 0, 0);
        mPlay.setMargin(insets);
        mStep.setMargin(insets);
        mStop.setMargin(insets);
        add(mPlay);
        add(mStep);
        add(mStop);
        mPlay.addActionListener(listener);
        mStep.addActionListener(listener);
        mStop.addActionListener(listener);
        resetButtons();
    }

    public void setListener(ClickListener listener) {
        mListener = listener;
    }

    public void resetButtons() {
        mPlay.setEnabled(true);
        mStep.setEnabled(true);
        mStop.setEnabled(false);
    }

    public void setRunning() {
        mPlay.setEnabled(false);
        mStep.setEnabled(false);
        mStop.setEnabled(true);
    }

    public void setStep() {
        mPlay.setEnabled(true);
        mStep.setEnabled(true);
        mStop.setEnabled(true);
    }

    private void playClicked() {
        if (mListener != null) {
            mListener.onPlayClicked();
        }
    }

    private void StepClicked() {
        if (mListener != null) {
            mListener.onStepClicked();
        }
    }

    private void stopClicked() {
        if (mListener != null) {
            mListener.onStopClicked();
        }
    }

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == mPlay) {
                playClicked();
            } else if (e.getSource() == mStep) {
                StepClicked();
            } else if (e.getSource() == mStop) {
                stopClicked();
            }
        }
    }

    interface ClickListener {
        public void onPlayClicked();
        public void onStepClicked();
        public void onStopClicked();
    }
}
