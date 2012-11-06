/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.core;

/**
 * @author gregory.amerson@liferay.com
 */
public abstract class AbstractPluginPublisher implements IPluginPublisher
{

    protected String facetId;

    protected String runtimeTypeId;

    public AbstractPluginPublisher()
    {
        this( null );
    }

    public AbstractPluginPublisher( String facetId )
    {
        super();
        this.facetId = facetId;
    }

    public String getFacetId()
    {
        return facetId;
    }

    public String getRuntimeTypeId()
    {
        return runtimeTypeId;
    }

    public void setFacetId( String facetId )
    {
        this.facetId = facetId;
    }

    public void setRuntimeTypeId( String runtimeTypeId )
    {
        this.runtimeTypeId = runtimeTypeId;
    }

}
