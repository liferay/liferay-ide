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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.remote.AbstractRemoteServerPublisher;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public IPath publishModuleFull( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        IPath deployPath = LiferayServerCore.getTempLocation( "direct-deploy", StringPool.EMPTY ); //$NON-NLS-1$
        File warFile = deployPath.append( project.getName() + ".war" ).toFile(); //$NON-NLS-1$
        warFile.getParentFile().mkdirs();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put( ISDKConstants.PROPERTY_AUTO_DEPLOY_UNPACK_WAR, "false" ); //$NON-NLS-1$

        final ILiferayRuntime runtime = ServerUtil.getLiferayRuntime( project );

        final String appServerDeployDirProp =
            ServerUtil.getAppServerPropertyKey( ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, runtime );

        properties.put( appServerDeployDirProp, deployPath.toOSString() );

        properties.put( ISDKConstants.PROPERTY_PLUGIN_FILE, warFile.getAbsolutePath() );

        // IDE-1073 LPS-37923
        properties.put( ISDKConstants.PROPERTY_PLUGIN_FILE_DEFAULT, warFile.getAbsolutePath() );

        Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

        IStatus directDeployStatus =
            sdk.war( project, properties, true, appServerProperties, new String[] { "-Duser.timezone=GMT" }, monitor ); //$NON-NLS-1$

        if( !directDeployStatus.isOK() || ( !warFile.exists() ) )
        {
            throw new CoreException( directDeployStatus );
        }

        return new Path(warFile.getAbsolutePath());
    }

}
