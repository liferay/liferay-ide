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
package com.liferay.ide.project.ui.migration;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.navigator.CommonNavigator;

import com.liferay.ide.project.ui.ProjectUI;

/**
 * @author Lovett Li
 */
public class ExpandAllAction extends Action
{

    private CommonNavigator cn;

    public ExpandAllAction( String text, CommonNavigator cn )
    {
        super( text );
        this.cn = cn;
        setImageDescriptor( ProjectUI.getDefault().getImageRegistry().getDescriptor( ProjectUI.EXPANDALL_IMAGE_ID ) );
    }

    @Override
    public void run()
    {
        cn.getCommonViewer().expandAll();
    }
}
