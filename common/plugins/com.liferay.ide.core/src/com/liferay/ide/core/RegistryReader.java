/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


/**
 * Class to read a registry. It is meant to be subclassed to provide specific function.
 *
 * @since 1.0.0
 */
@SuppressWarnings( "deprecation" )
public abstract class RegistryReader {

    private static final String JEM_PLUGIN_ID = "org.eclipse.jem.util"; //$NON-NLS-1$

    String pluginId;

    String extensionPointId;

    private static Bundle systemBundle;

    /**
     * Utility method to get the plugin id of a configuation element
     *
     * @param configurationElement
     * @return plugin id of configuration element
     * @since 1.0.0
     */
    public static String getPluginId(IConfigurationElement configurationElement) {
        String pluginId = null;

        if (configurationElement != null) {
            IExtension extension = configurationElement.getDeclaringExtension();

            if (extension != null)
                pluginId = extension.getContributor().getName();
        }

        return pluginId;
    }

    /**
     * Constructor for RegistryReader taking a registry, plugin id, and extension point id.
     *
     * @param registry
     * @param pluginID
     * @param extensionPoint
     *
     * @deprecated Use RegistryReader(plugin, extensionPoint) instead. The registry passed in is ignored.
     * @since 1.0.0
     */
    public RegistryReader(IPluginRegistry registry, String pluginID, String extensionPoint) {
        this(pluginID, extensionPoint);
    }

    /**
     * Constructor for RegistryReader taking the plugin id and extension point id.
     *
     * @param pluginID
     * @param extensionPoint
     *
     * @since 1.0.0
     */
    public RegistryReader(String pluginID, String extensionPoint) {
        super();
        this.pluginId = pluginID;
        extensionPointId = extensionPoint;
    }

    private void internalReadElement(IConfigurationElement element) {
        boolean recognized = this.readElement(element);
        if (!recognized) {
            logError(element, "Error processing extension: " + element); //$NON-NLS-1$
        }
    }

    /*
     * Logs the error in the desktop log using the provided text and the information in the configuration element.
     */
    protected void logError(IConfigurationElement element, String text) {
        IExtension extension = element.getDeclaringExtension();
        StringBuffer buf = new StringBuffer();
        buf.append("Plugin " + extension.getContributor().getName() + ", extension " + extension.getExtensionPointUniqueIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("\n" + text); //$NON-NLS-1$
        LiferayCore.logError(buf.toString());
    }

    /*
     * Logs a very common registry error when a required attribute is missing.
     */
    protected void logMissingAttribute(IConfigurationElement element, String attributeName) {
        logError(element, "Required attribute '" + attributeName + "' not defined"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * Implement this method to read element attributes. If this element has subelements, the reader will recursively cycle through them and call this
     * method so don't do it here.
     */
    public abstract boolean readElement(IConfigurationElement element);

    /**
     * Read the extension point and parse it.
     *
     * @since 1.0.0
     */
    public void readRegistry() {
        IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(pluginId, extensionPointId);
        if (point != null)
        {
            IConfigurationElement[] elements = point.getConfigurationElements();
            for (int i = 0; i < elements.length; i++) {
                internalReadElement(elements[i]);
            }
        }
        // the following code is to handle the contributions to the deprecated org.eclipse.jem.util extensions
        if (!JEM_PLUGIN_ID.equals(pluginId))
        {
            point = Platform.getExtensionRegistry().getExtensionPoint(JEM_PLUGIN_ID, extensionPointId);
            if (point != null)
            {
                IConfigurationElement[] elements = point.getConfigurationElements();
                for (int i = 0; i < elements.length; i++) {
                    internalReadElement(elements[i]);
                }
            }
        }
    }

    /**
     * Tests to see if it is valid at this point in time to create an executable extension. A valid reason not to would be that the workspace is
     * shutting donw.
     *
     * @param element
     * @return <code>true</code> if it is valid point to create an executable extension.
     *
     * @since 1.0.0
     */
    public static boolean canCreateExecutableExtension(IConfigurationElement element) {
        if (Platform.isRunning() && getSystemBundle().getState() != Bundle.STOPPING)
            return true;
        return false;
    }

    /**
     * Get the system bundle
     *
     * @return system bundle.
     *
     * @since 1.0.0
     */
    protected static Bundle getSystemBundle() {
        if (systemBundle == null)
            systemBundle = Platform.getBundle("org.eclipse.osgi"); //$NON-NLS-1$
        return systemBundle;
    }
}
