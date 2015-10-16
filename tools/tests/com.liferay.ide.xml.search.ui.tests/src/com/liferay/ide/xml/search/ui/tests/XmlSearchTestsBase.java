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

package com.liferay.ide.xml.search.ui.tests;

import com.liferay.ide.project.core.tests.ProjectCoreBase;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class XmlSearchTestsBase extends ProjectCoreBase
{

    public static final String XML_REFERENCES_MARKER_TYPE = "org.eclipse.wst.xml.search.editor.validationMarker";

    private static final String BUNDLE_ID = "com.liferay.ide.xml.search.ui.tests";

    public static String MESSAGE_TYPE_HIERARCHY_INCORRECT = "Type hierarchy of class \"{0}\" is incorrect";

    protected String getBundleId()
    {
        return BUNDLE_ID;
    }

}