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

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class TextEditorPO extends EditorPO
{

    public TextEditorPO( SWTBot bot, String name )
    {
        super( bot, name );
    }

    public String getText()
    {
        return getEditor().toTextEditor().getText();
    }

    public void setText( String text )
    {
        getEditor().toTextEditor().setText( text );
    }

}
