/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.util.SDKUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class PluginFacetValidator implements IFacetedProjectValidator
{

    public final static String MARKER_ID = "com.liferay.ide.project.core.facet.validator";

    public static final String PREFERENCE_NODE_QUALIFIER = ProjectCorePlugin.getDefault().getBundle().getSymbolicName();

    protected IPreferencesService fPreferencesService = Platform.getPreferencesService();

    @SuppressWarnings( "deprecation" )
    public void validate( IFacetedProject fproj ) throws CoreException
    {
        if( fproj == null )
        {
            return;
        }

        IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };

        ProjectScope projectScope = new ProjectScope( fproj.getProject() );

        boolean useProjectSettings =
            projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean( ProjectCorePlugin.USE_PROJECT_SETTINGS, false );

        if( useProjectSettings )
        {
            scopes = new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
        }

        // check for an SDK
        SDK projectSDK = null;

        try
        {
            projectSDK = SDKUtil.getSDK( fproj.getProject() );
        }
        catch( Exception e )
        {
            ProjectCorePlugin.logError( e );
        }

        if( projectSDK == null )
        {
            Object severity =
                getMessageSeverity( PREFERENCE_NODE_QUALIFIER, scopes, ValidationPreferences.SDK_NOT_VALID );

            if( severity == null )
            {
                return;
            }

            String msg = "No Liferay Plugin SDK configured on project " + fproj.getProject().getName();

            if( severity.equals( IMarker.SEVERITY_ERROR ) )
            {
                fproj.createErrorMarker( msg );
            }
            else if( severity.equals( IMarker.SEVERITY_WARNING ) )
            {
                fproj.createWarningMarker( msg );
            }

            return;
        }

        if( projectSDK != null )
        {
            IStatus status = projectSDK.validate();

            if( !status.isOK() )
            {
                fproj.createErrorMarker(
                    MARKER_ID + ".sdkError", "Configured Liferay Plugin SDK is invalid: " + status.getMessage() );
            }
        }

    }

    protected Integer getMessageSeverity( String qualifier, IScopeContext[] preferenceScopes, String key )
    {
        int sev = fPreferencesService.getInt( qualifier, key, IMessage.NORMAL_SEVERITY, preferenceScopes );

        switch( sev )
        {
            case ValidationMessage.ERROR:
                return new Integer( IMarker.SEVERITY_ERROR );

            case ValidationMessage.WARNING:
                return new Integer( IMarker.SEVERITY_WARNING );

            case ValidationMessage.INFORMATION:
                return new Integer( IMarker.SEVERITY_INFO );

            case ValidationMessage.IGNORE:
                return null;
        }

        return new Integer( IMarker.SEVERITY_WARNING );
    }

    protected abstract IProjectFacet getProjectFacet();

}
