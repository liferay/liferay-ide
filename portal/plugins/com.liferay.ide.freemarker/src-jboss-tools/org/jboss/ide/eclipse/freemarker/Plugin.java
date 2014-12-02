/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class Plugin extends AbstractUIPlugin {

	public static final String ID = "com.liferay.ide.freemarker"; //$NON-NLS-1$
	
	//The shared instance.
	private static Plugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	public Plugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.jboss.ide.eclipse.freemarker.resources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * The constructor.
	 */
	public Plugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Plugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static Plugin getInstance() {
		return plugin;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * Initializes the plugin preferences with default preference values for
	 * this plug-in.
	 */
	protected void initializeDefaultPluginPreferences() {
		Preferences prefs = getPluginPreferences();
		prefs.setDefault(Constants.HIGHLIGHT_RELATED_ITEMS, true);
		prefs.setDefault(Constants.COLOR_COMMENT, "170,0,0"); //$NON-NLS-1$
		prefs.setDefault(Constants.COLOR_TEXT, "0,0,0"); //$NON-NLS-1$
		prefs.setDefault(Constants.COLOR_INTERPOLATION, "255,0,128"); //$NON-NLS-1$
		prefs.setDefault(Constants.COLOR_DIRECTIVE, "0,0,255"); //$NON-NLS-1$
		prefs.setDefault(Constants.COLOR_STRING, "0,128,128"); //$NON-NLS-1$
		prefs.setDefault(Constants.COLOR_XML_COMMENT, "128,128,128"); //$NON-NLS-1$
		prefs.setDefault(Constants.COLOR_XML_TAG, "0,0,128"); //$NON-NLS-1$
		prefs.setDefault(Constants.COLOR_RELATED_ITEM, "255,255,128"); //$NON-NLS-1$
	}

	public static void error (Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		MessageDialog.openError(Display.getCurrent().getActiveShell(), t.getMessage(), sw.toString());
		log(t);
	}

	public static void log (Throwable t) {
		IStatus status = new Status(IStatus.ERROR,ID,t.getMessage(),t);
		Plugin.getDefault().getLog().log(status);
	}

	public static void log (String s) {
		IStatus status = new Status(IStatus.ERROR,ID,s);
		Plugin.getDefault().getLog().log(status);
	}
	
	public Image getImage(String key) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null || image.isDisposed()) {
			ImageDescriptor descriptor = getImageDescriptor(key);
			if (descriptor != null) {
				image = descriptor.createImage();
				imageRegistry.put(key, image);
			}
		}
		return image;
	}

	public ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor imageDescriptor = null;
		URL gifImageURL = getImageURL(key);
		if (gifImageURL != null)
			imageDescriptor = ImageDescriptor.createFromURL(gifImageURL);
		return imageDescriptor;
	}

	private URL getImageURL(String key) {
		Bundle bundle = getBundle();
		IPath path = new Path("icons").append(key); //$NON-NLS-1$
		if (FileLocator.find(bundle,path,null) == null)
			return null;
		try {
			return new URL( bundle.getEntry("/"), path.toString()); //$NON-NLS-1$ 
		} catch (MalformedURLException exception) {
			log(exception);
		}
		return null;
	}
}