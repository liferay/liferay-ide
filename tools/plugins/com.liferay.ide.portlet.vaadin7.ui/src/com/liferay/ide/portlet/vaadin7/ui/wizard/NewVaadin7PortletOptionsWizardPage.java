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

package com.liferay.ide.portlet.vaadin7.ui.wizard;

import com.liferay.ide.portlet.ui.wizard.NewPortletOptionsWizardPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
public class NewVaadin7PortletOptionsWizardPage extends NewPortletOptionsWizardPage
{

    public NewVaadin7PortletOptionsWizardPage(
        IDataModel dataModel, String pageName, String desc, String title, boolean fragment )
    {
        super( dataModel, pageName, desc, title, fragment );
    }

    @Override
    protected void createJSPsField( Composite parent )
    {
        // do nothing, Vaadin portlets dont yet use these fields
    }

    @Override
    protected void createLiferayPortletModesGroup( Composite composite )
    {
        // do nothing, Vaadin portlets dont yet use these fields
    }

    @Override
    protected void createPortletModesGroup( Composite composite )
    {
        // do nothing, Vaadin portlets dont yet use these fields
    }

}
