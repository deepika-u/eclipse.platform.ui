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

package org.eclipse.ui.tests.keys;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.tests.util.UITestCase;

/**
 * Tests Bug 43597
 * 
 * @since 3.0
 */
public class Bug43597Test extends UITestCase {

    /**
     * Constructor for Bug43597Test.
     * 
     * @param name
     *            The name of the test
     */
    public Bug43597Test(String name) {
        super(name);
    }

    /**
     * Tests that setting the text on a text widget to an empty string does not
     * reset the font. This was a problem only on carbon.
     */
    public void testFontReset() {
        String metaCharacter = "\u2325X"; //$NON-NLS-1$

        // Set up a working environment.
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        GridLayout gridLayout = new GridLayout();
        shell.setLayout(gridLayout);
        Text text = new Text(shell, SWT.LEFT);
        text.setFont(new Font(text.getDisplay(),
                "Lucida Grande", 13, SWT.NORMAL)); //$NON-NLS-1$
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        shell.pack();
        shell.open();

        // Set the text once, and get the font.
        text.setText(metaCharacter); //$NON-NLS-1$
        Font fontBefore = text.getFont();

        // Set the font again, and get the font afterward.
        text.setText(""); //$NON-NLS-1$
        text.setText(metaCharacter);
        Font fontAfter = text.getFont();

        // Test.
        assertEquals(
                "Clearing text resets font.", fontBefore.handle, fontAfter.handle); //$NON-NLS-1$

        // Clean up after myself.
        shell.close();
        shell.dispose();
    }
}