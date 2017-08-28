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

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.*;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.liferay.ide.workspace.ui.util.LiferayWorkspaceUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Terry Jia
 */
public class LiferayBundleConfiguration extends LocatableConfigurationBase implements CommonJavaRunConfigurationParameters, SearchScopeProvidingRunProfile {

    private static final SkipDefaultValuesSerializationFilters SERIALIZATION_FILTERS = new SkipDefaultValuesSerializationFilters();
    private LiferayBundleConfiguration.LiferayBundleConfigurationBean bean = new LiferayBundleConfiguration.LiferayBundleConfigurationBean();
    private Map<String, String> envs = new LinkedHashMap<>();
    private JavaRunConfigurationModule configurationModule;

    public LiferayBundleConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);

        configurationModule = new JavaRunConfigurationModule(project, true);
        bean.liferayBundle = Paths.get(project.getBasePath(), LiferayWorkspaceUtil.getHomeDir(project.getBasePath())).toString();
        bean.vmParameters = "-Xmx1024m";
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        final SettingsEditorGroup<LiferayBundleConfiguration> group = new SettingsEditorGroup<>();

        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new LiferayBundleConfigurable(getProject()));
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<>());

        return group;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);

        JavaRunConfigurationExtensionManager.getInstance().readExternal(this, element);
        XmlSerializer.deserializeInto(bean, element);
        EnvironmentVariablesComponent.readExternal(element, getEnvs());

        configurationModule.readExternal(element);
    }

    @Override
    public RunConfiguration clone() {
        LiferayBundleConfiguration clone = (LiferayBundleConfiguration) super.clone();
        clone.envs = new LinkedHashMap<>(envs);
        clone.configurationModule = new JavaRunConfigurationModule(getProject(), true);
        clone.configurationModule.setModule(configurationModule.getModule());
        clone.bean = XmlSerializerUtil.createCopy(bean);
        return clone;
    }

    public void setModule(Module module) {
        configurationModule.setModule(module);
    }

    public Module getModule() {
        return configurationModule.getModule();
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().writeExternal(this, element);
        XmlSerializer.serializeInto(bean, element, SERIALIZATION_FILTERS);
        EnvironmentVariablesComponent.writeExternal(element, getEnvs());
        if (configurationModule.getModule() != null) {
            configurationModule.writeExternal(element);
        }
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        JavaParametersUtil.checkAlternativeJRE(this);

        ProgramParametersUtil.checkWorkingDirectoryExist(this, getProject(), null);

        File liferayHome = new File(getLiferayBundle());

        if (!liferayHome.exists()) {
            throw new RuntimeConfigurationWarning(
                    "Unable to detect liferay bundle from '" + liferayHome.toPath() +
                            "', you may need to run gradle task initBundle firstly");
        }

        JavaRunConfigurationExtensionManager.checkConfigurationIsValid(this);
    }

    @NotNull
    public Module[] getModules() {
        Module module = configurationModule.getModule();
        return module != null ? new Module[]{module} : Module.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public GlobalSearchScope getSearchScope() {
        return SearchScopeProvider.createSearchScope(getModules());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new LiferayBundleCommandLineState(this, environment);
    }

    @Override
    public void setVMParameters(String value) {
        bean.vmParameters = value;
    }

    @Override
    public String getVMParameters() {
        return bean.vmParameters;
    }

    @Override
    public boolean isAlternativeJrePathEnabled() {
        return bean.ALTERNATIVE_JRE_PATH_ENABLED;
    }

    @Override
    public void setAlternativeJrePathEnabled(boolean enabled) {
        bean.ALTERNATIVE_JRE_PATH_ENABLED = enabled;
    }

    @Nullable
    @Override
    public String getAlternativeJrePath() {
        return bean.ALTERNATIVE_JRE_PATH;
    }

    @Override
    public void setAlternativeJrePath(String path) {
        bean.ALTERNATIVE_JRE_PATH = path;
    }

    @Nullable
    @Override
    public String getRunClass() {
        return null;
    }

    @Nullable
    @Override
    public String getPackage() {
        return null;
    }

    @Override
    public void setProgramParameters(@Nullable String value) {
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return null;
    }

    @Override
    public void setWorkingDirectory(@Nullable String value) {
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return null;
    }

    @Override
    public void setEnvs(@NotNull Map<String, String> envs) {
        this.envs.clear();
        this.envs.putAll(envs);
    }

    public String getLiferayBundle() {
        return bean.liferayBundle;
    }

    public void setLiferayBundle(String liferayBundle) {
        bean.liferayBundle = liferayBundle;
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return envs;
    }

    @Override
    public void setPassParentEnvs(boolean passParentEnvs) {
        bean.PASS_PARENT_ENVS = passParentEnvs;
    }

    @Override
    public boolean isPassParentEnvs() {
        return bean.PASS_PARENT_ENVS;
    }

    private static class LiferayBundleConfigurationBean {
        public String liferayBundle = "";
        public String vmParameters = "";
        public boolean ALTERNATIVE_JRE_PATH_ENABLED;
        public String ALTERNATIVE_JRE_PATH = "";
        public boolean PASS_PARENT_ENVS = true;
    }

}
