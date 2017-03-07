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

import com.liferay.ide.core.ILiferayProjectProvider;
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
 * @author Andy Wu
 */
public class ModuleProjectNameListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        updateLocation( op( event ) );
    }

    protected NewLiferayModuleProjectOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewLiferayModuleProjectOp.class );
    }

    public static void updateLocation( final NewLiferayModuleProjectOp op )
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

            boolean isGradleModule = false;

            ILiferayProjectProvider iProvider = op.getProjectProvider().content();

            if( iProvider != null )
            {
                String shortName = iProvider.getShortName();

                if( !CoreUtil.empty( shortName ) && shortName.startsWith( "gradle" ) )
                {
                    isGradleModule = true;
                }
            }

            boolean isThemeProject = false;

            if(op instanceof NewLiferayModuleProjectOp)
            {
                NewLiferayModuleProjectOp moduleProjectOp = (NewLiferayModuleProjectOp)op;

                String projectTemplateName = moduleProjectOp.getProjectTemplateName().content();

                if( "theme".equals( projectTemplateName ) )
                {
                    isThemeProject = true;
                }
            }

            if( hasLiferayWorkspace && isGradleModule )
            {
                IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

                if( liferayWorkspaceProject != null && liferayWorkspaceProject.exists() )
                {
                    if( isThemeProject )
                    {
                        String[] warsNames =
                            LiferayWorkspaceUtil.getLiferayWorkspaceProjectWarsDirs( liferayWorkspaceProject );

                        // use the first configured wars fodle name
                        newLocationBase =
                            PathBridge.create( liferayWorkspaceProject.getLocation().append( warsNames[0] ) );
                    }
                    else
                    {
                        String folder =
                            LiferayWorkspaceUtil.getLiferayWorkspaceProjectModulesDir( liferayWorkspaceProject );

                        if( folder != null )
                        {
                            IPath appendPath = liferayWorkspaceProject.getLocation().append( folder );

                            appendPath.toFile().mkdirs();

                            newLocationBase = PathBridge.create( appendPath );
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
