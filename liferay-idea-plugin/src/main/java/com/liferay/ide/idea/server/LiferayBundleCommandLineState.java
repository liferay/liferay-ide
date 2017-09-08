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

package com.liferay.ide.idea.server;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.application.BaseJavaApplicationCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.util.PathsList;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Terry Jia
 */
public class LiferayBundleCommandLineState extends BaseJavaApplicationCommandLineState<LiferayBundleConfiguration> {

    public LiferayBundleCommandLineState(@NotNull final LiferayBundleConfiguration configuration, final ExecutionEnvironment environment) {
        super(environment, configuration);
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters params = new JavaParameters();

        LiferayBundleConfiguration configuration = getConfiguration();

		final String jreHome = configuration.isAlternativeJrePathEnabled() ? configuration.getAlternativeJrePath() : null;

        params.setJdk(JavaParametersUtil.createProjectJdk(configuration.getProject(), jreHome));

        final String tomcat = configuration.getLiferayBundle() + "/tomcat-8.0.32";

        PathsList classPath = params.getClassPath();

		classPath.add(new File(tomcat + "/bin/tomcat-juli.jar"));
        classPath.add(new File(tomcat + "/bin/bootstrap.jar"));
        classPath.add(new File(tomcat + "/bin/commons-daemon.jar"));

        params.setMainClass("org.apache.catalina.startup.Bootstrap");

        ParametersList vmParametersList = params.getVMParametersList();

		vmParametersList.addParametersString(configuration.getVMParameters());

        vmParametersList.add("-Dcatalina.base=" + tomcat);
        vmParametersList.add("-Dcatalina.home=" + tomcat);
        vmParametersList.add("-Dcom.sun.management.jmxremote");
        vmParametersList.add("-Dcom.sun.management.jmxremote.authenticate=false");
        vmParametersList.add("-Dcom.sun.management.jmxremote.port=8099");
        vmParametersList.add("-Dcom.sun.management.jmxremote.ssl=false");
        vmParametersList.add("-Dfile.encoding=UTF8");
        vmParametersList.add("-Djava.endorsed.dirs=" + tomcat + "/endorsed");
        vmParametersList.add("-Djava.io.tmpdir=" + tomcat + "/temp");
        vmParametersList.add("-Djava.net.preferIPv4Stack=true");
        vmParametersList.add("-Djava.util.logging.config.file=" + tomcat + "/conf/logging.properties");
        vmParametersList.add("-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager");
        vmParametersList.add("-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false");

        setupJavaParameters(params);

        return params;
    }

}
