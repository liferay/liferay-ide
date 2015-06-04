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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.SDKProjectsImportOp30;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ValuePropertyContentEvent;


/**
 * @author Simon Jiang
 */
public class SDKImportProjectsLocationListener extends FilteredListener<ValuePropertyContentEvent>
{
    protected SDKProjectsImportOp30 op( PropertyContentEvent event )
    {
        return event.property().element().nearest( SDKProjectsImportOp30.class );
    }

    @Override
    protected void handleTypedEvent( ValuePropertyContentEvent event )
    {
        SDKProjectsImportOp30 op = op(event);

        if ( ( op.getLocation().content()!= null ) && !op.getLocation().content().isEmpty() )
        {
            ProjectRecord record = ProjectUtil.getProjectRecordForDir( op.getLocation().content().toPortableString() );

            if( record != null )
            {
                String projectName = record.getProjectName();
                IProject existingProject = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );

                if( existingProject != null )
                {
                    final String pluginType = ProjectUtil.getLiferayPluginType( op.getLocation().content().toPortableString() );

                    op.setPluginType( pluginType );

                    File projectDir = record.getProjectLocation().toFile();

                    SDK sdk = SDKUtil.getSDKFromProjectDir( projectDir );

                    final String sdkVersion = sdk.getVersion();         

                    op.setPluginType( pluginType );

                    op.setSdkVersion( sdkVersion );
                }
            }
            else
            {
                op.setPluginType( null );
                op.setSdkVersion( null );            
            }            
        }
    }
}