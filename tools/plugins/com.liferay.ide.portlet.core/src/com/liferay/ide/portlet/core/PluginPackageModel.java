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

package com.liferay.ide.portlet.core;

import com.liferay.ide.core.model.AbstractEditingModel;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.ModelChangedEvent;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PluginPackageModel extends AbstractEditingModel implements IPluginPackageModel {

	public PluginPackageModel(IFile file, IDocument document, boolean reconciling) {
		super(document, reconciling);

		try (InputStream is = file.getContents()) {
			pluginPackageProperties = new PluginPropertiesConfiguration();

			pluginPackageProperties.load(is);
		}
		catch (Exception e) {
			PortletCore.logError(e);
		}
	}

	public void addDependency(String propertyName, String value) {
		if (CoreUtil.isNullOrEmpty(value)) {
			return;
		}

		String existingDeps = pluginPackageProperties.getString(propertyName, StringPool.EMPTY);

		String[] existingValues = existingDeps.split(",");

		for (String existingValue : existingValues) {
			if (value.equals(existingValue)) {
				return;
			}
		}

		String newDeps = null;

		if (CoreUtil.isNullOrEmpty(existingDeps)) {
			newDeps = value;
		}
		else {
			newDeps = existingDeps + "," + value;
		}

		pluginPackageProperties.setProperty(propertyName, newDeps);

		flushProperties();

		fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.INSERT, newDeps.split(","), propertyName));
	}

	public void addPortalDependencyJar(String jar) {
		addDependency(PROPERTY_PORTAL_DEPENDENCY_JARS, jar);
	}

	public void addPortalDependencyTld(String tldFile) {
		addDependency(PROPERTY_PORTAL_DEPENDENCY_TLDS, tldFile);
	}

	public void addPortalDeployExcludeJar(String jar) {
		addDependency(PROPERTY_DEPLOY_EXCLUDE, jar);
	}

	public void addRequiredDeploymentContext(String context) {
		addDependency(PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS, context);
	}

	@Override
	public void adjustOffsets(IDocument document) throws CoreException {
	}

	public void dispose() {
		pluginPackageProperties = null;
	}

	public String getAuthor() {
		return getStringProperty(PROPERTY_AUTHOR);
	}

	public Boolean getBooleanProperty(String propertyName, boolean defaultVal) {
		if (pluginPackageProperties != null) {
			return pluginPackageProperties.getBoolean(propertyName, defaultVal);
		}

		return null;
	}

	public String getChangeLog() {
		return getStringProperty(PROPERTY_CHANGE_LOG);
	}

	public String getLicenses() {
		return getStringProperty(PROPERTY_LICENSES);
	}

	public String getLiferayVersions() {
		return getStringProperty(PROPERTY_LIFERAY_VERSIONS);
	}

	public String getLongDescription() {
		return getStringProperty(PROPERTY_LONG_DESCRIPTION);
	}

	public String getModuleGroupId() {
		return getStringProperty(PROPERTY_MODULE_GROUP_ID);
	}

	public String getModuleIncrementalVersion() {
		return getStringProperty(PROPERTY_MODULE_INCREMENTAL_VERSION);
	}

	public String getName() {
		return getStringProperty(PROPERTY_NAME);
	}

	public String getPageUrl() {
		return getStringProperty(PROPERTY_PAGE_URL);
	}

	public String[] getPortalDeloyExcludesJars() {
		String exludeJars = pluginPackageProperties.getString(PROPERTY_DEPLOY_EXCLUDE, null);

		if (exludeJars != null) {
			return exludeJars.split(",");
		}
		else {
			return new String[0];
		}
	}

	public String[] getPortalDependencyJars() {
		String portalJars = pluginPackageProperties.getString(PROPERTY_PORTAL_DEPENDENCY_JARS, null);

		if (portalJars != null) {
			return portalJars.split(",");
		}
		else {
			return new String[0];
		}
	}

	public String[] getPortalDependencyTlds() {
		String portalTlds = pluginPackageProperties.getString(PROPERTY_PORTAL_DEPENDENCY_TLDS, null);

		if (portalTlds != null) {
			return portalTlds.split(",");
		}
		else {
			return new String[0];
		}
	}

	public String[] getRequiredDeploymentContexts() {
		String contexts = pluginPackageProperties.getString(PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS, null);

		if (contexts != null) {
			return contexts.split(",");
		}
		else {
			return new String[0];
		}
	}

	public String getShortDescription() {
		return getStringProperty(PROPERTY_SHORT_DESCRIPTION);
	}

	public String getStringProperty(String propertyName) {
		if (pluginPackageProperties != null) {
			if (pluginPackageProperties.getProperty(propertyName) != null) {
				return pluginPackageProperties.getString(propertyName);
			}
		}

		return null;
	}

	public String getTags() {
		return getStringProperty(PROPERTY_TAGS);
	}

	public boolean isAdapterForType(Object type) {
		if ((type != null) && (INodeAdapter.class == type)) {
			return true;
		}

		return false;
	}

	public Boolean isSpeedFiltersEnabled() {
		return getBooleanProperty(PROPERTY_SPEED_FILTERS_ENABLED, true);
	}

	public void load(InputStream source, boolean outOfSync) throws CoreException {
		pluginPackageProperties.clear();

		try {
			pluginPackageProperties.load(source);

			flushProperties();

			fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.WORLD_CHANGED, null, null));
		}
		catch (ConfigurationException ce) {
			throw new CoreException(PortletCore.createErrorStatus(ce));
		}
	}

	public void removeDependency(String propertyName, String[] removedValues) {
		String portalDependencies = pluginPackageProperties.getString(propertyName, null);

		List<String> updatedValues = new ArrayList<>();

		String[] deps = portalDependencies.split(",");

		for (String dep : deps) {
			if (dep.startsWith("**/WEB-INF/lib/")) {
				dep = dep.substring(dep.lastIndexOf("/") + 1);
			}

			boolean shouldKeep = true;

			for (String removedValue : removedValues) {
				dep = dep.trim();

				if (dep.equals(removedValue.trim())) {
					shouldKeep = false;

					break;
				}
			}

			if (shouldKeep) {
				updatedValues.add(dep);
			}
		}

		if (ListUtil.isNotEmpty(updatedValues)) {
			pluginPackageProperties.setProperty(propertyName, StringPool.EMPTY);

			for (String updatedValue : updatedValues) {
				addDependency(propertyName, updatedValue);
			}
		}
		else {
			pluginPackageProperties.clearProperty(propertyName);
		}

		flushProperties();

		fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.REMOVE, updatedValues.toArray(), propertyName));
	}

	public void removePortalDependencyJars(String[] removedJars) {
		removeDependency(PROPERTY_PORTAL_DEPENDENCY_JARS, removedJars);
	}

	public void removePortalDependencyTlds(String[] removedTlds) {
		removeDependency(PROPERTY_PORTAL_DEPENDENCY_TLDS, removedTlds);
	}

	public void removePortalDeployExcludeJar(String[] removedJars) {
		removeDependency(PROPERTY_DEPLOY_EXCLUDE, removedJars);
	}

	public void removeRequiredDeploymentContexts(String[] contexts) {
		removeDependency(PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS, contexts);
	}

	public void setAuthor(String author) {
		setProperty(PROPERTY_AUTHOR, author);
	}

	public void setChangeLog(String changeLog) {
		setProperty(PROPERTY_CHANGE_LOG, changeLog);
	}

	public void setLicenses(String licenses) {
		setProperty(PROPERTY_LICENSES, licenses);
	}

	public void setLiferayVersions(String liferayVersions) {
		setProperty(PROPERTY_LIFERAY_VERSIONS, liferayVersions);
	}

	public void setLongDescription(String desc) {
		setProperty(PROPERTY_LONG_DESCRIPTION, desc);
	}

	public void setModuleGroupId(String moduleGroupId) {
		setProperty(PROPERTY_MODULE_GROUP_ID, moduleGroupId);
	}

	public void setModuleIncrementalVersion(String moduleIncrementalVersion) {
		setProperty(PROPERTY_MODULE_INCREMENTAL_VERSION, moduleIncrementalVersion);
	}

	public void setName(String name) {
		setProperty(PROPERTY_NAME, name);
	}

	public void setPageUrl(String pageUrl) {
		setProperty(PROPERTY_PAGE_URL, pageUrl);
	}

	public void setProperty(String propertyName, Object propertyValue) {
		Object oldValue = pluginPackageProperties.getProperty(propertyName);

		pluginPackageProperties.setProperty(propertyName, propertyValue);

		flushProperties();

		fireModelChanged(new ModelChangedEvent(this, null, propertyName, oldValue, propertyValue));
	}

	public void setShortDescription(String desc) {
		setProperty(PROPERTY_SHORT_DESCRIPTION, desc);
	}

	public void setSpeedFiltersEnabled(boolean enabled) {
		Boolean oldValue = isSpeedFiltersEnabled();

		pluginPackageProperties.setProperty(PROPERTY_SPEED_FILTERS_ENABLED, enabled);

		flushProperties();

		fireModelChanged(new ModelChangedEvent(this, null, PROPERTY_SPEED_FILTERS_ENABLED, oldValue, enabled));
	}

	public void setTags(String tags) {
		setProperty(PROPERTY_TAGS, tags);
	}

	public void swapDependencies(String property, String dep1, String dep2) {
		String[] deps = null;
		String depsValue = pluginPackageProperties.getString(property, null);

		if (depsValue != null) {
			deps = depsValue.split(",");
		}
		else {
			deps = new String[0];
		}

		ArrayList<String> list = new ArrayList<>();

		Collections.addAll(list, deps);

		int index1 = list.indexOf(dep1);

		int index2 = list.indexOf(dep2);

		list.set(index2, dep1);

		list.set(index1, dep2);

		String[] newValues = list.toArray(new String[0]);

		StringBuffer buffer = new StringBuffer();

		for (String val : newValues) {
			buffer.append(val + ",");
		}

		String s = buffer.toString();

		String newValue = s.substring(0, buffer.length() - 1);

		pluginPackageProperties.setProperty(property, newValue);

		flushProperties();

		fireModelChanged(new ModelChangedEvent(this, null, property, depsValue, newValue));
	}

	public PropertiesConfiguration pluginPackageProperties;

	protected void flushProperties() {
		try (StringWriter output = new StringWriter();) {
			pluginPackageProperties.save(output);
			getDocument().set(output.toString());
		}
		catch (ConfigurationException | IOException e) {
		}
	}

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

}