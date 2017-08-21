package com.liferay.ide.workspace.ui.bundle;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.application.BaseJavaApplicationCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class LiferayBundleCommandLineState extends BaseJavaApplicationCommandLineState<LiferayBundleConfiguration> {
    public LiferayBundleCommandLineState(@NotNull final LiferayBundleConfiguration configuration, final ExecutionEnvironment environment) {
        super(environment, configuration);
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters params = new JavaParameters();
        final String jreHome = myConfiguration.isAlternativeJrePathEnabled() ? myConfiguration.getAlternativeJrePath() : null;
        params.setJdk(JavaParametersUtil.createProjectJdk(myConfiguration.getProject(), jreHome));
        params.getClassPath().add(new File("/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32/bin/tomcat-juli.jar"));
        params.getClassPath().add(new File("/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32/bin/bootstrap.jar"));
        params.getClassPath().add(new File("/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32/bin/commons-daemon.jar"));
        params.setMainClass("org.apache.catalina.startup.Bootstrap");
        params.getVMParametersList().add("-Xmx1024m");
        params.getVMParametersList().add("-Dcatalina.base=/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32");
        params.getVMParametersList().add("-Dcatalina.home=/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32");
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote");
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote.authenticate=false");
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote.port=8099");
        params.getVMParametersList().add("-Dcom.sun.management.jmxremote.ssl=false");
        params.getVMParametersList().add("-Dfile.encoding=UTF8");
        params.getVMParametersList().add("-Djava.endorsed.dirs=/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32/endorsed");
        params.getVMParametersList().add("-Djava.io.tmpdir=/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32/temp");
        params.getVMParametersList().add("-Djava.net.preferIPv4Stack=true");
        params.getVMParametersList().add("-Djava.util.logging.config.file=/Users/terry/work/liferay-portal/liferay-ce-portal-7.0-ga4/tomcat-8.0.32/conf/logging.properties");
        params.getVMParametersList().add("-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager");
        params.getVMParametersList().add("-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false");




        setupJavaParameters(params);
        params.setJarPath(FileUtil.toSystemDependentName(myConfiguration.getJarPath()));
        return params;
    }
}
