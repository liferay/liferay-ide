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

package com.liferay.ide.project.core.modules.templates.strutsinaction;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentStrutsInActionOperation extends AbstractLiferayComponentTemplate
{

    private static final String STRUTS_TEMPLATE_FILE = "strutsinaction/strutsinaction.ftl";

    private final static String STRUTS_SUPER_CLASSES = "BaseStrutsAction ";
    private final static String STRUTS_EXTENSION_CLASSES = "StrutsAction.class";

    public NewLiferayComponentStrutsInActionOperation()
    {
        super();
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();
        imports.add( "com.liferay.portal.kernel.log.Log" );
        imports.add( "com.liferay.portal.kernel.log.LogFactoryUtil" );
        imports.add( "com.liferay.portal.kernel.struts.BaseStrutsAction" );
        imports.add( "com.liferay.portal.kernel.struts.StrutsAction" );
        imports.add( "javax.servlet.RequestDispatcher" );
        imports.add( "javax.servlet.ServletContext" );
        imports.add( "javax.servlet.http.HttpServletRequest" );
        imports.add( "javax.servlet.http.HttpServletResponse" );
        imports.add( "org.osgi.service.component.annotations.Reference" );
        imports.addAll( super.getImports() );
        return imports;
    }

    @Override
    protected List<String> getProperties()
    {
        List<String> imports = new ArrayList<String>();
        imports.add( "path=/portal/" + this.componentNameWithoutTemplateName.toLowerCase() );
        return imports;
    }

    @Override
    protected String getExtensionClass()
    {
        return STRUTS_EXTENSION_CLASSES;
    }

    @Override
    protected String getSuperClass()
    {
        return STRUTS_SUPER_CLASSES;
    }

    @Override
    protected String getTemplateFile()
    {
        return STRUTS_TEMPLATE_FILE;
    }
    
    @Override
    protected Map<String, Object> getTemplateMap()
    {
        Map<String, Object> root = super.getTemplateMap();
        
        root.put( "simplecomponent", componentNameWithoutTemplateName.toLowerCase() );
        
        
        return root;
    }
    
    
    @Override
    protected void doMergeResourcesOperation() throws CoreException
    {
        try
        {
            IFolder resourceFolder = liferayProject.getSourceFolder( "resources" );

            IFolder metaFolder = resourceFolder.getFolder( "META-INF/resources" );

            final IFile initJsp = metaFolder.getFile( new Path( this.componentClassName.toLowerCase() + "/html/init.jsp" ) );

            if( !initJsp.getLocation().toFile().exists() )
            {
                createSampleFile( initJsp, "strutsinaction/strutsinaction-init.jsp" );
            }

            final IFile viewJsp = metaFolder.getFile( new Path( this.componentClassName.toLowerCase() + "/html/portal/" + componentNameWithoutTemplateName.toLowerCase() + ".jsp" ) );

            if( !viewJsp.getLocation().toFile().exists() )
            {
                createSampleFile( viewJsp, "strutsinaction/strutsinaction-blade.jsp", "/html/init.jsp", this.componentClassName.toLowerCase() + "/html/init.jsp" );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }
}
