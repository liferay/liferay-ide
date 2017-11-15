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

package com.liferay.ide.portlet.core.model.lfrportlet.common;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.workspace.WorkspaceRelativePath;

/**
 * @author kamesh
 */
@GenerateImpl
public interface IWebResource extends Element {

	public ElementType TYPE = new ElementType(IWebResource.class);

	// *** WebResource ***

	@Type(base = Path.class)
	@Label(standard = "Resource Path")
	@ValidFileSystemResourceType(FileSystemResourceType.FILE)
	@FileExtensions(expr = "css,js,png,jpg,gif,bmp")
	@MustExist
	@WorkspaceRelativePath
	@CustomXmlValueBinding(impl = TextNodeValueBinding.class)
	public ValueProperty PROP_WEB_RESOURCE = new ValueProperty(TYPE, "WebResource");

	public Value<Path> getWebResource();

	public void setWebResource(String value);

	public void setWebResource(Path value);
}