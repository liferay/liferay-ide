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

package com.liferay.ide.project.core.modules.templates.pollerprocessor;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentPollerProcessorOperation extends AbstractLiferayComponentTemplate
{
	private static final String POLLER_TEMPLATE_FILE = "pollerprocessor/pollerprocessor.ftl";

    private final static String POLLER_SUPER_CLASSES = "BasePollerProcessor";
    private final static String POLLER_PORTLET_SUPER_CLASSES = "MVCPortlet";

    private final static String POLLER_EXTENSION_CLASSES = "PollerProcessor.class";
    private final static String POLLER_PORTLET_EXTENSION_CLASSES = "Portlet.class";

    private final static String[] POLLER_PORTLET_PROPERTIES_LIST =
    	{
        	"com.liferay.portlet.css-class-wrapper=portlet-pollprocessor-blade",
        	"com.liferay.portlet.display-category=category.sample",
        	"com.liferay.portlet.private-request-attributes=false",
        	"com.liferay.portlet.private-session-attributes=false",
        	"com.liferay.portlet.remoteable=true",
        	"com.liferay.portlet.render-weight=50",
        	"javax.portlet.expiration-cache=0",
        	"javax.portlet.portlet.info.keywords=pollprocessor",
        	"javax.portlet.security-role-ref=power-user,user"
    	};

    public NewLiferayComponentPollerProcessorOperation()
    {
        super();
    }

    private List<String> getPollerPortletImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "javax.portlet.Portlet" );
        imports.add( "com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet" );
        imports.addAll( super.getImports() );

        return imports;
    }

    private List<String> getPollerImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "com.liferay.portal.kernel.json.JSONFactoryUtil" );
        imports.add( "com.liferay.portal.kernel.json.JSONObject" );
        imports.add( "com.liferay.portal.kernel.log.Log" );
        imports.add( "com.liferay.portal.kernel.log.LogFactoryUtil" );
        imports.add( "com.liferay.portal.kernel.poller.BasePollerProcessor" );
        imports.add( "com.liferay.portal.kernel.poller.DefaultPollerResponse" );
        imports.add( "com.liferay.portal.kernel.poller.PollerProcessor" );
        imports.add( "com.liferay.portal.kernel.poller.PollerRequest" );
        imports.add( "com.liferay.portal.kernel.poller.PollerResponse" );
        imports.add( "java.util.Date" );

        imports.addAll( super.getImports() );
        return imports;
    }

    private List<String> getPollerProperties()
    {
        List<String> properties = new ArrayList<String>();

        for( String property : super.getProperties() )
        {
            properties.add( property );
        }
        properties.add( "javax.portlet.name=" + this.componentClassName + "Portlet" );

        return properties;
    }

    private List<String> getPollerPortletProperties()
    {
        List<String> properties = new ArrayList<String>();
        properties.addAll( Arrays.asList( POLLER_PORTLET_PROPERTIES_LIST ) );

        for( String property : super.getProperties() )
        {
            properties.add( property );
        }
        properties.add( "javax.portlet.init-param.template-path=/");
        properties.add( "com.liferay.portlet.poller-processor-class=" + this.packageName + "." + this.componentClassName );
        properties.add( "javax.portlet.display-name=" + this.componentClassName );
        properties.add( "javax.portlet.portlet.info.short-title=" + this.componentClassName );
        properties.add( "javax.portlet.portlet.info.title=" + this.componentClassName );
        properties.add( "com.liferay.portlet.header-portlet-javascript=/" + componentClassName.toLowerCase() + "/js/main.js" );
        properties.add( "javax.portlet.init-param.view-template=/" + componentClassName.toLowerCase() + "/view.jsp" );
        properties.add( "javax.portlet.resource-bundle=content.Language" );

        return properties;
    }

    private String getPollerExtensionClass()
    {
        return POLLER_EXTENSION_CLASSES;
    }

    private String getPollerPortletExtensionClass()
    {
        return POLLER_PORTLET_EXTENSION_CLASSES;
    }

    private String getPollerSuperClass()
    {
        return POLLER_SUPER_CLASSES;
    }

    private String getPollerPortletSuperClass()
    {
        return POLLER_PORTLET_SUPER_CLASSES;
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

                    IFile pollerClassFile = prepareClassFile( this.componentClassName );
                    doSourceCodeOperation( pollerClassFile, "poller" );

                    IFile pollerPortletClassFile = prepareClassFile( this.componentClassName + "Portlet" );
                    doSourceCodeOperation( pollerPortletClassFile, "pollerPortlet" );

                    doMergeResourcesOperation();

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

        if( type.equals( "poller" ) )
        {
            root.put( "importlibs", getPollerImports() );
            root.put( "properties", getPollerProperties() );
            root.put( "classname", componentClassName );
            root.put( "supperclass", getPollerSuperClass() );
            root.put( "extensionclass", getPollerExtensionClass() );
        }
        else
        {
            root.put( "importlibs", getPollerPortletImports() );
            root.put( "properties", getPollerPortletProperties() );
            root.put( "classname", componentClassName + "Portlet" );
            root.put( "supperclass", getPollerPortletSuperClass() );
            root.put( "extensionclass", getPollerPortletExtensionClass() );
        }

        root.put( "packagename", packageName );
        root.put( "projectname", projectName );
        root.put( "componenttype", templateName );

        return root;
    }

    @Override
    protected String getTemplateFile()
    {
        return POLLER_TEMPLATE_FILE;
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
    protected void doMergeResourcesOperation() throws CoreException
    {
        try
        {
            IFolder resourceFolder = liferayProject.getSourceFolder( "resources" );

            IFolder contentFolder = resourceFolder.getFolder( "content" );

            final IFile languageProperties = contentFolder.getFile( new Path( "Language.properties" ) );

            final File languagePropertiesFile = languageProperties.getLocation().toFile();

            if( languagePropertiesFile.exists() )
            {
                String originContent = FileUtil.readContents( languagePropertiesFile, true );

                URL sampleFileURL = getClass().getClassLoader().getResource(
                    TEMPLATE_DIR + "/pollerprocessor/poller-language.properties" );

                String addContent =
                    FileUtil.readContents( new File( FileLocator.toFileURL( sampleFileURL ).getFile() ), true );

                String totalContent = originContent + System.getProperty( "line.separator" ) + addContent;

                FileUtil.writeFile( languagePropertiesFile, totalContent.getBytes(), projectName );
            }
            else
            {
                createSampleFile( languageProperties, "pollerprocessor/poller-language.properties" );
            }

            IFolder metaFolder = resourceFolder.getFolder( "META-INF/resources" );

            final IFile mainJs = metaFolder.getFile( new Path( componentClassName.toLowerCase() + "/js/main.js" ) );

            if( !mainJs.getLocation().toFile().exists() )
            {
                createSampleFile( mainJs, "pollerprocessor/poller-main.js" );
            }

            final IFile initJsp = metaFolder.getFile( new Path( componentClassName.toLowerCase() + "/init.jsp" ) );

            if( !initJsp.getLocation().toFile().exists() )
            {
                createSampleFile( initJsp, "pollerprocessor/poller-init.jsp" );
            }

            final IFile viewJsp = metaFolder.getFile( new Path( componentClassName.toLowerCase() + "/view.jsp" ) );

            if( !viewJsp.getLocation().toFile().exists() )
            {
                createSampleFile( viewJsp, "pollerprocessor/poller-view.jsp", "/init.jsp", "/" + componentClassName.toLowerCase() + "/init.jsp" );
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
        componentDependency.add( new String[]{ "javax.portlet", "portlet-api", "2.0"} );
        return componentDependency;
    }
}