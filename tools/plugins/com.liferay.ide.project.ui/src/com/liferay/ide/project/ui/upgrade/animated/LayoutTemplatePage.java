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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

/**
 * @author Adny
 * @author Simon Jiang
 * @author Joye Luo
 */
public class LayoutTemplatePage extends Page
{

    public LayoutTemplatePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, LAYOUTTEMPLATE_PAGE_ID, true );

        LiferayLayouttplUpgradeTableViewCustomPart liferayLayouttplUpgradeTableViewCustomPart =
            new LiferayLayouttplUpgradeTableViewCustomPart( this, SWT.NONE, dataModel );
        liferayLayouttplUpgradeTableViewCustomPart.setLayoutData(
            new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        
    }

    @Override
    public String getPageTitle()
    {
        return "Upgrade Layout Template";
    }

    public void createSpecialDescriptor( Composite parent, int style )
    {
        final String descriptor = "This step will upgrade layout template files from 6.2 to 7.0.\n" +
            "The layout template's rows and columns are affected by the new grid system syntax of Bootsrap.\n" +
            "For more details, please see <a>Upgrading Layout Templates</a>.";

        String url = "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/upgrading-layout-templates";

        Link  link = SWTUtil.createHyperLink( this, style, descriptor, 1, url );
        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
    }

    @Override
    public int getGridLayoutCount()
    {
        return 2;
    }

    @Override
    public boolean getGridLayoutEqualWidth()
    {
        return false;
    }

}
