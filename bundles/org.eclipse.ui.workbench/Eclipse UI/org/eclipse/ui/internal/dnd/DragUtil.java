/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.ui.internal.DragCursors;

/**
 * Provides the methods for attaching drag-and-drop listeners to SWT controls. 
 */
public class DragUtil {
    private static final String DROP_TARGET_ID = "org.eclipse.ui.internal.dnd.dropTarget"; //$NON-NLS-1$

    /**
     * The location where all drags will end. If this is non-null, then
     * all user input is ignored in drag/drop. If null, we use user input
     * to determine where objects should be dropped.
     */
    private static TestDropLocation forcedDropTarget = null;

    /**
     * List of IDragOverListener
     */
    private static List defaultTargets = new ArrayList();

    /**
     * Sets the drop target for the given control. It is possible to add one or more 
     * targets for a "null" control. This becomes a default target that is used if no
     * other targets are found (for example, when dragging objects off the application
     * window). 
     * 
     * @param control the control that should be treated as a drag target, or null
     * to indicate the default target
     * @param target the drag target to handle the given control
     */
    public static void addDragTarget(Control control, IDragOverListener target) {
        if (control == null) {
            defaultTargets.add(target);
        } else {
            List targetList = getTargetList(control);

            if (targetList == null) {
                targetList = new ArrayList(1);
            }
            targetList.add(target);
            control.setData(DROP_TARGET_ID, targetList);
        }
    }

    private static List getTargetList(Control control) {
        List result = (List) control.getData(DROP_TARGET_ID);
        return result;
    }

    /**
     * Removes a drop target from the given control.
     * 
     * @param control
     * @param target
     */
    public static void removeDragTarget(Control control,
            IDragOverListener target) {
        if (control == null) {
            defaultTargets.remove(target);
        } else {
            List targetList = getTargetList(control);
            if (targetList != null) {
                targetList.remove(target);
                if (targetList.isEmpty()) {
                    control.setData(DROP_TARGET_ID, null);
                }
            }
        }
    }

    /**
     * Shorthand method. Returns the bounding rectangle for the given control, in
     * display coordinates. 
     * 
     * @param draggedItem
     * @param boundsControl
     * @return
     */
    public static Rectangle getDisplayBounds(Control boundsControl) {
        Control parent = boundsControl.getParent();
        if (parent == null) {
            return boundsControl.getBounds();
        }

        return Geometry.toDisplay(parent, boundsControl.getBounds());
    }

    public static boolean performDrag(final Object draggedItem,
            Rectangle sourceBounds) {
        return performDrag(draggedItem, sourceBounds, Display.getDefault()
                .getCursorLocation(), false);
    }

    public static boolean performDrag(final Object draggedItem,
            Rectangle sourceBounds, Point initialLocation, boolean allowSnapping) {

        IDropTarget target = dragToTarget(draggedItem, sourceBounds,
                initialLocation, allowSnapping);

        if (target == null) {
            return false;
        }

        target.drop();

        return true;
    }

    /**
     * Drags the given item to the given location (in display coordinates). This
     * method is intended for use by test suites.
     * 
     * @param draggedItem object being dragged
     * @param finalLocation location being dragged to
     * @return true iff the drop was accepted
     */
    public static boolean dragTo(Display display, Object draggedItem,
            Point finalLocation, Rectangle dragRectangle) {
        Control currentControl = SwtUtil.findControl(display, finalLocation);

        IDropTarget target = getDropTarget(currentControl, draggedItem,
                finalLocation, dragRectangle);

        if (target == null) {
            return false;
        }

        target.drop();

        return true;
    }

    /**
     * Forces all drags to end at the given position (display coordinates). Intended
     * for use by test suites. If this method is called, then all subsequent calls
     * to performDrag will terminate immediately and behave as though the object were
     * dragged to the given location. Calling this method with null cancels this 
     * behavior and causes performDrag to behave normally. 
     * 
     * @param forcedLocation location where objects will be dropped (or null to
     * cause drag/drop to behave normally).
     */
    public static void forceDropLocation(TestDropLocation forcedLocation) {
        forcedDropTarget = forcedLocation;
    }

    /**
     * Drags the given item, given an initial bounding rectangle in display coordinates.
     * Due to a quirk in the Tracker class, changing the tracking rectangle when using the
     * keyboard will also cause the mouse cursor to move. Since "snapping" causes the tracking
     * rectangle to change based on the position of the mouse cursor, it is impossible to do
     * drag-and-drop with the keyboard when snapping is enabled.    
     * 
     * @param draggedItem object being dragged
     * @param sourceBounds initial bounding rectangle for the dragged item
     * @param initialLocation initial position of the mouse cursor
     * @param allowSnapping true iff the rectangle should snap to the drop location. This must
     * be false if the user might be doing drag-and-drop using the keyboard. 
     *  
     * @return
     */
    static IDropTarget dragToTarget(final Object draggedItem,
            final Rectangle sourceBounds, final Point initialLocation,
            final boolean allowSnapping) {
        final Display display = Display.getDefault();

        if (forcedDropTarget != null) {

            Point location = forcedDropTarget.getLocation();
            Control currentControl = SwtUtil.findControl(display, location);

            return getDropTarget(currentControl, draggedItem, location,
                    sourceBounds);
        }

        // Create a tracker.  This is just an XOR rect on the screen.
        // As it moves we notify the drag listeners.
        final Tracker tracker = new Tracker(display, SWT.NULL);

        tracker.setStippled(true);

        tracker.addListener(SWT.Move, new Listener() {
            public void handleEvent(final Event event) {
                display.syncExec(new Runnable() {
                    public void run() {
                        Point location = new Point(event.x, event.y);

                        Control targetControl = display.getCursorControl();

                        IDropTarget target = getDropTarget(targetControl,
                                draggedItem, location,
                                tracker.getRectangles()[0]);

                        Rectangle snapTarget = null;

                        if (target != null) {
                            snapTarget = target.getSnapRectangle();

                            tracker.setCursor(target.getCursor());
                        } else {
                            tracker.setCursor(DragCursors
                                    .getCursor(DragCursors.INVALID));
                        }

                        if (allowSnapping) {

                            if (snapTarget == null) {
                                snapTarget = new Rectangle(sourceBounds.x
                                        + location.x - initialLocation.x,
                                        sourceBounds.y + location.y
                                                - initialLocation.y,
                                        sourceBounds.width, sourceBounds.height);
                            }

                            // Try to prevent flicker: don't change the rectangles if they're already in
                            // the right location

                            Rectangle[] currentRectangles = tracker
                                    .getRectangles();

                            if (!(currentRectangles.length == 1 && currentRectangles[0]
                                    .equals(snapTarget))) {
                                tracker
                                        .setRectangles(new Rectangle[] { snapTarget });
                            }
                        }
                    }
                });
            }
        });

        if (sourceBounds != null) {
            tracker.setRectangles(new Rectangle[] { new Rectangle(
                    sourceBounds.x, sourceBounds.y, sourceBounds.width,
                    sourceBounds.height) });
        }

        // HACK:
        // Some control needs to capture the mouse during the drag or other 
        // controls will interfere with the cursor
        Control startControl = display.getCursorControl();
        if (startControl != null) {
            startControl.setCapture(true);
        }

        // Run tracker until mouse up occurs or escape key pressed.
        boolean trackingOk = tracker.open();

        // HACK:
        // Release the mouse now
        if (startControl != null) {
            startControl.setCapture(false);
        }

        Point finalLocation = display.getCursorLocation();

        IDropTarget dropTarget = null;
        if (trackingOk) {
            Control targetControl = display.getCursorControl();

            dropTarget = getDropTarget(targetControl, draggedItem,
                    finalLocation, tracker.getRectangles()[0]);
        }

        // Cleanup.
        tracker.dispose();

        return dropTarget;
    }

    /**
     * Given a list of IDragOverListeners and a description of what is being dragged, it returns
     * a IDropTarget for the current drop.
     * 
     * @param toSearch
     * @param mostSpecificControl
     * @param draggedObject
     * @param position
     * @param dragRectangle
     * @return
     */
    private static IDropTarget getDropTarget(List toSearch,
            Control mostSpecificControl, Object draggedObject, Point position,
            Rectangle dragRectangle) {
        if (toSearch == null) {
            return null;
        }

        Iterator iter = toSearch.iterator();
        while (iter.hasNext()) {
            IDragOverListener next = (IDragOverListener) iter.next();

            IDropTarget dropTarget = next.drag(mostSpecificControl,
                    draggedObject, position, dragRectangle);

            if (dropTarget != null) {
                return dropTarget;
            }
        }

        return null;
    }

    /**
     * Returns the drag target for the given control or null if none. 
     * 
     * @param toSearch
     * @param e
     * @return
     */
    public static IDropTarget getDropTarget(Control toSearch,
            Object draggedObject, Point position, Rectangle dragRectangle) {

        for (Control current = toSearch; current != null; current = current
                .getParent()) {
            IDropTarget dropTarget = getDropTarget(getTargetList(current),
                    toSearch, draggedObject, position, dragRectangle);

            if (dropTarget != null) {
                return dropTarget;
            }

            // Don't look to parent shells for drop targets
            if (current instanceof Shell) {
                break;
            }
        }

        // No controls could handle this event -- check for default targets
        return getDropTarget(defaultTargets, toSearch, draggedObject, position,
                dragRectangle);
    }

    /**
     * Returns the location of the given event, in display coordinates
     * @return
     */
    public static Point getEventLoc(Event event) {
        Control ctrl = (Control) event.widget;
        return ctrl.toDisplay(new Point(event.x, event.y));
    }

}