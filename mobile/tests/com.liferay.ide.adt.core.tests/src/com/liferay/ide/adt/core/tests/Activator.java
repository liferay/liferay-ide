/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.adt.core.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/**
 * @author Gregory Amerson
 */
public class Activator extends Plugin
{

    // The shared instance
    private static Activator plugin;

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault()
    {
        return plugin;
    }

    /**
     * The constructor
     */
    public Activator()
    {
    }

    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );
    }

}
