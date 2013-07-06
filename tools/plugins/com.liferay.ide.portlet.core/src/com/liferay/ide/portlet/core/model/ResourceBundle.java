/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintanence
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.portlet.core.model.internal.ResourceBundleValueBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/elcl16/resources_16x16.gif" )
public interface ResourceBundle extends Element
{

    ElementType TYPE = new ElementType( ResourceBundle.class );

    // *** ResourceBundle ***
    @Type( base = Path.class )
    @Service( impl = GenericResourceBundlePathService.class )
    @FileExtensions( expr = "properties" )
    @ValidFileSystemResourceType( FileSystemResourceType.FILE )
    @XmlBinding( path = "resource-bundle" )
    @DependsOn( { "/Portlets/SupportedLocales" } )
    @CustomXmlValueBinding( impl = ResourceBundleValueBinding.class, params = { "resource-bundle" } )
    ValueProperty PROP_RESOURCE_BUNDLE = new ValueProperty( TYPE, "ResourceBundle" ); //$NON-NLS-1$

    Value<Path> getResourceBundle();

    void setResourceBundle( String value );

    void setResourceBundle( Path value );

}
