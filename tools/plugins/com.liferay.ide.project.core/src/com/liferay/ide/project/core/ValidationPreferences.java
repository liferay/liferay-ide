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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class ValidationPreferences
{

    public enum ValidationType
    {
        METHOD_NOT_FOUND,
        PROPERTY_NOT_FOUND,
        REFERENCE_NOT_FOUND,
        RESOURCE_NOT_FOUND,
        STATIC_NOT_FOUND,
        SYNTAX_INVALID,
        TYPE_NOT_FOUND,
        TYPE_HIERARCHY_INCORRECT,
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

    public static final String LIFERAY_LAYOUTTPL_XML_TYPE_HIERARCHY_INCORRECT = "liferay-layout-templates-xml-type-hierarchy-incorrect";
    public static final String LIFERAY_LAYOUTTPL_XML_TYPE_NOT_FOUND = "liferay-layout-templates-xml-type-not-found";
    public static final String LIFERAY_LAYOUTTPL_XML_REFERENCE_NOT_FOUND = "liferay-layout-templates-xml-reference-not-found";
    public static final String LIFERAY_LAYOUTTPL_XML_RESOURCE_NOT_FOUND = "liferay-layout-templates-xml-resource-not-found";
    public static final String LIFERAY_LAYOUTTPL_XML_SYNTAX_INVALID = "liferay-layout-templates-xml-syntax-invalid";


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

    // *** validation preferences of liferay jsp files ***

    public static final String LIFERAY_JSP_FILES_JAVA_METHOD_NOT_FOUND = "liferay-jsp-files-java-method-not-found";
    public static final String LIFERAY_JSP_FILES_RESOURCE_PROPERTY_NOT_FOUND = "liferay-jsp-files-resource-property-not-found";

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

        preferenceKeys.add( LIFERAY_JSP_FILES_JAVA_METHOD_NOT_FOUND );
        preferenceKeys.add( LIFERAY_JSP_FILES_RESOURCE_PROPERTY_NOT_FOUND );
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

    // Levels: IGNORE: -1, ERROR: 1, WARNNING: 2
    public static void setInstanceScopeValLevel( int validationLevel )
    {
        final IEclipsePreferences node = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        for( String key : preferenceKeys )
        {
            node.putInt( key, validationLevel );
        }

        try
        {
            node.flush();
        }
        catch( BackingStoreException e )
        {
            ProjectCore.logError( "Error setting validation preferences", e );
        }
    }

    public static void setProjectScopeValLevel( IProject project, int validationLevel )
    {
        final IEclipsePreferences node = new ProjectScope( project ).getNode( ProjectCore.PLUGIN_ID );

        try
        {
            node.putBoolean( ProjectCore.USE_PROJECT_SETTINGS, true );

            for( String key : preferenceKeys )
            {
                 node.putInt( key, validationLevel );
            }

            node.flush();
        }
        catch( BackingStoreException e )
        {
            ProjectCore.logError( "Error setting validation preferences", e );
        }
    }
}
