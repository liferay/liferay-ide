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

package com.liferay.ide.project.core.modules.fragment;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Joye Luo
 */
public class ModuleFragmentProjectGroupIdDefaultValueService extends DefaultValueService
{
    private Listener listener;

    @Override
    protected String compute()
    {
        String groupId = null;

        final Path location = op().getLocation().content();
        final NewModuleFragmentOp op = op();

        if( location != null )
        {
            final String parentProjectLocation = location.toOSString();
            final IPath parentProjectOsPath = org.eclipse.core.runtime.Path.fromOSString( parentProjectLocation );
            final String projectName = op().getProjectName().content();

            groupId = NewModuleFragmentOpMethods.getMavenParentPomGroupId( op, projectName, parentProjectOsPath );
        }

        if( groupId == null )
        {
            groupId = getDefaultMavenGroupId();

            if( CoreUtil.isNullOrEmpty( groupId ) )
            {
                groupId = op.getProjectName().content();
            }
        }

        return groupId;
    }

    @Override
    public void dispose()
    {
        if( this.listener != null && op() != null && !op().disposed() )
        {
            op().getProjectName().detach( this.listener );
            op().getProjectName().attach( this.listener );

            this.listener = null;
        }

        super.dispose();
    }

    private String getDefaultMavenGroupId()
    {
        final IScopeContext[] prefContexts = { DefaultScope.INSTANCE, InstanceScope.INSTANCE };
        final String defaultMavenGroupId = Platform.getPreferencesService().getString(
            ProjectCore.PLUGIN_ID, ProjectCore.PREF_DEFAULT_MODULE_PROJECT_MAVEN_GROUPID, null, prefContexts );
        return defaultMavenGroupId;
    }

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getLocation().attach( this.listener );
        op().getProjectName().attach( this.listener );
    }

    private NewModuleFragmentOp op()
    {
        return context( NewModuleFragmentOp.class );
    }

}
