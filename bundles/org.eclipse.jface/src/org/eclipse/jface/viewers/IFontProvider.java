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
package org.eclipse.jface.viewers;

import org.eclipse.swt.graphics.Font;

/**
 * Interface to provide font representation for a given element.
 * 
 * @since 3.0
 */
public interface IFontProvider {

    /**
     * Provides a font for the given element.
     * 
     * @param element the element
     * @return the font for the element, or <code>null</code> 
     *   to use the default font
     */
    public Font getFont(Object element);
}