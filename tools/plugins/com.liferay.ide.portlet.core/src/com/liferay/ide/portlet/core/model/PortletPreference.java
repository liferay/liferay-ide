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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/obj16/preferences.gif" )
public interface PortletPreference extends Element, Identifiable
{

    ElementType TYPE = new ElementType( PortletPreference.class );

    // *** PortletPreferences ***

    @Type( base = Preference.class )
    @Label( standard = "Preferences" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "preference", type = Preference.class ) )
    ListProperty PROP_PORTLET_PREFERENCES = new ListProperty( TYPE, "PortletPreferences" ); //$NON-NLS-1$

    ElementList<Preference> getPortletPreferences();

    // *** PreferernceValidator ***

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "javax.portlet.PreferencesValidator" } )
    @Label( standard = "Preference Validator" )
    @NoDuplicates
    @XmlBinding( path = "preferences-validator" )
    ValueProperty PROP_PREFERERNCE_VALIDATOR = new ValueProperty( TYPE, "PreferernceValidator" ); //$NON-NLS-1$

    ReferenceValue<JavaTypeName, JavaType> getPreferernceValidator();

    void setPreferernceValidator( String portletClass );

    void setPreferernceValidator( JavaTypeName portletClass );

}
