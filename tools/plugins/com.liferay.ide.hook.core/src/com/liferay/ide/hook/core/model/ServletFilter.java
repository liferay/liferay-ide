/*******************************************************************************
 *  Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 *   This library is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU Lesser General Public License as published by the Free
 *   Software Foundation; either version 2.1 of the License, or (at your option)
 *   any later version.
 *
 *   This library is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *   details.
 *
 *   Contributors:
 *          Kamesh Sampath - initial implementation
 *          Gregory Amerson - IDE-355
 *******************************************************************************/

package com.liferay.ide.hook.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/elcl16/filter_16x16.gif" )
public interface ServletFilter extends Element
{

    ElementType TYPE = new ElementType( ServletFilter.class );

    // *** Servlet Filter Name ***

    @Label( standard = "Servlet Filter Name" )
    @XmlBinding( path = "servlet-filter-name" )
    @Required
    ValueProperty PROP_SERVLET_FILTER_NAME = new ValueProperty( TYPE, "ServletFilterName" ); //$NON-NLS-1$

    Value<String> getServletFilterName();

    void setServletFilterName( String value );

    // ** ServletFilterImpl
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "Servlet Filter Implementation" )
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "javax.servlet.Filter" } )
    @MustExist
    @Required
    @XmlBinding( path = "servlet-filter-impl" )
    ValueProperty PROP_SERVLET_FILTER_IMPL = new ValueProperty( TYPE, "ServletFilterImpl" ); //$NON-NLS-1$

    ReferenceValue<JavaTypeName, JavaType> getServletFilterImpl();

    void setServletFilterImpl( String value );

    void setServletFilterImpl( JavaTypeName value );

    // *** InitParams ***

    @Type( base = Param.class )
    @Label( standard = "Init Params" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "init-param", type = Param.class ) } )
    ListProperty PROP_INIT_PARAMS = new ListProperty( TYPE, "InitParams" ); //$NON-NLS-1$

    ElementList<Param> getInitParams();

}
