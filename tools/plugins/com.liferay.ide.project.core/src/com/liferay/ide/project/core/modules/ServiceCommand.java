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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.util.TargetPlatformUtil;

/**
 * @author Lovett Li
 * @author Simon Jiang
 * @author Terry Jia
 */
public class ServiceCommand
{

    private String _serviceName;

    public ServiceCommand()
    {
    }

    public ServiceCommand( String serviceName )
    {
        _serviceName = serviceName;
    }

    public ServiceContainer execute() throws Exception
    {
        return getServiceFromTargetPlatform();
    }

    private ServiceContainer getServiceFromTargetPlatform() throws Exception
    {
        ServiceContainer result;

        if( _serviceName == null )
        {
            result = TargetPlatformUtil.getServicesList();
        }
        else
        {
            result = TargetPlatformUtil.getServiceBundle( _serviceName );
        }

        return result;
    }

}
