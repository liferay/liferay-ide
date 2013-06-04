/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Greg Amerson
 * @author kamesh.sampath [IDE-450]
 */
@SuppressWarnings( "restriction" )
public class PortletPluginFacetInstall extends PluginFacetInstall
{

    /*
     * IDE-815 Now with contributed portlet_2_0 uri we no longer need to copy TLD to user's project
     */
    /*
    public static void addLiferayPortletTldToWebXML( final IProject project )
    {
        ProjectUtil.addTldToWebXml( project, "http://java.sun.com/portlet_2_0", "/WEB-INF/tld/liferay-portlet.tld" ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected void copyPortletTLD() throws CoreException
    {
        IPath portalDir = getPortalDir();

        if( portalDir == null )
        {
            return;
        }

        IPath portletTld = portalDir.append( "WEB-INF/tld/liferay-portlet.tld" ); //$NON-NLS-1$

        if( portletTld.toFile().exists() )
        {
            IFolder tldFolder = getWebRootFolder().getFolder( "WEB-INF/tld" ); //$NON-NLS-1$

            CoreUtil.prepareFolder( tldFolder );

            IFile tldFile = tldFolder.getFile( "liferay-portlet.tld" ); //$NON-NLS-1$

            if( !tldFile.exists() )
            {
                try
                {
                    tldFile.create( new FileInputStream( portletTld.toFile() ), true, null );
                }
                catch( FileNotFoundException e )
                {
                    throw new CoreException( LiferayProjectCore.createErrorStatus( e ) );
                }
            }
        }
    }
    */

    @Override
    public void execute( IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor )
        throws CoreException
    {
        super.execute( project, fv, config, monitor );

        IDataModel model = (IDataModel) config;

        IDataModel masterModel = (IDataModel) model.getProperty( FacetInstallDataModelProvider.MASTER_PROJECT_DM );

        if( masterModel != null && masterModel.getBooleanProperty( CREATE_PROJECT_OPERATION ) )
        {
            SDK sdk = getSDK();

            String portletName = this.masterModel.getStringProperty( PORTLET_NAME );
            // FIX IDE-450
            if( portletName.endsWith( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) )
            {
                portletName =
                    portletName.substring( 0, portletName.indexOf( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) );
            }
            // END FIX IDE-450
            String displayName = this.masterModel.getStringProperty( DISPLAY_NAME );

            // get the template delegate
            String portletFrameworkId = this.masterModel.getStringProperty( PORTLET_FRAMEWORK_ID );

            IPortletFrameworkWizardProvider portletFramework =
                LiferayProjectCore.getPortletFramework( portletFrameworkId );

            String frameworkName = portletFramework.getShortName();

            Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

            IPath newPortletPath =
                sdk.createNewPortletProject( portletName, displayName, frameworkName, appServerProperties );

            processNewFiles( newPortletPath.append( portletName + ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) );

            // cleanup portlet files
            FileUtil.deleteDir( newPortletPath.toFile(), true );

            try
            {
                this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
            }
            catch( Exception e )
            {
                LiferayProjectCore.logError( e );
            }

            if( masterModel.getBooleanProperty( PLUGIN_FRAGMENT_ENABLED ) )
            {
                final IDataModel fragmentModel = masterModel.getNestedModel( PLUGIN_FRAGMENT_DM );

                if( fragmentModel != null )
                {
                    // IDE-205 remove view.jsp
                    try
                    {
                        if( fragmentModel.getBooleanProperty( IPluginWizardFragmentProperties.REMOVE_EXISTING_ARTIFACTS ) )
                        {
                            // IDE-110 IDE-648
                            IVirtualFolder webappRoot = CoreUtil.getDocroot( this.project );

                            for( IContainer container : webappRoot.getUnderlyingFolders() )
                            {
                                if( container != null && container.exists() )
                                {
                                    IFile viewJsp = container.getFile( new Path( "view.jsp" ) ); //$NON-NLS-1$

                                    if( viewJsp.exists() )
                                    {
                                        viewJsp.delete( true, monitor );
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    catch( Exception ex )
                    {
                        LiferayProjectCore.logError( "Error deleting view.jsp", ex ); //$NON-NLS-1$
                    }
                }
            }
        }
        else if( shouldSetupDefaultOutputLocation() )
        {
            setupDefaultOutputLocation();
        }

        /*
         * IDE-815 Now with contributed portlet_2_0 uri we no longer need to copy TLD to user's project
         */

        /*
        // IDE-719  if we have a runtime 6.2.0 or greater don't perform the workarounds for taglib imports.
        ILiferayProject liferayProject = LiferayCore.create( project );

        if( liferayProject != null )
        {
            Version portalVersion = new Version( liferayProject.getPortalVersion() );

            if( CoreUtil.compareVersions( portalVersion, ILiferayConstants.V6_2_0 ) < 0 )
            {
                // modify the web.xml and add <jsp-config><taglib> for liferay tlds
                copyPortletTLD();

                PortletPluginFacetInstall.addLiferayPortletTldToWebXML( this.project );

                try
                {
                    IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( project );

                    // IDE-575
                    if( !( defaultDocroot.getFile( "WEB-INF/tld/liferay-aui.tld" ).exists() ) && //$NON-NLS-1$
                        defaultDocroot.getFile( "WEB-INF/tld/aui.tld" ).exists() ) //$NON-NLS-1$
                    {
                        ProjectUtil.addTldToWebXml( project, "http://liferay.com/tld/aui", "/WEB-INF/tld/aui.tld" ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                catch( Exception e1 )
                {
                    LiferayServerCore.logError( "Error trying to add aui.tld to web.xml", e1 ); //$NON-NLS-1$
                }
            }
        }
        */

        try
        {
            this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e1 )
        {
            LiferayProjectCore.logError( e1 );
        }

        // IDE-417 set a project preference for disabling JSP fragment validation
        try
        {
            IEclipsePreferences node =
                new ProjectScope( this.project ).getNode( JSPCorePlugin.getDefault().getBundle().getSymbolicName() );
            node.putBoolean( JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false );
            node.putBoolean( JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, true );
            node.flush();
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Could not store jsp fragment validation preference", e ); //$NON-NLS-1$
        }

        if( shouldConfigureDeploymentAssembly() )
        {
            // IDE-565
            configureDeploymentAssembly( IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER, DEFAULT_DEPLOY_PATH );
        }
    }

    @Override
    protected String getDefaultOutputLocation()
    {
        return IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER;
    }

}
