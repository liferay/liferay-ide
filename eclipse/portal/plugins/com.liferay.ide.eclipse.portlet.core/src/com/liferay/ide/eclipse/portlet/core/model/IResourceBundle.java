/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Inc., All rights reserved.
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
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.ProjectRelativePathService;
import com.liferay.ide.eclipse.portlet.core.model.internal.ResourceBundleValidationService;
import com.liferay.ide.eclipse.portlet.core.model.internal.ResourceBundleValueBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/resource_bundle.png" )
public interface IResourceBundle extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IResourceBundle.class );

	// *** ResourceBundle ***
	@Type( base = Path.class )
	@Services( value = { @Service( impl = ProjectRelativePathService.class ),
		@Service( impl = ResourceBundleValidationService.class ) } )
	@FileExtensions( expr = "properties" )
	@CustomXmlValueBinding( impl = ResourceBundleValueBinding.class )
	ValueProperty PROP_RESOURCE_BUNDLE = new ValueProperty( TYPE, "ResourceBundle" );

	Value<Path> getResourceBundle();

	void setResourceBundle( String value );

	void setResourceBundle( Path value );

}
