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
package org.eclipse.jface.resource;

import java.net.URL;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * An image descriptor is an object that knows how to create
 * an SWT image.  It does not hold onto images or cache them,
 * but rather just creates them on demand.  An image descriptor
 * is intended to be a lightweight representation of an image
 * that can be manipulated even when no SWT display exists.
 * <p>
 * This package defines a concrete image descriptor implementation
 * which reads an image from a file (<code>FileImageDescriptor</code>).
 * It also provides abstract framework classes (this one and
 * <code>CompositeImageDescriptor</code>) which may be subclassed to define
 * news kinds of image descriptors.
 * </p>
 * <p>
 * Using this abstract class involves defining a concrete subclass
 * and providing an implementation for the <code>getImageData</code>
 * method.
 * </p>
 *
 * @see org.eclipse.swt.graphics.Image
 */
public abstract class ImageDescriptor {

    /** 
     * A small red square used to warn that an image cannot be created.
     * <p>
     */
    protected static final ImageData DEFAULT_IMAGE_DATA = new ImageData(6, 6,
            1, new PaletteData(new RGB[] { new RGB(255, 0, 0) }));

    /**
     * Constructs an image descriptor.
     */
    protected ImageDescriptor() {
        // do nothing
    }

    /**
     * Creates and returns a new image descriptor from a file.
     * Convenience method for
     * <code>new FileImageDescriptor(location,filename)</code>.
     *
     * @param location the class whose resource directory contain the file
     * @param filename the file name
     * @return a new image descriptor
     */
    public static ImageDescriptor createFromFile(Class location, String filename) {
        return new FileImageDescriptor(location, filename);
    }

    /**
     * Creates and returns a new image descriptor from a URL.
     *
     * @param url The URL of the image file.
     * @return a new image descriptor
     */
    public static ImageDescriptor createFromURL(URL url) {
        if (url == null) {
            return getMissingImageDescriptor();
        }
        return new URLImageDescriptor(url);
    }

    /**
     * Creates and returns a new SWT image for this image descriptor.
     * Note that each call returns a new SWT image object.
     * A default image is returned in the event of an error.
     * <p>
     * Note: it is still possible for this method to return <code>null</code> in extreme cases,
     * for example if SWT runs out of image handles.
     * </p>
     *
     * @return a new image or <code>null</code> if the image could not be created
     */
    public Image createImage() {
        return createImage(true);
    }

    /**
     * Creates and returns a new SWT image for this image descriptor.
     * In the even of an error, a default image is returned if 
     * <code>returnMissingImageOnError</code> is true, otherwise <code>null</code>
     * is returned.  
     * <p>
     * Note: Even if <code>returnMissingImageOnError</code> is true, 
     * it is still possible for this method to return <code>null</code> in extreme cases,
     * for example if SWT runs out of image handles.
     * </p>
     *
     * @param returnMissingImageOnError flag that determines if a default image is returned on error
     * @return a new image or <code>null</code> if the image could not be created
     */
    public Image createImage(boolean returnMissingImageOnError) {
        return createImage(returnMissingImageOnError, Display.getCurrent());
    }

    /**
     * Creates and returns a new SWT image for this image descriptor.
     * A default image is returned in the event of an error.
     * <p>
     * Note: it is still possible for this method to return <code>null</code> in extreme cases,
     * for example if SWT runs out of image handles.
     * </p>
     *
     * @param device the device on which to create the image
     * @return a new image or <code>null</code> if the image could not be created
     * @since 2.0
     */
    public Image createImage(Device device) {
        return createImage(true, device);
    }

    /**
     * Creates and returns a new SWT image for this image descriptor.
     * In the even of an error, a default image is returned if 
     * <code>returnMissingImageOnError</code> is true, otherwise <code>null</code>
     * is returned.  
     * <p>
     * Note: Even if <code>returnMissingImageOnError</code> is true, 
     * it is still possible for this method to return <code>null</code> in extreme cases,
     * for example if SWT runs out of image handles.
     * </p>
     *
     * @param returnMissingImageOnError flag that determines if a default image is returned on error
     * @param device the device on which to create the image
     * @return a new image or <code>null</code> if the image could not be created
     * @since 2.0
     */
    public Image createImage(boolean returnMissingImageOnError, Device device) {

        ImageData data = getImageData();
        if (data == null) {
            if (!returnMissingImageOnError) {
                return null;
            } else {
                data = DEFAULT_IMAGE_DATA;
            }
        }

        /*
         * Try to create the supplied image. If there is an SWT Exception try and create
         * the default image if that was requested. Return null if this fails.
         */

        try {
            if (data.transparentPixel >= 0) {
                ImageData maskData = data.getTransparencyMask();
                return new Image(device, data, maskData);
            }
            return new Image(device, data);
        } catch (SWTException exception) {
            if (returnMissingImageOnError) {
                try {
                    return new Image(device, DEFAULT_IMAGE_DATA);
                } catch (SWTException nextException) {
                    return null;
                }
            } else
                return null;
        }
    }

    /**
     * Creates and returns a new SWT <code>ImageData</code> object
     * for this image descriptor.
     * Note that each call returns a new SWT image data object.
     * <p>
     * This framework method is declared public so that it is
     * possible to request an image descriptor's image data without
     * creating an SWT image object.
     * </p>
     * <p>
     * Returns <code>null</code> if the image data could not be created.
     * </p>
     *
     * @return a new image data or <code>null</code>
     */
    public abstract ImageData getImageData();

    /**
     * Returns the shared image descriptor for a missing image.
     *
     * @return the missing image descriptor
     */
    public static ImageDescriptor getMissingImageDescriptor() {
        return MissingImageDescriptor.getInstance();
    }
}