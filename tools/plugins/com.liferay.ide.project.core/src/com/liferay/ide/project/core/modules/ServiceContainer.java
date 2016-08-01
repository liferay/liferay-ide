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

import java.util.List;

/**
 * @author Lovett Li
 */
public class ServiceContainer
{

    private List<String> serviceList;
    private String bundleName;
    private String bundleVersion;

    public ServiceContainer( List<String> serviceList )
    {
        this.serviceList = serviceList;
    }

    public ServiceContainer( String bundleName, String bundleVersion )
    {
        this.bundleName = bundleName;
        this.bundleVersion = bundleVersion;
    }

    public List<String> getServiceList()
    {
        return serviceList;
    }

    public void setServiceList( List<String> serviceList )
    {
        this.serviceList = serviceList;
    }

    public String getBundleName()
    {
        return bundleName;
    }

    public void setBundleName( String bundleName )
    {
        this.bundleName = bundleName;
    }

    public String getBundleVersion()
    {
        return bundleVersion;
    }

    public void setBundleVersion( String bundleVersion )
    {
        this.bundleVersion = bundleVersion;
    }

}
