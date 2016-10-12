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

package com.liferay.ide.project.core.modules.templates.strutsportletaction;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentStrutsPortletActionOperation extends AbstractLiferayComponentTemplate
{

    private static final String TEMPLATE_FILE = "strutsportletaction/strutsportletaction.ftl";

    private final static String SUPER_CLASS = "BaseStrutsPortletAction";
    private final static String EXTENSION_CLASS = "StrutsPortletAction.class";

    private final static String[] PROPERTIES_LIST = new String[] { "path=/login/login" };

    public NewLiferayComponentStrutsPortletActionOperation()
    {
        super();
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

                    IFile srcFile = prepareClassFile( this.componentNameWithoutTemplateName + "PortletAction" );
                    doSourceCodeOperation( srcFile );

                    op.setComponentClassName( this.componentNameWithoutTemplateName + "PortletAction" );

                    project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                }
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "com.liferay.portal.kernel.log.Log" );
        imports.add( "com.liferay.portal.kernel.log.LogFactoryUtil" );
        imports.add( "com.liferay.portal.kernel.struts.BaseStrutsPortletAction" );
        imports.add( "com.liferay.portal.kernel.struts.StrutsPortletAction" );
        imports.add( "com.liferay.portal.kernel.util.WebKeys" );
        imports.add( "com.liferay.portal.kernel.model.User" );
        imports.add( "com.liferay.portal.kernel.service.UserLocalService" );
        imports.add( "com.liferay.portal.kernel.theme.ThemeDisplay" );
        imports.add( "javax.portlet.ActionRequest" );
        imports.add( "javax.portlet.ActionResponse" );
        imports.add( "javax.portlet.PortletConfig" );
        imports.add( "javax.portlet.RenderRequest" );
        imports.add( "javax.portlet.RenderResponse" );
        imports.add( "javax.portlet.ResourceRequest" );
        imports.add( "javax.portlet.ResourceResponse" );
        imports.add( "org.osgi.service.component.annotations.Reference" );
        imports.addAll( super.getImports() );
        
        return imports;
    }

    @Override
    protected Map<String, Object> getTemplateMap()
    {
        Map<String, Object> root = super.getTemplateMap();
        
        root.put( "classname", componentNameWithoutTemplateName + "PortletAction" );

        return root;
    }
    
    @Override
    protected List<String> getProperties()
    {
        List<String> properties = new ArrayList<String>();
        properties.addAll( Arrays.asList( PROPERTIES_LIST ) );

        for( String property : super.getProperties() )
        {
            properties.add( property );
        }
        return properties;
    }

    @Override
    protected String getExtensionClass()
    {
        return EXTENSION_CLASS;
    }

    @Override
    protected String getSuperClass()
    {
        return SUPER_CLASS;
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }

    @Override
    protected List<String[]> getComponentDependency() throws CoreException
    {
        List<String[]> componentDependency = super.getComponentDependency();
        componentDependency.add( new String[]{ "javax.portlet", "portlet-api", "2.0"} );
        return componentDependency;
    }
}
