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
package com.liferay.ide.project.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;


/**
 * @author Gregory Amerson
 */
public class BundleFactoryDelegate extends ProjectModuleFactoryDelegate
{

    private Map <IModule, BundleModulelDelegate> moduleDelegates = new HashMap<IModule, BundleModulelDelegate>(5);

    public BundleFactoryDelegate()
    {
        super();
    }

    @Override
    protected IModule[] createModules( IProject project )
    {
        IModule[] retval = new IModule[0];

        if( project != null && project.getName().startsWith( "sample.bundle" ) )
        {
            retval = new IModule[] { createModule( project.getName(), project.getName(), "liferay.bundle", "1.0", project ) };
        }


        return retval;
    }

    @Override
    public ModuleDelegate getModuleDelegate( IModule module )
    {
        BundleModulelDelegate md = moduleDelegates.get( module );

        if( md == null )
        {
            md = new BundleModulelDelegate( module.getProject() );
            moduleDelegates.put( module, md );
        }

        return md;
    }

}
