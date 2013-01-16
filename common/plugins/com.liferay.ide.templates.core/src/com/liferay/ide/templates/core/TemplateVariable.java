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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.templates.core;

import com.liferay.ide.core.util.CoreUtil;

/**
 * @author Gregory Amerson
 */
public class TemplateVariable
{

    private String name;

    private boolean required;

    public TemplateVariable( String varName, String reqVal )
    {
        this.name = varName;

        if( CoreUtil.isNullOrEmpty( reqVal ) )
        {
            this.required = Boolean.FALSE;
        }
        else
        {
            this.required = Boolean.parseBoolean( reqVal );
        }
    }

    public String getName()
    {
        return name;
    }

    public boolean isRequired()
    {
        return required;
    }

}
