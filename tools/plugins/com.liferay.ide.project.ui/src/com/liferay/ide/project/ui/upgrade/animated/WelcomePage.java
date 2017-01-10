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
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
public class WelcomePage extends Page
{

    public WelcomePage( final Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, WELCOME_PAGE_ID, false );
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

    @Override
    public String getPageTitle()
    {
        return "Welcome to the Liferay code upgrade tool";
    }

    public void createSpecialDescriptor( Composite parent, int style )
    {
        final String desriptor =
            "The Liferay code upgrade tool will help you to upgrade Liferay 6.2 plugin projects into Liferay 7.0 projects.\n\n" +
                "The key functions are described below:\n" +
                "       1. Convert Liferay Plugins SDK 6.2 to Liferay Plugins SDK 7.0 or to Liferay Workspace\n" +
                "       2. Update Descriptor files from 6.2 to 7.0\n" +
                "       3. Find Breaking Changes in the API stages that need to be migrated to Liferay 7\n" +
                "       4. Update Layout Template files from 6.2 to 7.0 format\n" +
                "       5. Automatically Convert Custom JSP Hooks to OSGi modules\n\n" +
                "Note:\n" +
                "       It is highly recommended that you back-up copies of your original plugin files before continuing.\n" +
                "       Ext projects are not supported to upgrade in this tool currently.\n" +
                "       For more details, please see <a>From Liferay 6 to Liferay 7</a>.\n\n" +
                "How to use this tool:\n" +
                "       In order to move through various upgrade steps,\n" +
                "       use left, right, ✓, X and clicking on each gear to move between the upgrade steps.\n" +
                "       When you are finished with an upgrade step, mark it done by selecting ✓ button,\n" +
                "       or, if it is not complete or you want to come back to it later, mark it with an X.";

        String url = "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/from-liferay-6-to-liferay-7";

        Link link = SWTUtil.createHyperLink( this, style, desriptor, 1, url );

        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 1, 1 ) );
    }
}