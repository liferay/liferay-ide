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
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IProject;
import org.junit.Test;


/**
 * @author Lovett Li
 */
public class SDKUtilTests extends ProjectCoreBase
{

    @Test
    public void nullWorkSpaceSDKProject() throws Exception
    {
        IProject project = SDKUtil.getWorkspaceSDKProject();

        assertNull( project );
    }

    @Test
    public void singleWorkSpaceProject() throws Exception
    {
        SDK sdkProject = SDKManager.getInstance().getDefaultSDK();
        SDKUtil.openAsProject( sdkProject );

        IProject project = SDKUtil.getWorkspaceSDKProject();

        assertNotNull( project );
    }

}
