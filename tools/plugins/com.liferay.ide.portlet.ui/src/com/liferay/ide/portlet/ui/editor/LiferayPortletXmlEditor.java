/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.ui.editor;


import com.liferay.ide.portlet.core.lfportlet.model.LiferayPortletXml;
import com.liferay.ide.ui.editor.LazyLoadingEditorForXml;

import org.eclipse.sapphire.ui.def.DefinitionLoader;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class LiferayPortletXmlEditor extends LazyLoadingEditorForXml
{
    public static final String ID = "com.liferay.ide.eclipse.portlet.ui.editor.LiferayPortletXmlEditor";

    public LiferayPortletXmlEditor()
    {
        super
        (
            LiferayPortletXml.TYPE,
            DefinitionLoader.sdef( LiferayPortletXmlEditor.class ).page( "liferay-portlet-app.editor" )
        );
    }

    @Override
    protected boolean shouldCreateFormPages()
    {
        return true;
    }
}
