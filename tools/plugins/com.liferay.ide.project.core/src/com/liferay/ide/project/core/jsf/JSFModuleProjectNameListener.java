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

package com.liferay.ide.project.core.jsf;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

/**
 * @author Simon Jiang
 */
public class JSFModuleProjectNameListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        updateLocation( op( event ) );
    }

    protected NewLiferayJSFModuleProjectOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewLiferayJSFModuleProjectOp.class );
    }

    public static void updateLocation( final NewLiferayJSFModuleProjectOp op )
    {
        final String currentProjectName = op.getProjectName().content( true );
        Path newLocationBase = null;

        if( currentProjectName == null || CoreUtil.isNullOrEmpty( currentProjectName.trim() ) )
        {
            return;
        }

        final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );

        if( useDefaultLocation )
        {
            newLocationBase = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );
        }
        else
        {
            final Path currentProjectLocation = op.getLocation().content( true );

            boolean hasLiferayWorkspace = false;

            if ( currentProjectLocation != null )
            {
                hasLiferayWorkspace = LiferayWorkspaceUtil.isWorkspace( currentProjectLocation.toFile() );
            }            

            if( hasLiferayWorkspace )
            {
                File workspaceDir = LiferayWorkspaceUtil.getWorkspaceDir( currentProjectLocation.toFile() );

                if ( workspaceDir == null || !workspaceDir.exists() )
                {
                    return;
                }
                String[] folders = LiferayWorkspaceUtil.getLiferayWorkspaceProjectWarsDirs( workspaceDir.getAbsolutePath() );
    
                if( folders != null )
                {
                    boolean appedndWarFolder = false;
                    IPath projectLocation = PathBridge.create( currentProjectLocation );
                    for ( String folder : folders )
                    {
                        if ( projectLocation.lastSegment().endsWith( folder ) )
                        {
                            appedndWarFolder = true;
                            break;
                        }
                    }
                    
                    if ( appedndWarFolder == true )
                    {
                        newLocationBase = PathBridge.create( projectLocation );
                    }
                    else
                    {
                        newLocationBase = PathBridge.create( projectLocation.append( folders[0] ) );
                    }
                }
                else
                {
                    newLocationBase = PathBridge.create( CoreUtil.getWorkspaceRoot().getLocation() );
                }
            }
        }

        if( newLocationBase != null )
        {
            op.setLocation( newLocationBase );
        }
    }
}
