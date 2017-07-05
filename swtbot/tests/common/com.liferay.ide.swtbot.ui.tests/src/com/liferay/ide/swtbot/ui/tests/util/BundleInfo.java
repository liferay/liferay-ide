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

package com.liferay.ide.swtbot.ui.tests.util;

/**
 * @author Terry Jia
 */
public class BundleInfo
{

    private String bundleZip = "";
    private String bundleDir = "";
    private String tomcatDir = "";
    private String type = "";
    private String version = "";

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getBundleZip()
    {
        return bundleZip;
    }

    public void setBundleZip( String bundleZip )
    {
        this.bundleZip = bundleZip;
    }

    public String getBundleDir()
    {
        return bundleDir;
    }

    public void setBundleDir( String bundleDir )
    {
        this.bundleDir = bundleDir;
    }

    public String getTomcatDir()
    {
        return tomcatDir;
    }

    public void setTomcatDir( String tomcatDir )
    {
        this.tomcatDir = tomcatDir;
    }

}
