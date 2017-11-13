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

package com.liferay.ide.project.core.upgrade;

import com.liferay.ide.project.core.upgrade.service.CheckSDKLocationDerivedValueService;
import com.liferay.ide.project.core.upgrade.service.LayoutPossibleValuesService;
import com.liferay.ide.project.core.upgrade.service.LiferayServerNameDefaultValueService;
import com.liferay.ide.project.core.upgrade.service.LiferayServerNamePossibleValuesService;
import com.liferay.ide.project.core.upgrade.service.LiferayServerNameValidationService;
import com.liferay.ide.project.core.upgrade.service.SdkLocationDefaultValueService;
import com.liferay.ide.project.core.upgrade.service.SdkLocationValidationService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Terry Jia
 */
@XmlBinding(path = "CodeUpgrade")
public interface CodeUpgradeOp extends Element {

	public ElementType TYPE = new ElementType(CodeUpgradeOp.class);

	public Value<Boolean> getConfirm();

	public Value<Boolean> getHasExt();

	public Value<Boolean> getHasHook();

	public Value<Boolean> getHasLayout();

	public Value<Boolean> getHasPortlet();

	public Value<Boolean> getHasServiceBuilder();

	public Value<Boolean> getHasTheme();

	public Value<Boolean> getHasWeb();

	public Value<String> getLayout();

	public Value<String> getLiferay62ServerLocation();

	public Value<String> getLiferayServerName();

	public Value<Path> getNewLocation();

	public Value<String> getProjectName();

	public Value<Path> getSdkLocation();

	public void setConfirm(Boolean confirm);

	public void setConfirm(String confirm);

	public void setHasExt(Boolean hasExt);

	public void setHasExt(String hasExt);

	public void setHasHook(Boolean hasHook);

	public void setHasHook(String hasHook);

	public void setHasLayout(Boolean hasLayout);

	public void setHasLayout(String hasLayout);

	public void setHasPortlet(Boolean hasPortlet);

	public void setHasPortlet(String hasPortlet);

	public void setHasServiceBuilder(Boolean hasServiceBuilder);

	public void setHasServiceBuilder(String hasServiceBuilder);

	public void setHasTheme(Boolean hasTheme);

	public void setHasTheme(String hasTheme);

	public void setHasWeb(Boolean hasWeb);

	public void setHasWeb(String hasWeb);

	public void setLayout(String layout);

	public void setLiferay62ServerLocation(String value);

	public void setLiferayServerName(String value);

	public void setNewLocation(Path newLocation);

	public void setNewLocation(String newLocation);

	public void setProjectName(String projectName);

	public void setSdkLocation(Path sdkLocation);

	public void setSdkLocation(String sdkLocation);

	@DefaultValue(text = "false")
	@Label(standard = "Yes,I am confirm")
	@Type(base = Boolean.class)
	public ValueProperty PROP_CONFIRM = new ValueProperty(TYPE, "Confirm");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	@XmlBinding(path = "HasExt")
	public ValueProperty PROP_HAS_EXT = new ValueProperty(TYPE, "HasExt");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	@XmlBinding(path = "HasHook")
	public ValueProperty PROP_HAS_HOOK = new ValueProperty(TYPE, "HasHook");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	@XmlBinding(path = "HasLayout")
	public ValueProperty PROP_HAS_LAYOUT = new ValueProperty(TYPE, "HasLayout");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	@XmlBinding(path = "HasPortlet")
	public ValueProperty PROP_HAS_PORTLET = new ValueProperty(TYPE, "HasPortlet");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	@XmlBinding(path = "HasServiceBuilder")
	public ValueProperty PROP_HAS_SERVICE_BUILDER = new ValueProperty(TYPE, "HasServiceBuilder");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	@XmlBinding(path = "HasTheme")
	public ValueProperty PROP_HAS_THEME = new ValueProperty(TYPE, "HasTheme");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	@XmlBinding(path = "HasWeb")
	public ValueProperty PROP_HAS_WEB = new ValueProperty(TYPE, "HasWeb");

	@DefaultValue(text = "Use plugin sdk in liferay workspace")
	@Service(impl = LayoutPossibleValuesService.class)
	public ValueProperty PROP_LAYOUT = new ValueProperty(TYPE, "Layout");

	@Derived
	@Service(impl = CheckSDKLocationDerivedValueService.class)
	public ValueProperty PROP_LIFERAY_62SERVER_LOCATION = new ValueProperty(TYPE, "Liferay62ServerLocation");

	@Required
	@Service(impl = LiferayServerNameDefaultValueService.class)
	@Service(impl = LiferayServerNamePossibleValuesService.class)
	@Service(impl = LiferayServerNameValidationService.class)
	@XmlBinding(path = "ServerName")
	public ValueProperty PROP_LIFERAY_SERVER_NAME = new ValueProperty(TYPE, "LiferayServerName");

	@Required
	@XmlBinding(path = "ProjectName")
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, "ProjectName");

	@AbsolutePath
	@Label(standard = "SDK Location")
	@Required
	@Service(impl = SdkLocationDefaultValueService.class)
	@Service(impl = SdkLocationValidationService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	@XmlBinding(path = "SdkLocation")
	public ValueProperty PROP_SDK_LOCATION = new ValueProperty(TYPE, "SdkLocation");

	@AbsolutePath
	@Required
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	@XmlBinding(path = "NewLocation")
	public ValueProperty PROP_NewLOCATION = new ValueProperty(TYPE, "NewLocation");

}