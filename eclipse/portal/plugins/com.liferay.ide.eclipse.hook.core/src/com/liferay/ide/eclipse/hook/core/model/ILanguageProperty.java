/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 *    Gregory Amerson - IDE-355
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import com.liferay.ide.eclipse.hook.core.model.internal.ResourceBundleRelativePathService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@GenerateImpl
@Image( path = "images/elcl16/locale_16x16.gif" )
public interface ILanguageProperty extends IModelElement {

	ModelElementType TYPE = new ModelElementType( ILanguageProperty.class );
	// *** Value ***

	@Label( standard = "Language Properties" )
	@XmlBinding( path = "" )
	@Service( impl = ResourceBundleRelativePathService.class )
	@FileExtensions( expr = "properties" )
	@ValidFileSystemResourceType( FileSystemResourceType.FILE )
	ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );

	Value<String> getValue();

	void setValue( String value );
}
