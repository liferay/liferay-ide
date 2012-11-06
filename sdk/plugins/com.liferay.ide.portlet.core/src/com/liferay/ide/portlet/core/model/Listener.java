/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 *    Gregory Amerson - IDE-405 - added image listener
 ******************************************************************************/

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
@Image( path = "images/obj16/portlet_class_obj.gif" )
public interface Listener extends IModelElement, Describeable, Displayable
{

    ModelElementType TYPE = new ModelElementType( Listener.class );

    // *** Implementation ***

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "javax.portlet.PortletURLGenerationListener" )
    @Label( standard = "Implementation", full = "Listener implementation class" )
    @Required
    @XmlBinding( path = "listener-class" )
    @Documentation( content = "The listener implementation class." )
    ValueProperty PROP_IMPLEMENTATION = new ValueProperty( TYPE, "Implementation" );

    ReferenceValue<JavaTypeName, JavaType> getImplementation();

    void setImplementation( String value );

    void setImplementation( JavaTypeName value );

}
