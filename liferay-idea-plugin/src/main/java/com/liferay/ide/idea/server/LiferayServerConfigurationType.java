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

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;

import com.liferay.ide.idea.util.LiferayIcons;

import org.jetbrains.annotations.NotNull;

/**
 * @author Terry Jia
 */
public class LiferayServerConfigurationType extends ConfigurationTypeBase implements ConfigurationType {

	public LiferayServerConfigurationType() {
		super(
			"LiferayServerConfiguration", "Liferay Server", "Run or Debug a Liferay Server", LiferayIcons.LIFERAY_ICON);

		addFactory(
			new ConfigurationFactoryEx<RunConfiguration>(this) {

				public RunConfiguration createTemplateConfiguration(Project project) {
					return new LiferayServerConfiguration(project, this, "");
				}

				@Override
				public void onNewConfigurationCreated(@NotNull RunConfiguration configuration) {
					LiferayServerConfiguration jarApplicationConfiguration = (LiferayServerConfiguration)configuration;

					if (StringUtil.isEmpty(jarApplicationConfiguration.getWorkingDirectory())) {
						Project project = configuration.getProject();

						String basePath = StringUtil.notNullize(project.getBasePath());

						String baseDir = FileUtil.toSystemIndependentName(basePath);

						jarApplicationConfiguration.setWorkingDirectory(baseDir);
					}
				}

			});
	}

}