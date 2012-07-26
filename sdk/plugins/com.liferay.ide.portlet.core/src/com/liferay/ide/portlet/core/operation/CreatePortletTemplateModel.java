/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.core.operation;

import java.util.Collection;

import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class CreatePortletTemplateModel extends CreateWebClassTemplateModel
{
    protected boolean generateGenericInclude = false;

    public CreatePortletTemplateModel( IDataModel dataModel )
    {
        super( dataModel );
    }

    public String getClassName()
    {
        return dataModel.getStringProperty( INewPortletClassDataModelProperties.CLASS_NAME );
    }

    @Override
    public Collection<String> getImports()
    {
        Collection<String> collection = super.getImports();

        if( !isMVCPortletSuperclass() )
        {
            collection.add( "java.io.IOException" );
            collection.add( "javax.portlet.PortletException" );
            // collection.add("javax.portlet.PortletRequest");
            collection.add( "javax.portlet.PortletRequestDispatcher" );
            // collection.add("javax.portlet.PortletResponse");
            collection.add( "javax.portlet.RenderRequest" );
            collection.add( "javax.portlet.RenderResponse" );
            collection.add( "com.liferay.portal.kernel.log.Log" );
            collection.add( "com.liferay.portal.kernel.log.LogFactoryUtil" );
        }

        if( shouldGenerateOverride( INewPortletClassDataModelProperties.PROCESSACTION_OVERRIDE ) )
        {
            collection.add( "javax.portlet.ActionRequest" );
            collection.add( "javax.portlet.ActionResponse" );
        }

        return collection;
    }

    public boolean hasPortletMode( String portletModeProperty )
    {
        return dataModel.getBooleanProperty( portletModeProperty );
    }

    public boolean isGenericPortletSuperclass()
    {
        return isGenericPortletSuperclass( false );
    }

    public boolean isGenericPortletSuperclass( boolean checkHierarchy )
    {
        return PortletSupertypesValidator.isGenericPortletSuperclass( dataModel, checkHierarchy );
    }

    public boolean isLiferayPortletSuperclass()
    {
        return PortletSupertypesValidator.isLiferayPortletSuperclass( dataModel );
    }

    public boolean isMVCPortletSuperclass()
    {
        return PortletSupertypesValidator.isMVCPortletSuperclass( dataModel );
    }

    public void setGenerateGenericInclude( boolean include )
    {
        this.generateGenericInclude = include;
    }

    public boolean shouldGenerateGenericInclude()
    {
        return this.generateGenericInclude;
    }

    public boolean shouldGenerateOverride( String generateProperty )
    {
        if( isMVCPortletSuperclass() )
        {
            return false;
        }

        return dataModel.getBooleanProperty( generateProperty );
    }

}
