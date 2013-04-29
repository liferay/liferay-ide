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
 *******************************************************************************/

package com.liferay.ide.portlet.vaadin7.core.operation;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.operation.NewPortletClassDataModelProvider;
import com.liferay.ide.sdk.core.ISDKConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;

/**
 * @author Henri Sara
 */
@SuppressWarnings( { "restriction", "rawtypes", "unchecked" } )
public class NewVaadin7PortletClassDataModelProvider extends NewPortletClassDataModelProvider
    implements INewVaadin7PortletClassDataModelProperties
{

    public NewVaadin7PortletClassDataModelProvider( boolean fragment )
    {
        super( fragment );
    }

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( CLASS_NAME.equals( propertyName ) )
        {
            return "NewVaadin7PortletApplication"; //$NON-NLS-1$
        }
        else if( PORTLET_NAME.equals( propertyName ) || LIFERAY_PORTLET_NAME.equals( propertyName ) )
        {
            return getPortletName().toLowerCase();
        }
        else if( DISPLAY_NAME.equals( propertyName ) || TITLE.equals( propertyName ) ||
            SHORT_TITLE.equals( propertyName ) )
        {
            return getPortletName();
        }
        else if( CSS_CLASS_WRAPPER.equals( propertyName ) )
        {
            return getPortletName().toLowerCase() + ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
        }
        else if( SUPERCLASS.equals( propertyName ) )
        {
            return QUALIFIED_VAADIN7_UI;
        }
        else if( VAADIN7_PORTLET_CLASS.equals( propertyName ) )
        {
            return QUALIFIED_VAADIN7_PORTLET;
        }
        else if( CREATE_JSPS.equals( propertyName ) )
        {
            return false;
        }
        else if( CONSTRUCTOR.equals( propertyName ) )
        {
            return false;
        }
        else if( SHOW_NEW_CLASS_OPTION.equals( propertyName ) )
        {
            return false;
        }

        return super.getDefaultProperty( propertyName );
    }

    private String getPortletName()
    {
        return getProperty( CLASS_NAME ).toString().replaceAll( "UI", StringPool.EMPTY ); //$NON-NLS-1$
    }

    @Override
    public Set getPropertyNames()
    {
        Set propertyNames = super.getPropertyNames();
        propertyNames.add( VAADIN7_PORTLET_CLASS );
        return propertyNames;
    }

    @Override
    public DataModelPropertyDescriptor[] getValidPropertyDescriptors( String propertyName )
    {
        if( SUPERCLASS.equals( propertyName ) )
        {
            String[] vals = new String[] { QUALIFIED_VAADIN7_UI };

            return DataModelPropertyDescriptor.createDescriptors( vals, vals );
        }
        else if( VAADIN7_PORTLET_CLASS.equals( propertyName ) )
        {
            String[] vals = new String[] { QUALIFIED_VAADIN7_PORTLET };

            return DataModelPropertyDescriptor.createDescriptors( vals, vals );
        }

        return super.getValidPropertyDescriptors( propertyName );
    }

    @Override
    public boolean isPropertyEnabled( String propertyName )
    {
        if( CREATE_JSPS.equals( propertyName ) || CREATE_JSPS_FOLDER.equals( propertyName ) )
        {
            return false;
        }
        else if( EDIT_MODE.equals( propertyName ) || HELP_MODE.equals( propertyName ) )
        {
            return false;
        }

        return super.isPropertyEnabled( propertyName );
    }

    @Override
    public IStatus validate( String propertyName )
    {
        // also accept the case where the superclass does not exist (yet)
        if( SUPERCLASS.equals( propertyName ) )
        {
            return Status.OK_STATUS;
        }

        return super.validate( propertyName );
    }

    @Override
    protected Object getInitParams()
    {
        List<ParamValue> initParams = new ArrayList<ParamValue>();

        if( getStringProperty( VAADIN7_PORTLET_CLASS ).equals( QUALIFIED_VAADIN7_PORTLET ) )
        {
            ParamValue paramValue = CommonFactory.eINSTANCE.createParamValue();
            paramValue.setName( "UI" ); //$NON-NLS-1$

            String pkg = getDataModel().getStringProperty( JAVA_PACKAGE );
            String cls = getDataModel().getStringProperty( CLASS_NAME );
            String qualifiedApplicationClass = ( pkg == null || StringPool.EMPTY.equals( pkg ) ) ? cls : pkg + "." + cls; //$NON-NLS-1$

            paramValue.setValue( qualifiedApplicationClass );

            initParams.add( paramValue );
        }

        return initParams;
    }

}
