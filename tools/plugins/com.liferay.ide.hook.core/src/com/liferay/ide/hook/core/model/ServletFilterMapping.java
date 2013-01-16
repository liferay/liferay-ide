/*******************************************************************************
 *  Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.hook.core.model.internal.BeforeAfterFilterNameBinding;
import com.liferay.ide.hook.core.model.internal.BeforeAfterFilterTypeBinding;
import com.liferay.ide.hook.core.model.internal.PortalFilterNamesPossibleValuesService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/filter_mapping_16x16.gif" )
public interface ServletFilterMapping extends IModelElement
{

    ModelElementType TYPE = new ModelElementType( ServletFilterMapping.class );

    // *** Servlet Filter Name ***

    @Label( standard = "Servlet Filter Name" )
    @XmlBinding( path = "servlet-filter-name" )
    @PossibleValues( property = "/ServletFilters/ServletFilterName" )
    ValueProperty PROP_SERVLET_FILTER_NAME = new ValueProperty( TYPE, "ServletFilterName" ); //$NON-NLS-1$

    Value<String> getServletFilterName();

    void setServletFilterName( String value );

    @Type( base = BeforeAfterFilterType.class )
    @DefaultValue( text = "before-filter" )
    @CustomXmlValueBinding( impl = BeforeAfterFilterTypeBinding.class )
    ValueProperty PROP_BEFORE_AFTER_FILTER_TYPE = new ValueProperty( TYPE, "BeforeAfterFilterType" ); //$NON-NLS-1$

    Value<BeforeAfterFilterType> getBeforeAfterFilterType();

    void setBeforeAfterFilterType( String value );

    void setBeforeAfterFilterType( BeforeAfterFilterType value );

    // *** BeforeAfterFilterName ***

    @Label( standard = "Portal Filter Name" )
    @CustomXmlValueBinding( impl = BeforeAfterFilterNameBinding.class )
    @Service( impl = PortalFilterNamesPossibleValuesService.class )
    ValueProperty PROP_BEFORE_AFTER_FILTER_NAME = new ValueProperty( TYPE, "BeforeAfterFilterName" ); //$NON-NLS-1$

    Value<String> getBeforeAfterFilterName();

    void setBeforeAfterFilterName( String value );

    // *** URLPattern ***

    @Type( base = URLPattern.class )
    @Label( standard = "url patterns" )
    @CountConstraint( min = 1 )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "url-pattern", type = URLPattern.class ) } )
    ListProperty PROP_URL_PATTERNS = new ListProperty( TYPE, "URLPatterns" ); //$NON-NLS-1$

    ModelElementList<URLPattern> getURLPatterns();

    // *** Dispatchers ***

    @Type( base = Dispatcher.class )
    @Label( standard = "Dispatchers" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "dispatcher", type = Dispatcher.class ) } )
    ListProperty PROP_DISPATCHERS = new ListProperty( TYPE, "Dispatchers" ); //$NON-NLS-1$

    ModelElementList<Dispatcher> getDispatchers();

}
