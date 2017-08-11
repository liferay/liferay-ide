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

package com.liferay.ide.swtbot.liferay.ui.util;

/**
 * @author Terry Jia
 */
public class ValidationMsg
{

    private String expect;
    private String input;

    public String getExpect()
    {
        return expect;
    }

    public String getInput()
    {
        return input;
    }

    public void setExpect( String expect )
    {
        this.expect = expect;
    }

    public void setInput( String input )
    {
        this.input = input;
    }

}
