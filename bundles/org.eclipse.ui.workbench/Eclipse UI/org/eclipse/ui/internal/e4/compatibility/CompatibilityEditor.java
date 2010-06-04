/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.e4.compatibility;

import org.eclipse.e4.ui.workbench.modeling.EPartService;

import java.util.Iterator;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.internal.EditorActionBars;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;

public class CompatibilityEditor extends CompatibilityPart {

	private EditorReference reference;

	@Inject
	CompatibilityEditor(MPart part, EditorReference ref) {
		super(part);
		reference = ref;

		if (!part.getTags().contains(EPartService.REMOVE_ON_HIDE_TAG)) {
			part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		}
	}

	protected void createPartControl(final IWorkbenchPart legacyPart, Composite parent) {
		super.createPartControl(legacyPart, parent);
		EditorDescriptor descriptor = reference.getDescriptor();
		if (descriptor != null) {
			IConfigurationElement element = descriptor.getConfigurationElement();
			if (element != null) {
				String iconURI = element.getAttribute(IWorkbenchRegistryConstants.ATT_ICON);
				if (iconURI != null) {
					StringBuilder builder = new StringBuilder("platform:/plugin/"); //$NON-NLS-1$
					builder.append(element.getNamespaceIdentifier()).append('/');

					// FIXME: need to get rid of $nl$ properly
					if (iconURI.startsWith("$nl$")) { //$NON-NLS-1$
						iconURI = iconURI.substring(4);
					}

					builder.append(iconURI);
					part.setIconURI(builder.toString());
				}
			}
		}
	}

	public IEditorPart getEditor() {
		return (IEditorPart) getPart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.internal.e4.compatibility.CompatibilityPart#getReference()
	 */
	@Override
	public WorkbenchPartReference getReference() {
		return reference;
	}

	@PreDestroy
	void preDestroy() {
		IWorkbenchPartReference reference = getReference();
		WorkbenchPage page = (WorkbenchPage) reference.getPage();
		for (Iterator<EditorReference> it = page.getInternalEditorReferences().iterator(); it
				.hasNext();) {
			IEditorReference ref = it.next();
			if (ref == reference) {
				it.remove();
				continue;
			}
		}
	}

	@Override
	void disposeSite() {
		EditorActionBars bars = (EditorActionBars) ((IEditorSite) getReference().getSite())
				.getActionBars();
		EditorReference.disposeEditorActionBars(bars);
		super.disposeSite();
	}
}
