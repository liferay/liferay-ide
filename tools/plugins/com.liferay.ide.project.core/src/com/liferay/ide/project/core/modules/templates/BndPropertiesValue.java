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

package com.liferay.ide.project.core.modules.templates;

/**
 * @author Simon Jiang
 */

public class BndPropertiesValue
{

    private String originalValue;
    private String formatedValue;
    private boolean isMultiLine;

    public BndPropertiesValue()
    {
    }

    public BndPropertiesValue( String value )
    {
        this.formatedValue = value;
        this.originalValue = value;
    }

    public BndPropertiesValue( String formatedValue, String originalValue )
    {
        this.formatedValue = formatedValue;
        this.originalValue = originalValue;
    }

    public boolean isMultiLine()
    {
        return isMultiLine;
    }

    public void setMultiLine( boolean isMultiLine )
    {
        this.isMultiLine = isMultiLine;
    }

    private int keyIndex;

    public int getKeyIndex()
    {
        return keyIndex;
    }

    public void setKeyIndex( int keyIndex )
    {
        this.keyIndex = keyIndex;
    }

    public String getOriginalValue()
    {
        return originalValue;
    }

    public void setOriginalValue( String originalValue )
    {
        this.originalValue = originalValue;
    }

    public String getFormatedValue()
    {
        return formatedValue;
    }

    public void setFormatedValue( String formatedValue )
    {
        this.formatedValue = formatedValue;
    }

}
