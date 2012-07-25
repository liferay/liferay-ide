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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.ISDKConstants;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.server.core.LiferayServerCorePlugin;
import com.liferay.ide.server.util.ServerUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Greg Amerson
 * @author kamesh.sampath [IDE-450]
 */
@SuppressWarnings( "restriction" )
public class PortletPluginFacetInstall extends PluginFacetInstall
{

    protected void copyPortletTLD() throws CoreException
    {
        IPath portalDir = getPortalDir();

        IPath portletTld = portalDir.append( "WEB-INF/tld/liferay-portlet.tld" );

        if( portletTld.toFile().exists() )
        {
            IFolder tldFolder = getWebRootFolder().getFolder( "WEB-INF/tld" );

            CoreUtil.prepareFolder( tldFolder );

            IFile tldFile = tldFolder.getFile( "liferay-portlet.tld" );

            if( !tldFile.exists() )
            {
                try
                {
                    tldFile.create( new FileInputStream( portletTld.toFile() ), true, null );
                }
                catch( FileNotFoundException e )
                {
                    throw new CoreException( ProjectCorePlugin.createErrorStatus( e ) );
                }
            }
        }
    }

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
                ProjectCorePlugin.getPortletFramework( portletFrameworkId );

            String frameworkName = portletFramework.getShortName();

            Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

            IPath newPortletPath =
                sdk.createNewPortletProject( portletName, displayName, frameworkName, appServerProperties );

            processNewFiles( newPortletPath.append( portletName + ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ), false );

            // cleanup portlet files
            FileUtil.deleteDir( newPortletPath.toFile(), true );

            this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

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
                            IFolder docroot = CoreUtil.getDocroot( this.project );
                            IFile viewJsp = docroot.getFile( "view.jsp" );
                            if( viewJsp.exists() )
                            {
                                viewJsp.delete( true, monitor );
                            }
                        }
                    }
                    catch( Exception ex )
                    {
                        ProjectCorePlugin.logError( "Error deleting view.jsp", ex );
                    }
                }
            }
        }
        else
        {
            setupDefaultOutputLocation();
        }

        // modify the web.xml and add <jsp-config><taglib> for liferay tlds
        copyPortletTLD();

        ProjectUtil.addLiferayPortletTldToWebXML( this.project );

        try
        {
            IFolder docroot = CoreUtil.getDocroot( project );

            // IDE-575
            if( !( docroot.getFile( "WEB-INF/tld/liferay-aui.tld" ).exists() ) &&
                docroot.getFile( "WEB-INF/tld/aui.tld" ).exists() )
            {
                ProjectUtil.addTldToWebXml( project, "http://liferay.com/tld/aui", "/WEB-INF/tld/aui.tld" );
            }
        }
        catch( Exception e1 )
        {
            LiferayServerCorePlugin.logError( "Error trying to add aui.tld to web.xml", e1 );
        }

        this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

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
            ProjectCorePlugin.logError( "Could not store jsp fragment validation preference", e );
        }

        // IDE-565
        configureDeploymentAssembly( IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER, DEFAULT_DEPLOY_PATH );
    }

    @Override
    protected String getDefaultOutputLocation()
    {
        return IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER;
    }

    // protected void overwriteProjectFromTemplate(IPath newProjectPath, IPortletFramework portletTemplate) {
    // String bundleId = portletTemplate.getBundleId();
    //
    // try {
    // URL url =
    // FileLocator.toFileURL(Platform.getBundle(bundleId).getEntry(portletTemplate.getRequiredSDKVersion()));
    // File templateZip = new File(url.getFile());
    //
    // if (templateZip.exists() && newProjectPath.toFile().isDirectory()) {
    // File newProjectDir = newProjectPath.toFile();
    // FileUtil.deleteDirContents(newProjectDir);
    //
    // ZipUtil.unzip(templateZip, newProjectPath.toFile());
    // }
    // }
    // catch (IOException e) {
    // ProjectCorePlugin.logError("Could not unzip project template from bundle.", e);
    // }
    // }

    // @SuppressWarnings("unchecked")
    // protected void configWebXML() {
    // WebArtifactEdit webArtifactEdit =
    // WebArtifactEdit.getWebArtifactEditForWrite(this.project);
    // int j2eeVersion = webArtifactEdit.getJ2EEVersion();
    // WebApp webApp = webArtifactEdit.getWebApp();
    // webApp.setFileList(null);
    // JSPConfig jspConfig = webApp.getJspConfig();
    // if (jspConfig == null && webApp.getVersionID() != 23) {
    // jspConfig = JspFactory.eINSTANCE.createJSPConfig();
    // }
    // TagLibRefType tagLibRefType = JspFactory.eINSTANCE.createTagLibRefType();
    // tagLibRefType.setTaglibURI("http://java.sun.com/portlet_2_0");
    // tagLibRefType.setTaglibLocation("/WEB-INF/tld/liferay-portlet.tld");
    // if (jspConfig != null) {
    // jspConfig.getTagLibs().add(tagLibRefType);
    // } else {
    // EList tagLibs = webApp.getTagLibs();
    // tagLibs.add(tagLibRefType);
    // }
    // if (jspConfig != null) {
    // webApp.setJspConfig(jspConfig);
    // }
    // webArtifactEdit.saveIfNecessary(null);
    // webArtifactEdit.dispose();
    // }

}
