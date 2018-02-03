/**
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
 */

package com.liferay.ide.idea.server;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.CommonJavaRunConfigurationParameters;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.execution.configurations.SearchScopeProvider;
import com.intellij.execution.configurations.SearchScopeProvidingRunProfile;
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

import com.liferay.ide.idea.util.LiferayWorkspaceUtil;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Terry Jia
 */
public class LiferayServerConfiguration
	extends LocatableConfigurationBase implements CommonJavaRunConfigurationParameters, SearchScopeProvidingRunProfile {

	public LiferayServerConfiguration(Project project, ConfigurationFactory factory, String name) {
		super(project, factory, name);

		_configurationModule = new JavaRunConfigurationModule(project, true);

		Path path = Paths.get(project.getBasePath(), LiferayWorkspaceUtil.getHomeDir(project.getBasePath()));

		_config.liferayBundle = path.toString();

		_config.vmParameters = "-Xmx1024m";
	}

	@Override
	public void checkConfiguration() throws RuntimeConfigurationException {
		JavaParametersUtil.checkAlternativeJRE(this);

		ProgramParametersUtil.checkWorkingDirectoryExist(this, getProject(), null);

		File liferayHome = new File(getLiferayBundle());

		if (!liferayHome.exists()) {
			throw new RuntimeConfigurationWarning(
				"Unable to detect liferay bundle from '" + liferayHome.toPath() +
					"', you need to run gradle task 'initBundle' first.");
		}

		JavaRunConfigurationExtensionManager.checkConfigurationIsValid(this);
	}

	@Override
	public RunConfiguration clone() {
		LiferayServerConfiguration clone = (LiferayServerConfiguration)super.clone();

		clone.setConfig(XmlSerializerUtil.createCopy(_config));

		JavaRunConfigurationModule configurationModule = new JavaRunConfigurationModule(getProject(), true);

		configurationModule.setModule(_configurationModule.getModule());

		clone.setConfigurationModule(configurationModule);

		clone.setEnvs(new LinkedHashMap<>(clone.getEnvs()));

		return clone;
	}

	@Nullable
	@Override
	public String getAlternativeJrePath() {
		return _config.alternativeJrePath;
	}

	@NotNull
	@Override
	public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
		SettingsEditorGroup<LiferayServerConfiguration> group = new SettingsEditorGroup<>();

		String title = ExecutionBundle.message("run.configuration.configuration.tab.title");

		group.addEditor(title, new LiferayServerConfigurable(getProject()));

		JavaRunConfigurationExtensionManager javaRunConfigurationExtensionManager =
			JavaRunConfigurationExtensionManager.getInstance();

		javaRunConfigurationExtensionManager.appendEditors(this, group);

		group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<>());

		return group;
	}

	@NotNull
	@Override
	public Map<String, String> getEnvs() {
		return _envs;
	}

	public String getLiferayBundle() {
		return _config.liferayBundle;
	}

	public Module getModule() {
		return _configurationModule.getModule();
	}

	@NotNull
	public Module[] getModules() {
		Module module = _configurationModule.getModule();

		if (module != null) {
			return new Module[] {module};
		}

		return Module.EMPTY_ARRAY;
	}

	@Nullable
	@Override
	public String getPackage() {
		return null;
	}

	@Nullable
	@Override
	public String getProgramParameters() {
		return null;
	}

	@Nullable
	@Override
	public String getRunClass() {
		return null;
	}

	@Nullable
	@Override
	public GlobalSearchScope getSearchScope() {
		return SearchScopeProvider.createSearchScope(getModules());
	}

	@Nullable
	@Override
	public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment)
		throws ExecutionException {

		return new LiferayServerCommandLineState(this, environment);
	}

	@Override
	public String getVMParameters() {
		return _config.vmParameters;
	}

	@Nullable
	@Override
	public String getWorkingDirectory() {
		return null;
	}

	@Override
	public boolean isAlternativeJrePathEnabled() {
		return _config.alternativeJrePathEnabled;
	}

	@Override
	public boolean isPassParentEnvs() {
		return _config.passParentEnvs;
	}

	@Override
	public void readExternal(Element element) throws InvalidDataException {
		super.readExternal(element);

		JavaRunConfigurationExtensionManager javaRunConfigurationExtensionManager =
			JavaRunConfigurationExtensionManager.getInstance();

		javaRunConfigurationExtensionManager.readExternal(this, element);

		XmlSerializer.deserializeInto(_config, element);
		EnvironmentVariablesComponent.readExternal(element, getEnvs());

		_configurationModule.readExternal(element);
	}

	@Override
	public void setAlternativeJrePath(String path) {
		_config.alternativeJrePath = path;
	}

	@Override
	public void setAlternativeJrePathEnabled(boolean enabled) {
		_config.alternativeJrePathEnabled = enabled;
	}

	public void setConfig(LiferayBundleConfig config) {
		_config = config;
	}

	public void setConfigurationModule(JavaRunConfigurationModule configurationModule) {
		_configurationModule = configurationModule;
	}

	@Override
	public void setEnvs(@NotNull Map<String, String> envs) {
		_envs.clear();
		_envs.putAll(envs);
	}

	public void setLiferayBundle(String liferayBundle) {
		_config.liferayBundle = liferayBundle;
	}

	public void setModule(Module module) {
		_configurationModule.setModule(module);
	}

	@Override
	public void setPassParentEnvs(boolean passParentEnvs) {
		_config.passParentEnvs = passParentEnvs;
	}

	@Override
	public void setProgramParameters(@Nullable String value) {
	}

	@Override
	public void setVMParameters(String value) {
		_config.vmParameters = value;
	}

	@Override
	public void setWorkingDirectory(@Nullable String value) {
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException {
		super.writeExternal(element);

		JavaRunConfigurationExtensionManager javaRunConfigurationExtensionManager =
			JavaRunConfigurationExtensionManager.getInstance();

		javaRunConfigurationExtensionManager.writeExternal(this, element);

		XmlSerializer.serializeInto(_config, element, new SkipDefaultValuesSerializationFilters());
		EnvironmentVariablesComponent.writeExternal(element, getEnvs());

		if (_configurationModule.getModule() != null) {
			_configurationModule.writeExternal(element);
		}
	}

	private LiferayBundleConfig _config = new LiferayBundleConfig();
	private JavaRunConfigurationModule _configurationModule;
	private Map<String, String> _envs = new LinkedHashMap<>();

	private static class LiferayBundleConfig {

		public String alternativeJrePath = "";
		public boolean alternativeJrePathEnabled;
		public String liferayBundle = "";
		public boolean passParentEnvs = true;
		public String vmParameters = "";

	}

}