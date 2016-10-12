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

package com.liferay.ide.project.core.modules.templates.portletfilter;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentPortletFilterOperation extends AbstractLiferayComponentTemplate
{
	private static final String PORTLET_FILTER_TEMPLATE_FILE = "portletfilter/portletfilter.ftl";

    private final static String PORTLET_SUPER_CLASSES = "GenericPortlet";
    private final static String PORTLET_FILTER_SUPER_CLASSES = "RenderFilter";

    private final static String PORTLET_EXTENSION_CLASSES = "Portlet.class";
    private final static String PORTLET_FILTER_EXTENSION_CLASSES = "PortletFilter.class";

    private final static String[] PORTLET_PROPERTIES_LIST =
    	{
    			"com.liferay.portlet.display-category=category.sample",
    			"com.liferay.portlet.instanceable=true",
    			"javax.portlet.security-role-ref=power-user,user"
    	};

    public NewLiferayComponentPortletFilterOperation( )
    {
        super();
    }

    private List<String> getPortletImports()
    {
        List<String> imports = new ArrayList<String>();
        
        imports.add( "java.io.IOException" );
        imports.add( "java.io.PrintWriter" );
        imports.add( "javax.portlet.GenericPortlet" );
        imports.add( "javax.portlet.Portlet" );
        imports.add( "javax.portlet.PortletException" );
        imports.add( "javax.portlet.RenderRequest" );
        imports.add( "javax.portlet.RenderResponse" );

        imports.addAll( super.getImports() );

        return imports;
    }

    private List<String> getPortletFilterImports()
    {
        List<String> imports = new ArrayList<String>();
        
        imports.add( "java.io.IOException" );
        imports.add( "javax.portlet.filter.FilterChain" );
        imports.add( "javax.portlet.filter.FilterConfig" );
        imports.add( "javax.portlet.filter.PortletFilter" );
        imports.add( "javax.portlet.filter.RenderFilter" );
        imports.add( "javax.portlet.RenderRequest" );
        imports.add( "javax.portlet.RenderResponse" );
        imports.add( "javax.portlet.PortletException" );
        imports.addAll( super.getImports() );

        return imports;
    }

    private List<String> getPortletProperties()
    {
        List<String> properties = new ArrayList<String>();
        properties.addAll( Arrays.asList( PORTLET_PROPERTIES_LIST ) );
        for( String property : super.getProperties() )
        {
            properties.add( property );
        }
        properties.add( "javax.portlet.display-name=" + this.componentNameWithoutTemplateName + " Filter Portlet" );
        properties.add( "javax.portlet.name=blade_portlet_filter_" + this.componentNameWithoutTemplateName + "Portlet" );

        return properties;
    }

    private List<String> getPortletFilterProperties()
    {
        List<String> properties = new ArrayList<String>();

        for( String property : super.getProperties() )
        {
            properties.add( property );
        }

        properties.add( "javax.portlet.name=blade_portlet_filter_" + this.componentNameWithoutTemplateName + "Portlet" );

        return properties;
    }

    private String getPortletExtensionClass()
    {
        return PORTLET_EXTENSION_CLASSES;
    }

    private String getPortletFilterExtensionClass()
    {
        return PORTLET_FILTER_EXTENSION_CLASSES;
    }

    private String getPortletSuperClass()
    {
        return PORTLET_SUPER_CLASSES;
    }

    private String getPortletFilterSuperClass()
    {
        return PORTLET_FILTER_SUPER_CLASSES;
    }

    @Override
    public void doExecute( NewLiferayComponentOp op, IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            initializeOperation( op );

            this.project = CoreUtil.getProject( projectName );

            if( project != null )
            {
                liferayProject = LiferayCore.create( project );

                if( liferayProject != null )
                {
                    initFreeMarker();

                    IFile pollerClassFile = prepareClassFile( this.componentNameWithoutTemplateName + "Portlet" );
                    doSourceCodeOperation( pollerClassFile, "portlet" );

                    IFile pollerPortletClassFile =
                        prepareClassFile( this.componentNameWithoutTemplateName + "RenderFilter" );
                    doSourceCodeOperation( pollerPortletClassFile, "renderFilter" );

                    op.setComponentClassName( this.componentNameWithoutTemplateName + "RenderFilter" );

                    project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                }
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    private Map<String, Object> getTemplateMap( String type )
    {
        Map<String, Object> root = new HashMap<String, Object>();

        if( type.equals( "portlet" ) )
        {
            root.put( "importlibs", getPortletImports() );
            root.put( "properties", getPortletProperties() );
            root.put( "classname", componentNameWithoutTemplateName + "Portlet" );
            root.put( "supperclass", getPortletSuperClass() );
            root.put( "extensionclass", getPortletExtensionClass() );
        }
        else
        {
            root.put( "importlibs", getPortletFilterImports() );
            root.put( "properties", getPortletFilterProperties() );
            root.put( "classname", componentNameWithoutTemplateName + "RenderFilter" );
            root.put( "supperclass", getPortletFilterSuperClass() );
            root.put( "extensionclass", getPortletFilterExtensionClass() );
        }

        root.put( "packagename", packageName );
        root.put( "projectname", projectName );
        root.put( "componenttype", templateName );

        return root;
    }

    @Override
    protected String getTemplateFile()
    {
        return PORTLET_FILTER_TEMPLATE_FILE;
    }

    private void doSourceCodeOperation( IFile srcFile, String type ) throws CoreException
    {
        try(OutputStream fos = new FileOutputStream( srcFile.getLocation().toFile() ))
        {

            Template temp = cfg.getTemplate( getTemplateFile() );

            Map<String, Object> root = getTemplateMap( type );

            Writer out = new OutputStreamWriter( fos );
            temp.process( root, out );
            fos.flush();
        }
        catch( IOException | TemplateException e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    @Override
    protected List<String[]> getComponentDependency() throws CoreException
    {
        List<String[]> componentDependency = super.getComponentDependency();
        componentDependency.add( new String[]{ "javax.portlet", "portlet-api", "2.0"} );
        return componentDependency;
    }
}