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
 * Authored by Kan-Ru Chen <kanru@0xlab.org>
 */

package org.zeroxlab.aster.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;

import org.zeroxlab.aster.JActionList;
import org.zeroxlab.aster.cmds.AsterCommand;

import com.android.ninepatch.NinePatch;

/**
 * Basic UI for {@link JActionList}.
 */
public class BasicActionListUI extends ActionListUI {
    /**
     * The associated action list.
     */
    protected JActionList actionList;

    protected MouseListener mouseListener;

    protected MouseMotionListener mouseMotionListener;

    protected ChangeListener actionListChangeListener;

    protected EventListenerList newActionListenerList = new EventListenerList();

    /*
     * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicActionListUI();
    }

    /*
     * @see javax.swing.plaf.ComponentUI#installUI(javax.swing.JComponent)
     */
    @Override
    public void installUI(JComponent c) {
        actionList = (JActionList) c;
        installDefaults();
        installListeners();
        c.setLayout(null);
    }

    /*
     * @see javax.swing.plaf.ComponentUI#uninstallUI(javax.swing.JComponent)
     */
    @Override
    public void uninstallUI(JComponent c) {
        c.setLayout(null);
        uninstallListeners();
        uninstallDefaults();

        actionList = null;
    }

    public void installDefaults() {
    }

    public void installListeners() {
        mouseListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }
            };
        actionList.addMouseListener(mouseListener);

        mouseMotionListener = new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                }
            };
        actionList.addMouseMotionListener(mouseMotionListener);

        actionListChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    updateButtonList();
                    actionList.repaint();
                }
            };
        actionList.getModel().addChangeListener(
            actionListChangeListener);
        actionList.getModel().addCommandChangeListener(
            actionListChangeListener);
    }

    public void uninstallDefaults() {
    }

    public void uninstallListeners() {
        actionList.removeMouseListener(mouseListener);
        mouseListener = null;

        actionList.removeMouseMotionListener(mouseMotionListener);
        mouseMotionListener = null;

        actionList.getModel().removeChangeListener(
            actionListChangeListener);
        actionList.getModel().removeCommandChangeListener(
            actionListChangeListener);
        actionListChangeListener = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        CloseButton delay = null;
        for (Component child: c.getComponents()) {
            if (child instanceof CloseButton) {
                delay = (CloseButton)child;
            } else {
                child.paint(g);
            }
        }
        if (delay != null) {
            delay.paint(g);
        }
    }

    @Override
    public void addNewActionListener(MouseListener l) {
        newActionListenerList.add(MouseListener.class, l);
    }

    @Override
    public void removeNewActionListener(MouseListener l) {
        newActionListenerList.remove(MouseListener.class, l);
    }

    protected void fireNewAction(MouseEvent e) {
        Object[] listeners = newActionListenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MouseListener.class) {
                ((MouseListener)listeners[i+1]).mouseClicked(e);
            }
        }
    }

    // ******************
    //   Layout Methods
    // ******************
    protected static int BUTTON_MARGIN = 10;
    protected java.util.List<JComponent> buttonList;

    protected void updateButtonList() {
        buttonList = new Vector<JComponent>();
        buttonList.add(new ActionButton(actionList.getModel().getRecall()));
        for (AsterCommand cmd : actionList.getModel().getCommands()) {
            buttonList.add(new ActionButton(cmd));
        }
        updateComponents();
        updateLayout();
        actionList.revalidate();
    }

    protected void updateComponents() {
        actionList.removeAll();
        Iterator<JComponent> it = buttonList.iterator();
        while (it.hasNext()) {
            JComponent btn = it.next();
            if (!it.hasNext() && !actionList.getModel().empty()) {
                CloseButton closebtn = new CloseButton();
                closebtn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            actionList.getModel().popCmd();
                        }
                    });
                actionList.add(closebtn);
            }
            actionList.add(btn);
            LittleArrow arrow = new LittleArrow();
            actionList.add(arrow);
        }
        NewActionButton newbtn = new NewActionButton();
        newbtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fireNewAction(e);
                }
            });
        actionList.add(newbtn);
    }

    protected void updateLayout() {
        int offset_y = BUTTON_MARGIN;
        int width = actionList.getPreferredSize().width;
        Component focused = null;
        Component[] cs = actionList.getComponents();
        for (int i = 0; i < cs.length; i++) {
            ActionButton btn = null;
            Component cls = null;
            if (cs[i] instanceof CloseButton) {
                cls = cs[i];
                i++;
            }
            if (cs[i] instanceof ActionButton) {
                btn = (ActionButton)cs[i];
                Dimension size =  btn.getPreferredSize();
                int offset_x = (width - size.width) / 2;
                btn.setBounds(offset_x, offset_y, size.width, size.height);
                offset_y += btn.getHeight() + BUTTON_MARGIN;
                if (cls != null) {
                    cls.setSize(cls.getPreferredSize());
                    cls.setLocation(btn.getX() + btn.getWidth() - 15,
                                    btn.getY() - 5);
                }
                if (btn.isExecuting())
                    focused = btn;
            }
            if (i+1 < cs.length && cs[i+1] instanceof LittleArrow) {
                // TODO: How to calculate?
                i++;
                cs[i].setLocation(btn.getX(), btn.getY() + btn.getHeight() - 3);
                cs[i].setSize(btn.getWidth(), BUTTON_MARGIN);
            }
            if (i+1 < cs.length && cs[i+1] instanceof NewActionButton) {
                i++;
                cs[i].setSize(cs[i].getPreferredSize());
                cs[i].setLocation((width - cs[i].getWidth())/2, offset_y);
                if (focused == null) {
                    focused = cs[i];
                }
            }
        }
        if (focused != null)
            actionList.scrollRectToVisible(focused.getBounds());
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension max = new Dimension(0, 0);
        for (Component child: c.getComponents()) {
            Dimension d = child.getPreferredSize();
            if (BUTTON_MARGIN * 2 + d.width > max.width) {
                max.width = BUTTON_MARGIN * 2 + d.width;
            }
            max.height += d.height;
            if (child instanceof ActionButton) {
                max.height += BUTTON_MARGIN * 2;
            }
        }
        return max;
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

    static class ActionButton extends JComponent {
        static int TEXT_MARGIN = 10;
        static int CLIP_WIDTH = 54;
        AsterCommand mCommand;
        Font mFont;
        Rectangle mFontBox;
        static NinePatch mPatch;
        static NinePatch mInnerPatch;
        static NinePatch mActivePatch;
        static NinePatch mActiveInnerPatch;
        static BufferedImage mCloseButton;
        BufferedImage mClipImage;

        static {
            try {
                InputStream stream;
                stream = ActionButton.class.getResourceAsStream("/green_border.9.png");
                mPatch = NinePatch.load(stream, true, false);
                stream.close();
                stream = ActionButton.class.getResourceAsStream("/inner_border.9.png");
                mInnerPatch = NinePatch.load(stream, true, false);
                stream.close();
                stream = ActionButton.class.getResourceAsStream("/yellow_border.9.png");
                mActivePatch = NinePatch.load(stream, true, false);
                stream.close();
                stream = ActionButton.class.getResourceAsStream("/inner_yellow_border.9.png");
                mActiveInnerPatch = NinePatch.load(stream, true, false);
                stream.close();
            } catch (IOException e) {
            }
        }

	public ActionButton(AsterCommand cmd) {
            mCommand = cmd;
            mFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
            mFontBox = new Rectangle();
            mClipImage = mCommand.getImage();
        }

        public boolean isExecuting() {
            return mCommand.isExecuting();
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(mFont);
            int width = fm.stringWidth(mCommand.getName()) + TEXT_MARGIN*2;
            int height = fm.getDescent() + fm.getAscent() + TEXT_MARGIN*2;
            if (mClipImage != null) {
                height += TEXT_MARGIN + CLIP_WIDTH;
            }
            return new Dimension(width, height);
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            mFontBox.setLocation(getBounds().getLocation());
            FontMetrics fm = getFontMetrics(mFont);
            mFontBox.translate(TEXT_MARGIN, TEXT_MARGIN + fm.getAscent());
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Rectangle bbox = getBounds();
            NinePatch borderPatch;
            NinePatch innerPatch;
            if (mCommand.isExecuting()) {
                borderPatch = mActivePatch;
                innerPatch = mActiveInnerPatch;
            } else {
                borderPatch = mPatch;
                innerPatch = mInnerPatch;
            }
            if (borderPatch != null) {
                borderPatch.draw((Graphics2D)g,
                            bbox.x,
                            bbox.y,
                            bbox.width,
                            bbox.height);
            }
            g.setFont(mFont);
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                             RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawString(mCommand.getName(),
                         mFontBox.x, mFontBox.y);
            if (mClipImage != null) {
                int clip_x = bbox.x + (bbox.width - CLIP_WIDTH)/2;
                int clip_y = bbox.y + (bbox.height - TEXT_MARGIN - CLIP_WIDTH);
                g.drawImage(mClipImage, clip_x, clip_y, CLIP_WIDTH, CLIP_WIDTH, null);
                if (innerPatch != null) {
                    innerPatch.draw((Graphics2D)g, clip_x, clip_y, CLIP_WIDTH, CLIP_WIDTH);
                }
            }
        }
    }

    static class LittleArrow extends JComponent {
        public LittleArrow() {
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Rectangle bbox = getBounds();
            Line2D.Double line = new Line2D.Double(bbox.x + bbox.width / 2.0,
                                                   bbox.y,
                                                   bbox.x + bbox.width / 2.0,
                                                   bbox.y + bbox.height);
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint( 0,3);
            arrowHead.addPoint( -3, -3);
            arrowHead.addPoint( 3,-3);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.draw(line);
            g2d.translate(line.x2, line.y2);
            g2d.fill(arrowHead);
            g2d.dispose();
        }
    }

    static class NewActionButton extends JComponent {
        static BufferedImage inactiveImage;
        static BufferedImage activeImage;
        static {
            try {
                InputStream stream = NewActionButton.class.getResourceAsStream("/add_button_inactive.png");
                inactiveImage = ImageIO.read(stream);
                stream.close();
                stream = NewActionButton.class.getResourceAsStream("/add_button_active.png");
                activeImage = ImageIO.read(stream);
                stream.close();
            } catch (IOException e) {
            }
        }

        boolean mActiveP = false;

        public NewActionButton() {
            addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        NewActionButton btn = (NewActionButton)e.getSource();
                        btn.setActive(true);
                        btn.repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        NewActionButton btn = (NewActionButton)e.getSource();
                        btn.setActive(false);
                        btn.repaint();
                    }
                });
            Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
            setCursor(handCursor);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(inactiveImage.getWidth(), inactiveImage.getHeight());
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Rectangle bbox = getBounds();
            if (mActiveP) {
                g.drawImage(activeImage, bbox.x, bbox.y, bbox.width, bbox.height, null);
            } else {
                g.drawImage(inactiveImage, bbox.x, bbox.y, bbox.width, bbox.height, null);
            }
        }
        public void setActive(boolean activep) {
            mActiveP = activep;
        }
    }

    static class CloseButton extends JComponent {
        static BufferedImage closeImage;
        static {
            try {
                InputStream stream = CloseButton.class.getResourceAsStream("/close_button.png");
                closeImage = ImageIO.read(stream);
                stream.close();
            } catch (IOException e) {
            }
        }

        public CloseButton() {
            Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
            setCursor(handCursor);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(closeImage.getWidth(), closeImage.getHeight());
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Rectangle bbox = getBounds();
            g.drawImage(closeImage, bbox.x, bbox.y, bbox.width, bbox.height, null);
        }
    }
}
