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
package org.eclipse.ui.internal.presentations;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.presentations.IPresentablePart;

public class SystemMenuCloseAll extends Action implements ISelfUpdatingAction {

    private DefaultPartPresentation presentation;

    public SystemMenuCloseAll(DefaultPartPresentation presentation) {
        this.presentation = presentation;
        setText(WorkbenchMessages.getString("PartPane.closeAll")); //$NON-NLS-1$
    }

    public void dispose() {
        presentation = null;
    }

    public void run() {
        List parts = presentation.getPresentableParts();
        presentation.close((IPresentablePart[]) parts
                .toArray(new IPresentablePart[parts.size()]));
    }

    public void update() {
        List parts = presentation.getPresentableParts();
        setEnabled(parts.size() != 0);
    }

    public boolean shouldBeVisible() {
        return true;
    }

}