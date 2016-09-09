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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 */
public class DescriptorsPage extends Page
{

    public DescriptorsPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, DESCRIPTORS_PAGE_ID, true );

        new LiferayDescriptorUpgradeTableViewCustomPart( this, SWT.NONE );
    }

    @Override
    public String getDescriptor()
    {
        return "This step will upgrade descriptor xml dtd version from 6.2 to 7.0 and " +
            "delete wap-template-path \ntag in liferay-layout-template.xml.\n" +
            "Double click the file in the list. It will popup a comparison page which shows the differences\n" +
            "between your original source file and the upgrade preview file.";
    }

    @Override
    public String getPageTitle()
    {
        return "Upgrade Descriptor Files";
    }

}
