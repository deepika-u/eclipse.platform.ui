/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.bindings.keys.formatting;

import java.util.HashMap;
import java.util.ResourceBundle;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyLookupFactory;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;

/**
 * <p>
 * Formats the key sequences and key strokes into the native human-readable
 * format. This is typically what you would see on the menus for the given
 * platform and locale.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>. The commands architecture is currently under
 * development for Eclipse 3.1. This class -- its existence, its name and its
 * methods -- are in flux. Do not use this class yet.
 * </p>
 * 
 * @since 3.1
 */
public class NativeKeyFormatter extends AbstractKeyFormatter {

	/**
	 * The key into the internationalization resource bundle for the delimiter
	 * to use between keys (on the Carbon platform).
	 */
	private final static String CARBON_KEY_DELIMITER_KEY = "CARBON_KEY_DELIMITER"; //$NON-NLS-1$

	/**
	 * A look-up table for the string representations of various carbon keys.
	 */
	private final static HashMap CARBON_KEY_LOOK_UP = new HashMap();

	/**
	 * The resource bundle used by <code>format()</code> to translate formal
	 * string representations by locale.
	 */
	private final static ResourceBundle RESOURCE_BUNDLE;

	/**
	 * The key into the internationalization resource bundle for the delimiter
	 * to use between key strokes (on the Win32 platform).
	 */
	private final static String WIN32_KEY_STROKE_DELIMITER_KEY = "WIN32_KEY_STROKE_DELIMITER"; //$NON-NLS-1$

	static {
		RESOURCE_BUNDLE = ResourceBundle.getBundle(NativeKeyFormatter.class
				.getName());

		final String carbonBackspace = Character.toString('\u232B');
		CARBON_KEY_LOOK_UP.put(IKeyLookup.BS_NAME, carbonBackspace);
		CARBON_KEY_LOOK_UP.put(IKeyLookup.BACKSPACE_NAME, carbonBackspace);
		CARBON_KEY_LOOK_UP
				.put(IKeyLookup.CR_NAME, Character.toString('\u21A9'));
		final String carbonDelete = Character.toString('\u2326');
		CARBON_KEY_LOOK_UP.put(IKeyLookup.DEL_NAME, carbonDelete);
		CARBON_KEY_LOOK_UP.put(IKeyLookup.DELETE_NAME, carbonDelete);
		CARBON_KEY_LOOK_UP.put(IKeyLookup.SPACE_NAME, Character
				.toString('\u2423'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.ALT_NAME, Character
				.toString('\u2325'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.COMMAND_NAME, Character
				.toString('\u2318'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.CTRL_NAME, Character
				.toString('\u2303'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.SHIFT_NAME, Character
				.toString('\u21E7'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.ARROW_DOWN_NAME, Character
				.toString('\u2193'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.ARROW_LEFT_NAME, Character
				.toString('\u2190'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.ARROW_RIGHT_NAME, Character
				.toString('\u2192'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.ARROW_UP_NAME, Character
				.toString('\u2191'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.END_NAME, Character
				.toString('\u2198'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.NUMPAD_ENTER_NAME, Character
				.toString('\u2324'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.HOME_NAME, Character
				.toString('\u2196'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.PAGE_DOWN_NAME, Character
				.toString('\u21DF'));
		CARBON_KEY_LOOK_UP.put(IKeyLookup.PAGE_UP_NAME, Character
				.toString('\u21DE'));
	}

	/**
	 * Formats an individual key into a human readable format. This uses an
	 * internationalization resource bundle to look up the key. This does the
	 * platform-specific formatting for Carbon.
	 * 
	 * @param key
	 *            The key to format.
	 * @return The key formatted as a string; should not be <code>null</code>.
	 */
	public final String format(final int key) {
		final IKeyLookup lookup = KeyLookupFactory.getDefault();
		final String name = lookup.formalNameLookup(key);

		// TODO consider platform-specific resource bundles
		if ("carbon".equals(SWT.getPlatform())) { //$NON-NLS-1$    	
			String formattedName = (String) CARBON_KEY_LOOK_UP.get(name);
			if (formattedName != null) {
				return formattedName;
			}
		}

		return super.format(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.keys.AbstractKeyFormatter#getKeyDelimiter()
	 */
	protected String getKeyDelimiter() {
		// We must do the look up every time, as our locale might change.
		if ("carbon".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return Util.translateString(RESOURCE_BUNDLE,
					CARBON_KEY_DELIMITER_KEY, Util.ZERO_LENGTH_STRING);
		}

		return Util.translateString(RESOURCE_BUNDLE, KEY_DELIMITER_KEY,
				KeyStroke.KEY_DELIMITER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.keys.AbstractKeyFormatter#getKeyStrokeDelimiter()
	 */
	protected String getKeyStrokeDelimiter() {
		// We must do the look up every time, as our locale might change.
		if ("win32".equals(SWT.getPlatform())) { //$NON-NLS-1$
			return Util.translateString(RESOURCE_BUNDLE,
					WIN32_KEY_STROKE_DELIMITER_KEY,
					KeySequence.KEY_STROKE_DELIMITER);
		}

		return Util.translateString(RESOURCE_BUNDLE, KEY_STROKE_DELIMITER_KEY,
				KeySequence.KEY_STROKE_DELIMITER);
	}

	protected int[] sortModifierKeys(final int modifierKeys) {
		final IKeyLookup lookup = KeyLookupFactory.getDefault();
		final String platform = SWT.getPlatform();
		final int[] sortedKeys = new int[4];
		int index = 0;

		if ("win32".equals(platform)) { //$NON-NLS-1$
			if ((modifierKeys & lookup.getCtrl()) != 0) {
				sortedKeys[index++] = lookup.getCtrl();
			}
			if ((modifierKeys & lookup.getAlt()) != 0) {
				sortedKeys[index++] = lookup.getAlt();
			}
			if ((modifierKeys & lookup.getShift()) != 0) {
				sortedKeys[index++] = lookup.getShift();
			}

		} else if ("gtk".equals(platform) || "motif".equals(platform)) { //$NON-NLS-1$ //$NON-NLS-2$
			if ((modifierKeys & lookup.getShift()) != 0) {
				sortedKeys[index++] = lookup.getShift();
			}
			if ((modifierKeys & lookup.getCtrl()) != 0) {
				sortedKeys[index++] = lookup.getCtrl();
			}
			if ((modifierKeys & lookup.getAlt()) != 0) {
				sortedKeys[index++] = lookup.getAlt();
			}

		} else if ("carbon".equals(platform)) { //$NON-NLS-1$
			if ((modifierKeys & lookup.getShift()) != 0) {
				sortedKeys[index++] = lookup.getShift();
			}
			if ((modifierKeys & lookup.getCtrl()) != 0) {
				sortedKeys[index++] = lookup.getCtrl();
			}
			if ((modifierKeys & lookup.getAlt()) != 0) {
				sortedKeys[index++] = lookup.getAlt();
			}
			if ((modifierKeys & lookup.getCommand()) != 0) {
				sortedKeys[index++] = lookup.getCommand();
			}

		}

		return sortedKeys;
	}
}
