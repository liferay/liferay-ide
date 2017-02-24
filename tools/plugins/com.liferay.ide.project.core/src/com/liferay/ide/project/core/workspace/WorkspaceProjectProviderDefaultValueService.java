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

package com.liferay.ide.project.core.workspace;

import org.eclipse.sapphire.DefaultValueService;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

/**
 * @author Joye Luo
 */
public class WorkspaceProjectProviderDefaultValueService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        String retval = null;

        retval = ProjectUtil.getSelectProjectBuildType( ProjectCore.PREF_DEFAULT_WORKSPACE_PROJECT_BUILD_TYPE_OPTION );

        if( retval != null )
        {
            return retval;
        }
        else
        {
            retval = "gradle-liferay-workspace";

            return retval;
        }
    }

}
