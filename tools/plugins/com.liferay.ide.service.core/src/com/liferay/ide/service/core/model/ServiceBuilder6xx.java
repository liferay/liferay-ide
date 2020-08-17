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

package com.liferay.ide.service.core.model;

import com.liferay.ide.service.core.model.internal.ServiceBuilderDefaultValueService;
import com.liferay.ide.service.core.model.internal.ServiceBuilderRootElementController;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.VersionCompatibilityTarget;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@CustomXmlRootBinding(ServiceBuilderRootElementController.class)
@VersionCompatibilityTarget(version = "${ Version }", versioned = "Service Builder")
@XmlBinding(path = "service-builder")
public interface ServiceBuilder6xx extends ServiceBuilder {

	public ElementType TYPE = new ElementType(ServiceBuilder6xx.class);

	public Value<Version> getVersion();

	// *** Version ***

	public void setVersion(String value);

	public void setVersion(Version value);

	@Service(impl = ServiceBuilderDefaultValueService.class)
	@Type(base = Version.class)
	public ValueProperty PROP_VERSION = new ValueProperty(TYPE, "Version");

}