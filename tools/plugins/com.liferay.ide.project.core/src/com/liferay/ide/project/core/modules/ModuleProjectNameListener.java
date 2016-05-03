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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class ModuleProjectNameListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        updateLocation( op( event ) );
    }

    protected BaseModuleOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( BaseModuleOp.class );
    }

    public static void updateLocation( final BaseModuleOp op )
    {
        final String currentProjectName = op.getProjectName().content( true );

        if( currentProjectName == null || CoreUtil.isNullOrEmpty( currentProjectName.trim() ) )
        {
            return;
        }

        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );

        if( useDefaultLocation )
        {
            Path newLocationBase = null;
            boolean hasLiferayWorkspace = false;

            try
            {
                hasLiferayWorkspace = LiferayWorkspaceUtil.hasLiferayWorkspace();
            }
            catch( Exception e )
            {
                ProjectCore.logError( "Failed to check LiferayWorkspace project. " );
            }

            if( hasLiferayWorkspace )
            {
                IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

                if( liferayWorkspaceProject != null && liferayWorkspaceProject.exists() )
                {
                    String liferayWorkspaceProjectModulesDir =
                        LiferayWorkspaceUtil.getLiferayWorkspaceProjectModulesDir( liferayWorkspaceProject );

                    if( liferayWorkspaceProjectModulesDir != null )
                    {
                        IPath modulesPath =
                            liferayWorkspaceProject.getLocation().append( liferayWorkspaceProjectModulesDir );

                        if( modulesPath != null && modulesPath.toFile().exists() )
                        {
                            newLocationBase = PathBridge.create( modulesPath );
                        }
                        else
                        {
                            newLocationBase = PathBridge.create(
                                liferayWorkspaceProject.getLocation().append( "modules" ) );
                        }
                    }
                }
                else
                {
                    newLocationBase = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );
                }
            }
            else
            {
                newLocationBase = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );
            }

            if( newLocationBase != null )
            {
                op.setLocation( newLocationBase );
            }
        }
    }

}
