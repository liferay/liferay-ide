/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.templates.core;

import org.apache.velocity.VelocityContext;

/**
 * @author Cindy Li
 */
public class TemplateContext extends VelocityContext implements ITemplateContext
{
    TemplateContext()
    {
        super();
    }

    public boolean containsKey( String key )
    {
        return super.containsKey( key );
    }

    public Object put( String key, Object value )
    {
        return super.put( key, value );
    }

}
