/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.remote.AbstractRemoteServerPublisher;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class SDKProjectRemoteServerPublisher extends AbstractRemoteServerPublisher
{
    private SDK sdk;

    public SDKProjectRemoteServerPublisher( IProject project, SDK sdk )
    {
        super( project );
        this.sdk = sdk;
    }

    public IPath publishModuleFull( IProgressMonitor monitor ) throws CoreException
    {
        final IPath deployPath = LiferayServerCore.getTempLocation( "direct-deploy", StringPool.EMPTY ); //$NON-NLS-1$
        File warFile = deployPath.append( getProject().getName() + ".war" ).toFile(); //$NON-NLS-1$
        warFile.getParentFile().mkdirs();

        final Map<String, String> properties = new HashMap<String, String>();
        properties.put( ISDKConstants.PROPERTY_AUTO_DEPLOY_UNPACK_WAR, "false" ); //$NON-NLS-1$

        final ILiferayRuntime runtime = ServerUtil.getLiferayRuntime( getProject() );

        final String appServerDeployDirProp =
            ServerUtil.getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, runtime );

        properties.put( appServerDeployDirProp, deployPath.toOSString() );

        // IDE-1073 LPS-37923
        properties.put( ISDKConstants.PROPERTY_PLUGIN_FILE_DEFAULT, warFile.getAbsolutePath() );

        properties.put( ISDKConstants.PROPERTY_PLUGIN_FILE, warFile.getAbsolutePath() );

        final String fileTimeStamp = System.currentTimeMillis() + "";

        // IDE-1491
        properties.put( ISDKConstants.PROPERTY_LP_VERSION, fileTimeStamp );

        properties.put( ISDKConstants.PROPERTY_LP_VERSION_SUFFIX, ".0" );

        final Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( getProject() );

        final IStatus directDeployStatus =
            sdk.war(
                getProject(), properties, true, appServerProperties, new String[] { "-Duser.timezone=GMT" }, monitor );

        if( !directDeployStatus.isOK() || ( !warFile.exists() ) )
        {

            String pluginVersion = "1";

            final Path pluginPropertiesPath = new Path( "WEB-INF/liferay-plugin-package.properties" );
            IFile propertiesFile = CoreUtil.getDocrootFile( getProject(), pluginPropertiesPath.toOSString() );
            if( propertiesFile != null )
            {
                try
                {
                    if( propertiesFile.exists() )
                    {
                        PropertiesConfiguration pluginPackageProperties = new PropertiesConfiguration();
                        InputStream is = propertiesFile.getContents();
                        pluginPackageProperties.load( is );
                        pluginVersion = pluginPackageProperties.getString( "module-incremental-version" );
                        is.close();
                    }

                }
                catch(Exception e)
                {
                    LiferayCore.logError( "error reading module-incremtnal-version. ", e );
                }
            }

            warFile =
                sdk.getLocation().append( "dist" ).append(
                    getProject().getName() + "-" + fileTimeStamp + "." + pluginVersion + ".0" + ".war" ).toFile();

            if ( !warFile.exists() )
            {
                throw new CoreException( directDeployStatus );
            }

        }
        return new Path( warFile.getAbsolutePath() );
    }

}
