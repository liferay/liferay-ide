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
package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourcePathComputer;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;


/**
 * @author Gregory Amerson
 */
public class PortalSourcePathComputerDelegate extends JavaSourcePathComputer
{
    public static final String ID = "com.liferay.ide.server.core.portal.sourcePathComputer";

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public ISourceContainer[] computeSourceContainers(
        ILaunchConfiguration configuration, IProgressMonitor monitor ) throws CoreException
    {
        final List<ISourceContainer> sourceContainers = new ArrayList<ISourceContainer>();

        final IServer server = ServerUtil.getServer( configuration );

        Stream.of(server.getModules()).map(
            module -> LiferayCore.create(module.getProject())
        ).filter(
            liferayProject -> liferayProject != null
        ).forEach( liferayProject -> {
            String projectName = liferayProject.getProject().getName();

            try {
                ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
                ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
                ILaunchConfigurationWorkingCopy sourceLookupConfig = launchConfigurationType.newInstance(null, configuration.getName());

                sourceLookupConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);
                sourceLookupConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);

                ISourceContainer[] computedSourceContainers = super.computeSourceContainers(sourceLookupConfig, monitor);

                Stream.of(computedSourceContainers).filter(
                    computedSourceContainer -> !sourceContainers.contains(computedSourceContainer)
                ).forEach(
                    sourceContainers::add
                );
            } catch (CoreException e) {
            }
        });

        // TODO at least add a source container for the Liferay Target Platform
        return sourceContainers.toArray(new ISourceContainer[0]);
    }


}
