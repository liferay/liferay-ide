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
 *******************************************************************************/

package com.liferay.ide.portlet.jsf.ui;

/**
 * @author Greg Amerson
 */
public class JSFPortletTemplateContextTypeIds
{

    public static final String ALL = getAll();

    public static final String ATTRIBUTE = getAttribute();

    public static final String ATTRIBUTE_VALUE = getAttributeValue();

    public static final String NEW = getNew();

    public static final String NEW_TAG = "tag_new"; //$NON-NLS-1$

    public static final String TAG = getTag();

    private static String getAll()
    {
        return getPrefix() + "_all"; //$NON-NLS-1$
    }

    private static String getAttribute()
    {
        return getPrefix() + "_attribute"; //$NON-NLS-1$
    }

    private static String getAttributeValue()
    {
        return getPrefix() + "_attribute_value"; //$NON-NLS-1$
    }

    private static String getNew()
    {
        return getPrefix() + "_new"; //$NON-NLS-1$
    }

    private static String getPrefix()
    {
        return "jsf_portlet"; //$NON-NLS-1$
    }

    private static String getTag()
    {
        return getPrefix() + "_tag"; //$NON-NLS-1$
    }
}
