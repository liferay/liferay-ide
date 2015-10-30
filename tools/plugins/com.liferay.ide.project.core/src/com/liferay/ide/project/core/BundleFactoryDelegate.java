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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
    protected IPath[] getListenerPaths()
    {
        return new IPath[] { new Path( "pom.xml" ), new Path( "bnd.bnd" ), new Path( "build.gradle" ) };
    }

    @Override
    protected IModule[] createModules( IProject project )
    {
        IModule[] retval = new IModule[0];

        ILiferayProject liferayProject = LiferayCore.create( project );

        if( liferayProject instanceof IBundleProject )
        {
            retval = new IModule[] { createSimpleModule( project ) };
        }

        return retval;
    }

    private IModule createSimpleModule( IProject project )
    {
        return createModule( project.getName(), project.getName(), "liferay.bundle", "1.0", project );
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
