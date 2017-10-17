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

package com.liferay.ide.project.core.modules.templates.portletinactioncommand;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;
import com.liferay.ide.project.core.modules.templates.BndProperties;
import com.liferay.ide.project.core.modules.templates.BndPropertiesValue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentPortletActionCommandOperation extends AbstractLiferayComponentTemplate
{
	private static final String PORTLET_ACTION_COMMAND_TEMPLATE_FILE = "portletinactioncommand/portletactioncommand.ftl";

    private final static String PORTLET_SUPER_CLASSES = "FreeMarkerPortlet";
    private final static String PORTLET_ACTION_COMMAND_SUPER_CLASSES = "MVCActionCommand";

    private final static String PORTLET_EXTENSION_CLASSES = "Portlet.class";
    private final static String PORTLET_ACTION_COMMAND_EXTENSION_CLASSES = "MVCActionCommand.class";

    private final static String[] PORTLET_PROPERTIES_LIST =
        {
            "com.liferay.portlet.display-category=category.sample",
            "com.liferay.portlet.instanceable=true",
            "javax.portlet.init-param.template-path=/",
            "javax.portlet.security-role-ref=power-user,user"
        };

    public NewLiferayComponentPortletActionCommandOperation()
    {
        super();
    }

    private List<String> getPortletImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.addAll( super.getImports() );
        imports.add( "javax.portlet.Portlet" );
        imports.add( "com.liferay.util.bridges.freemarker.FreeMarkerPortlet" );

        return imports;
    }

    private List<String> getPortletActionCommandImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "com.liferay.portal.kernel.log.Log" );
        imports.add( "com.liferay.portal.kernel.log.LogFactoryUtil" );
        imports.add( "com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand" );
        imports.add( "com.liferay.portal.kernel.servlet.SessionMessages" );
        imports.add( "com.liferay.portal.kernel.util.ParamUtil" );
        imports.add( "com.liferay.portal.kernel.util.StringPool" );
        imports.add( "javax.portlet.ActionRequest" );
        imports.add( "javax.portlet.ActionResponse" );
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
        properties.add( "javax.portlet.display-name=" + this.componentNameWithoutTemplateName + " Portlet" );
        properties.add( "javax.portlet.init-param.view-template=/" + this.componentClassName.toLowerCase().toLowerCase() + "/view.ftl" );
        properties.add( "com.liferay.portlet.css-class-wrapper=portlet-" + this.componentNameWithoutTemplateName.toLowerCase() );

        return properties;
    }

    private List<String> getPortletActionCommandProperties()
    {
        List<String> properties = new ArrayList<String>();

        properties.add( "javax.portlet.name=" + "blade_portlet_" +  this.componentNameWithoutTemplateName + "Portlet" );
        properties.add( "mvc.command.name=" + this.componentNameWithoutTemplateName.toLowerCase() );

        return properties;
    }

    private String getPortletExtensionClass()
    {
        return PORTLET_EXTENSION_CLASSES;
    }

    private String getPortletActionCommandExtensionClass()
    {
        return PORTLET_ACTION_COMMAND_EXTENSION_CLASSES;
    }

    private String getPortletSuperClass()
    {
        return PORTLET_SUPER_CLASSES;
    }

    private String getPortletActionCommandSuperClass()
    {
        return PORTLET_ACTION_COMMAND_SUPER_CLASSES;
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

                    IFile pollerClassFile = prepareClassFile( componentNameWithoutTemplateName + "Portlet" );
                    doSourceCodeOperation( pollerClassFile, "portlet" );

                    IFile pollerPortletClassFile =
                        prepareClassFile( componentNameWithoutTemplateName + "ActionCommand" );
                    doSourceCodeOperation( pollerPortletClassFile, "actionCommand" );

                    op.setComponentClassName( componentNameWithoutTemplateName + "ActionCommand" );

                    doMergeResourcesOperation();
                    
                    doMergeBndOperation();

                    doMergeDependencyOperation();

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
            root.put( "importlibs", getPortletActionCommandImports() );
            root.put( "properties", getPortletActionCommandProperties() );
            root.put( "classname", componentNameWithoutTemplateName + "ActionCommand" );
            root.put( "supperclass", getPortletActionCommandSuperClass() );
            root.put( "extensionclass", getPortletActionCommandExtensionClass() );
        }

        root.put( "packagename", packageName );
        root.put( "projectname", projectName );
        root.put( "componenttype", templateName );

        return root;
    }

    @Override
    protected String getTemplateFile()
    {
        return PORTLET_ACTION_COMMAND_TEMPLATE_FILE;
    }

    private void doSourceCodeOperation( IFile srcFile, String type ) throws CoreException
    {
        try(OutputStream fos = Files.newOutputStream( srcFile.getLocation().toFile().toPath() ))
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
    protected void doMergeResourcesOperation() throws CoreException
    {
        try
        {
            IFolder resourceFolder = liferayProject.getSourceFolder( "resources" );

            IFolder metaFolder = resourceFolder.getFolder( "META-INF/resources" );

            final IFile initFtl = metaFolder.getFile( new Path( this.componentClassName.toLowerCase().toLowerCase() + "/init.ftl" ) );

            if( !initFtl.getLocation().toFile().exists() )
            {
                createSampleFile( initFtl, "portletinactioncommand/portletactioncommand-init.ftl" );
            }

            final IFile viewFtl = metaFolder.getFile( new Path( this.componentClassName.toLowerCase().toLowerCase() + "/view.ftl" ) );

            if( !viewFtl.getLocation().toFile().exists() )
            {
                createSampleFile( viewFtl, "portletinactioncommand/portletactionconmmand-view.ftl" );
            }

        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    @Override
    protected List<String[]> getComponentDependency() throws CoreException
    {
        List<String[]> componentDependency = super.getComponentDependency();
        componentDependency.add( new String[]{ "com.liferay.portal", "com.liferay.util.bridges", "2.0.0"} );
        componentDependency.add( new String[]{ "com.liferay.portal", "com.liferay.util.taglib", "2.0.0"} );
        componentDependency.add( new String[]{ "javax.portlet", "portlet-api", "2.0"} );
        componentDependency.add( new String[]{ "javax.servlet", "javax.servlet-api", "3.0.1"} );
        return componentDependency;
    }

    @Override
    protected void setBndProperties( BndProperties bndProperty )
    {
        final String formatedValue = "\\" + System.getProperty( "line.separator" ) +
            "\t" + "@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class," + "\\" +  System.getProperty( "line.separator" ) +
            "\t" + "@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld";
        final String originalValue = "@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class," + 
            "@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld";

        bndProperty.addValue( "-includeresource", new BndPropertiesValue( formatedValue, originalValue ) );
        bndProperty.addValue( "-sources", new BndPropertiesValue( "true" ) );
    }

}