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

package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public interface LiferayUpgradeDataModel extends Element {

	public Value<Path> getBackupLocation();

	public ElementType TYPE = new ElementType(LiferayUpgradeDataModel.class);

	// *** SdkLocation ***

	public Value<Boolean> getBackupSdk();

	public Value<String> getBundleName();

	public Value<String> getBundleUrl();

	public Value<Path> getConvertedProjectLocation();

	// *** Layout ***

	public Value<Boolean> getConvertLiferayWorkspace();

	public Value<Boolean> getDownloadBundle();

	public Value<Boolean> getHasExt();

	// *** Liferay70ServerName ***

	public Value<Boolean> getHasHook();

	public Value<Boolean> getHasLayout();

	public Value<Boolean> getHasMavenProject();

	// *** Liferay62ServerLocation ***

	public Value<Boolean> getHasPortlet();

	public Value<Boolean> getHasServiceBuilder();

	public Value<Boolean> getHasTheme();

	// *** BundleName ***

	public Value<Boolean> getHasWeb();

	public Value<Boolean> getImportFinished();

	public Value<String> getLayout();

	// *** BundleUrl ***

	public Value<String> getLiferay62ServerLocation();

	public Value<String> getLiferay70ServerName();

	public Value<Path> getSdkLocation();

	// *** HasMavenProject ***

	public void setBackupLocation(Path backupLocation);

	public void setBackupLocation(String backupLocation);

	public void setBackupSdk(Boolean backupSdk);

	public void setBackupSdk(String backupSdk);

	// *** HasHook ***

	public void setBundleName(String bundleName);

	public void setBundleUrl(String bundleUrl);

	public void setConvertedProjectLocation(Path convertedProjectLocation);

	public void setConvertedProjectLocation(String convertedProjectLocation);

	// *** HasPortlet ***

	public void setConvertLiferayWorkspace(Boolean convertLiferayWorkspace);

	public void setConvertLiferayWorkspace(String convertLiferayWorkspace);

	public void setDownloadBundle(Boolean downloadBundle);

	public void setDownloadBundle(String downloadBundle);

	// *** HasTheme ***

	public void setHasExt(Boolean hasExt);

	public void setHasExt(String hasExt);

	public void setHasHook(Boolean hasHook);

	public void setHasHook(String hasHook);

	// *** HasExt ***

	public void setHasLayout(Boolean hasLayout);

	public void setHasLayout(String hasLayout);

	public void setHasMavenProject(Boolean hasMavenProject);

	public void setHasMavenProject(String hasMavenProject);

	// *** HasServiceBuilder ***

	public void setHasPortlet(Boolean hasPortlet);

	public void setHasPortlet(String hasPortlet);

	public void setHasServiceBuilder(Boolean hasServiceBuilder);

	public void setHasServiceBuilder(String hasServiceBuilder);

	// *** HasLayout ***

	public void setHasTheme(Boolean hasTheme);

	public void setHasTheme(String hasTheme);

	public void setHasWeb(Boolean hasWeb);

	public void setHasWeb(String hasWeb);

	// *** HasWeb ***

	public void setImportFinished(Boolean importFinished);

	public void setImportFinished(String importFinished);

	public void setLayout(String layout);

	public void setLiferay62ServerLocation(String value);

	// *** ConvertedProjectLocation ***

	public void setLiferay70ServerName(String value);

	public void setSdkLocation(Path sdkLocation);

	public void setSdkLocation(String sdkLocation);

	public String DEFAULT_BUNDLE_URL =
		"https://releases-cdn.liferay.com/portal/7.0.6-ga7/liferay-ce-portal-tomcat-7.0-ga7-20180507111753223.zip";

	// *** ConvertLiferayWorkspace ***

	@Service(impl = BackupLocationValidationService.class)
	@Type(base = Path.class)
	public ValueProperty PROP_BACKUP_LOCATION = new ValueProperty(TYPE, "BackupLocation");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_BACKUP_SDK = new ValueProperty(TYPE, "BackupSdk");

	@DefaultValue(text = "Liferay 7.x")
	@Service(impl = BundleNameValidationService.class)
	public ValueProperty PROP_BUNDLE_NAME = new ValueProperty(TYPE, "BundleName");

	@DefaultValue(text = DEFAULT_BUNDLE_URL)
	@Service(impl = BundleUrlValidationService.class)
	public ValueProperty PROP_BUNDLE_URL = new ValueProperty(TYPE, "BundleUrl");

	// *** DownloadBundle ***

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_CONVERT_LIFERAY_WORKSPACE = new ValueProperty(TYPE, "ConvertLiferayWorkspace");

	@Service(impl = ConvertedProjectLocationValidationService.class)
	@Type(base = Path.class)
	public ValueProperty PROP_CONVERTED_PROJECT_LOCATION = new ValueProperty(TYPE, "ConvertedProjectLocation");

	@DefaultValue(text = "true")
	@Type(base = Boolean.class)
	public ValueProperty PROP_DOWNLOAD_BUNDLE = new ValueProperty(TYPE, "downloadBundle");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_EXT = new ValueProperty(TYPE, "HasExt");

	// *** BackupSdk ***

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_HOOK = new ValueProperty(TYPE, "HasHook");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_LAYOUT = new ValueProperty(TYPE, "HasLayout");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_MAVEN_PROJECT = new ValueProperty(TYPE, "HasMavenProject");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_PORTLET = new ValueProperty(TYPE, "HasPortlet");

	// *** BackupLocation ***

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_SERVICE_BUILDER = new ValueProperty(TYPE, "HasServiceBuilder");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_THEME = new ValueProperty(TYPE, "HasTheme");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_WEB = new ValueProperty(TYPE, "HasWeb");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_IMPORT_FINISHED = new ValueProperty(TYPE, "ImportFinished");

	// *** ImportFinished ***

	public ValueProperty PROP_LAYOUT = new ValueProperty(TYPE, "Layout");

	public ValueProperty PROP_LIFERAY_62_SERVER_LOCATION = new ValueProperty(TYPE, "Liferay62ServerLocation");

	public ValueProperty PROP_LIFERAY_70_SERVER_NAME = new ValueProperty(TYPE, "Liferay70ServerName");

	@Service(impl = ProjectLocationValidationService.class)
	@Type(base = Path.class)
	public ValueProperty PROP_SDK_LOCATION = new ValueProperty(TYPE, "SdkLocation");

}