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

package com.liferay.ide.portlet.jsf.ui.wizard;

import com.liferay.ide.portlet.ui.wizard.NewPortletOptionsWizardPage;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewJSFPortletOptionsWizardPage extends NewPortletOptionsWizardPage
{

    public NewJSFPortletOptionsWizardPage(
        IDataModel dataModel, String pageName, String desc, String title, boolean fragment )
    {
        super( dataModel, pageName, desc, title, fragment );
    }

    @Override
    protected void createJSPsField( Composite parent )
    {
        super.createJSPsField( parent );

        createJspsButton.setText( Msgs.createXhtmlFiles );
        jspLabel.setText( Msgs.xhtmlFolder );
    }

    @Override
    protected void createLiferayPortletModesGroup( Composite composite )
    {
        // don't create liferay portlet modes section
    }

    @Override
    protected void createResourceBundleField( Composite parent )
    {
        // don't create resource bundle field section
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite top = super.createTopLevelComposite( parent );

        this.synchHelper.getDataModel().addListener( new IDataModelListener()
        {

            public void propertyChanged( DataModelEvent event )
            {
                if( PORTLET_NAME.equals( event.getPropertyName() ) )
                {
                    synchHelper.synchAllUIWithModel();
                }
            }
        } );

        return top;
    }

    private static class Msgs extends NLS
    {
        public static String createXhtmlFiles;
        public static String xhtmlFolder;

        static
        {
            initializeMessages( NewJSFPortletOptionsWizardPage.class.getName(), Msgs.class );
        }
    }
}
