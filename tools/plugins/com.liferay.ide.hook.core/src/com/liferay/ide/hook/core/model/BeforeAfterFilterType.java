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
 *
 * Contributors:
 *    Gregory Amerson - initial implementation
 *******************************************************************************/

package com.liferay.ide.hook.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public enum BeforeAfterFilterType
{

    @Label( standard = "Before Filter" )
    @EnumSerialization( caseSensitive = true, primary = "before-filter" )
    BEFORE_FILTER("before-filter"), //$NON-NLS-1$

    @Label( standard = "After Filter" )
    @EnumSerialization( caseSensitive = true, primary = "after-filter" )
    AFTER_FILTER("after-filter"); //$NON-NLS-1$

    private String text;

    private BeforeAfterFilterType( String primary )
    {
        text = primary;
    }

    public String getText()
    {
        return text;
    }
}
