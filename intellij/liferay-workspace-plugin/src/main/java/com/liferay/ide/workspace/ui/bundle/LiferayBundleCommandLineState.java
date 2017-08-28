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

package com.liferay.ide.workspace.ui.bundle;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.application.BaseJavaApplicationCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
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

        final String jreHome = getConfiguration().isAlternativeJrePathEnabled() ? getConfiguration().getAlternativeJrePath() : null;

        params.setJdk(JavaParametersUtil.createProjectJdk(getConfiguration().getProject(), jreHome));

        final String tomcat = getConfiguration().getLiferayBundle() + "/tomcat-8.0.32";

        params.getClassPath().add(new File(tomcat + "/bin/tomcat-juli.jar"));
        params.getClassPath().add(new File(tomcat + "/bin/bootstrap.jar"));
        params.getClassPath().add(new File(tomcat + "/bin/commons-daemon.jar"));

        params.setMainClass("org.apache.catalina.startup.Bootstrap");

        params.getVMParametersList().addParametersString(getConfiguration().getVMParameters());

        params.getVMParametersList().add("-Dcatalina.base=" + tomcat);
        params.getVMParametersList().add("-Dcatalina.home=" + tomcat);
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote");
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote.authenticate=false");
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote.port=8099");
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote.ssl=false");
        params.getVMParametersList().add("-Dfile.encoding=UTF8");
        params.getVMParametersList().add("-Djava.endorsed.dirs=" + tomcat + "/endorsed");
        params.getVMParametersList().add("-Djava.io.tmpdir=" + tomcat + "/temp");
        params.getVMParametersList().add("-Djava.net.preferIPv4Stack=true");
        params.getVMParametersList().add("-Djava.util.logging.config.file=" + tomcat + "/conf/logging.properties");
        params.getVMParametersList().add("-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager");
        params.getVMParametersList().add("-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false");

        setupJavaParameters(params);

        return params;
    }

}
