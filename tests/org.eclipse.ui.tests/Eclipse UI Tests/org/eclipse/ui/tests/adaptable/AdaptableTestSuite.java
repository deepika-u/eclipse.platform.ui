/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.tests.adaptable;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The AdaptableTestSuite is the TestSuite for the
 * adaptable support in the UI.
 */
public class AdaptableTestSuite extends TestSuite {

    /**
     * Returns the suite.  This is required to
     * use the JUnit Launcher.
     */
    public static Test suite() {
        return new AdaptableTestSuite();
    }

    /**
     * Constructor for AdaptableTestSuite.
     */
    public AdaptableTestSuite() {
        addTest(new TestSuite(AdaptableDecoratorTestCase.class));
        addTest(new TestSuite(MarkerImageProviderTest.class));
    }

}