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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.modules.ModuleProjectNameValidationService;
import com.liferay.ide.project.core.service.CommonProjectLocationInitialValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionPossibleValuesService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Terry Jia
 * @author Seiphon Wang
 */
public interface NewModuleFragmentOp extends BaseModuleOp {

	public ElementType TYPE = new ElementType(NewModuleFragmentOp.class);

	@DelegateImplementation(NewModuleFragmentOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getArtifactVersion();

	public Value<String> getGroupId();

	public Value<String> getHostOsgiBundle();

	public Value<String> getLiferayRuntimeName();

	public Value<String> getLiferayVersion();

	public Value<String> getLpkgName();

	public ElementList<OverrideFilePath> getOverrideFiles();

	public void setArtifactVersion(String value);

	public void setGroupId(String value);

	public void setHostOsgiBundle(String value);

	public void setLiferayRuntimeName(String value);

	public void setLiferayVersion(String value);

	public void setLpkgName(String value);

	@Label(standard = "artifact version")
	@Service(impl = ModuleFragmentProjectArtifactVersionDefaultValueService.class)
	public ValueProperty PROP_ARTIFACT_VERSION = new ValueProperty(TYPE, "ArtifactVersion");

	@Label(standard = "group id")
	@Service(impl = ModuleFragmentProjectGroupIdDefaultValueService.class)
	@Service(impl = ModuleFragmentProjectGroupIdValidationService.class)
	@Whitespace(trim = false)
	public ValueProperty PROP_GROUP_ID = new ValueProperty(TYPE, "GroupId");

	@DefaultValue(text = "")
	@Label(standard = "Host OSGi Bundle")
	@Listeners(OSGiBundleListener.class)
	@Required
	@Service(impl = HostOSGiBundlePossibleValuesService.class)
	public ValueProperty PROP_HOST_OSGI_BUNDLE = new ValueProperty(TYPE, "HostOsgiBundle");

	@Required
	@Services(
		{
			@Service(impl = LiferayRuntimeNamePossibleValuesService.class),
			@Service(impl = LiferayRuntimeNameDefaultValueService.class),
			@Service(impl = LiferayRuntimeNameValidationService.class)
		}
	)
	public ValueProperty PROP_LIFERAY_RUNTIME_NAME = new ValueProperty(TYPE, "LiferayRuntimeName");

	@Label(standard = "liferay version")
	@Service(impl = FragmentProjectLiferayVersionDefaultValueService.class)
	@Service(impl = TargetLiferayVersionPossibleValuesService.class)
	@Service(impl = FragmentProjectLiferayVersionValidationService.class)
	public ValueProperty PROP_LIFERAY_VERSION = new ValueProperty(TYPE, "LiferayVersion");

	@Service(impl = CommonProjectLocationInitialValueService.class)
	@Service(impl = FragmentProjectLocationValidationService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_LOCATION);

	public ValueProperty PROP_LPKG_NAME = new ValueProperty(TYPE, "LpkgName");

	@Label(standard = "Overridden files")
	@Type(base = OverrideFilePath.class)
	public ListProperty PROP_OVERRIDE_FILES = new ListProperty(TYPE, "OverrideFiles");

	@Listeners(FragmentProjectNameListener.class)
	@Service(impl = ModuleProjectNameValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_NAME);

	@Label(standard = "build type")
	@Listeners(FragmentProjectNameListener.class)
	@Service(impl = FragmentProjectProviderDefaultValueService.class)
	@Service(impl = FragmentProjectProviderPossibleValuesService.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_PROVIDER);

	@Listeners(ModuleFragmentProjectUseDefaultLocationListener.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION);

}