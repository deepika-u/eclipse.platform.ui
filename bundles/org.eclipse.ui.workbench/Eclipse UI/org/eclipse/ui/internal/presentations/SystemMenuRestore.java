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

import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.presentations.IStackPresentationSite;

public class SystemMenuRestore extends SystemMenuStateChange {

    /**
     * @param site
     * @param name
     * @param state
     */
    public SystemMenuRestore(IStackPresentationSite site) {
        super(site, WorkbenchMessages.getString("PartPane.restore"), //$NON-NLS-1$
                IStackPresentationSite.STATE_RESTORED);
    }
}