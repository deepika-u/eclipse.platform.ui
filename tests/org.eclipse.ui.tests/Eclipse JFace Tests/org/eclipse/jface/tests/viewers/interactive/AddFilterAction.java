/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.tests.viewers.interactive;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

public class AddFilterAction extends TestBrowserAction {

    public AddFilterAction(String label, TestBrowser browser) {
        super(label, browser);
    }

    public void run() {
        Viewer viewer = getBrowser().getViewer();
        if (viewer instanceof StructuredViewer)
            ((StructuredViewer) viewer).addFilter(new Filter());
    }
}