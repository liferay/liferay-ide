/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kuo Zhang
 */
public class ValidationPreferences
{
    public enum ValidationType
    {
        SYNTAX_INVALID, TYPE_NOT_FOUND, TYPE_HIERARCHY_INCORRECT, REFERENCE_NOT_FOUND, RESOURCE_NOT_FOUND
    }

    // *** validation preferences of liferay-display.xml ***

    public static final String LIFERAY_DISPLAY_XML_TYPE_HIERARCHY_INCORRECT = "liferay-display-xml-type-hierarchy-incorrect";
    public static final String LIFERAY_DISPLAY_XML_TYPE_NOT_FOUND = "liferay-display-xml-type-not-found";
    public static final String LIFERAY_DISPLAY_XML_REFERENCE_NOT_FOUND = "liferay-display-xml-reference-not-found";
    public static final String LIFERAY_DISPLAY_XML_RESOURCE_NOT_FOUND = "liferay-display-xml-resource-not-found";
    public static final String LIFERAY_DISPLAY_XML_SYNTAX_INVALID = "liferay-display-xml-syntax-invalid";


    // *** validation preferences of liferay-hook.xml ***

    public static final String LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT = "liferay-hook-xml-type-hierarchy-incorrect";
    public static final String LIFERAY_HOOK_XML_TYPE_NOT_FOUND = "liferay-hook-xml-type-not-found";
    public static final String LIFERAY_HOOK_XML_REFERENCE_NOT_FOUND = "liferay-hook-xml-reference-not-found";
    public static final String LIFERAY_HOOK_XML_RESOURCE_NOT_FOUND = "liferay-hook-xml-resource-not-found";
    public static final String LIFERAY_HOOK_XML_SYNTAX_INVALID = "liferay-hook-xml-syntax-invalid";


    // *** validation preferences of liferay-layouttpl.xml

    public static final String LIFERAY_LAYOUTTPL_XML_TYPE_HIERARCHY_INCORRECT = "liferay-layouttpl-xml-type-hierarchy-incorrect";
    public static final String LIFERAY_LAYOUTTPL_XML_TYPE_NOT_FOUND = "liferay-layouttpl-xml-type-not-found";
    public static final String LIFERAY_LAYOUTTPL_XML_REFERENCE_NOT_FOUND = "liferay-layouttpl-xml-reference-not-found";
    public static final String LIFERAY_LAYOUTTPL_XML_RESOURCE_NOT_FOUND = "liferay-layouttpl-xml-resource-not-found";
    public static final String LIFERAY_LAYOUTTPL_XML_SYNTAX_INVALID = "liferay-layouttpl-xml-syntax-invalid";


    // *** validation preferences of liferay-portlet.xml ***

    public static final String LIFERAY_PORTLET_XML_TYPE_HIERARCHY_INCORRECT = "liferay-portlet-xml-type-hierarchy-incorrect";
    public static final String LIFERAY_PORTLET_XML_TYPE_NOT_FOUND = "liferay-portlet-xml-type-not-found";
    public static final String LIFERAY_PORTLET_XML_REFERENCE_NOT_FOUND = "liferay-portlet-xml-reference-not-found";
    public static final String LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND = "liferay-portlet-xml-resource-not-found";
    public static final String LIFERAY_PORTLET_XML_SYNTAX_INVALID = "liferay-portlet-xml-syntax-invalid";


    // *** validation preferences of portlet.xml ***

    public static final String PORTLET_XML_TYPE_HIERARCHY_INCORRECT = "portlet-xml-type-hierarchy-incorrect";
    public static final String PORTLET_XML_TYPE_NOT_FOUND = "portlet-xml-type-not-found";
    public static final String PORTLET_XML_REFERENCE_NOT_FOUND = "portlet-xml-reference-not-found";
    public static final String PORTLET_XML_RESOURCE_NOT_FOUND ="portlet-xml-resource-not-found";
    public static final String PORTLET_XML_SYNTAX_INVALID = "portlet-xml-syntax-invalid";


    // *** validation preferences of service.xml ***

    public static final String SERVICE_XML_TYPE_HIERARCHY_INCORRECT = "service-xml-type-hierarchy-incorrect";
    public static final String SERVICE_XML_TYPE_NOT_FOUND = "service-xml-type-not-found";
    public static final String SERVICE_XML_REFERENCE_NOT_FOUND = "service-xml-reference-not-found";
    public static final String SERVICE_XML_RESOURCE_NOT_FOUND = "service-xml-resource-not-found";
    public static final String SERVICE_XML_SYNTAX_INVALID = "service-xml-syntax-invalid";

    private static Set<String> preferenceKeys; 

    static 
    {
        preferenceKeys = new HashSet<String>();

        preferenceKeys.add( LIFERAY_DISPLAY_XML_TYPE_HIERARCHY_INCORRECT );
        preferenceKeys.add( LIFERAY_DISPLAY_XML_TYPE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_DISPLAY_XML_REFERENCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_DISPLAY_XML_RESOURCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_DISPLAY_XML_SYNTAX_INVALID );

        preferenceKeys.add( LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT );
        preferenceKeys.add( LIFERAY_HOOK_XML_TYPE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_HOOK_XML_REFERENCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_HOOK_XML_RESOURCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_HOOK_XML_SYNTAX_INVALID );

        preferenceKeys.add( LIFERAY_LAYOUTTPL_XML_TYPE_HIERARCHY_INCORRECT );
        preferenceKeys.add( LIFERAY_LAYOUTTPL_XML_TYPE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_LAYOUTTPL_XML_REFERENCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_LAYOUTTPL_XML_RESOURCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_LAYOUTTPL_XML_SYNTAX_INVALID );

        preferenceKeys.add( LIFERAY_PORTLET_XML_TYPE_HIERARCHY_INCORRECT );
        preferenceKeys.add( LIFERAY_PORTLET_XML_TYPE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_PORTLET_XML_REFERENCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_PORTLET_XML_RESOURCE_NOT_FOUND );
        preferenceKeys.add( LIFERAY_PORTLET_XML_SYNTAX_INVALID );

        preferenceKeys.add( PORTLET_XML_TYPE_HIERARCHY_INCORRECT );
        preferenceKeys.add( PORTLET_XML_TYPE_NOT_FOUND );
        preferenceKeys.add( PORTLET_XML_REFERENCE_NOT_FOUND );
        preferenceKeys.add( PORTLET_XML_RESOURCE_NOT_FOUND );
        preferenceKeys.add( PORTLET_XML_SYNTAX_INVALID );

        preferenceKeys.add( SERVICE_XML_TYPE_HIERARCHY_INCORRECT );
        preferenceKeys.add( SERVICE_XML_TYPE_NOT_FOUND );
        preferenceKeys.add( SERVICE_XML_REFERENCE_NOT_FOUND );
        preferenceKeys.add( SERVICE_XML_RESOURCE_NOT_FOUND );
        preferenceKeys.add( SERVICE_XML_SYNTAX_INVALID );
    }

    public static String getValidationPreferenceKey( String descriptorFileName, ValidationType type )
    {
        StringBuilder retval = new StringBuilder();

        if( !CoreUtil.isNullOrEmpty( descriptorFileName ) )
        {
            retval.append( ( descriptorFileName.replace( ".", "-" ).toLowerCase() ) );
            retval.append( "-" );
        }

        if( type != null )
        {
            retval.append( type.toString().toLowerCase().replace( "_", "-" ) );
        }

        return retval.toString();
    }

}
