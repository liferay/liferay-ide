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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.condition.EditorActiveCondition;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class EditorPO extends AbstractPO
{

    private final String _editorName;

    public EditorPO( SWTBot bot, String editorName )
    {
        super( bot );

        _editorName = editorName;
    }

    public void close()
    {
        getEditor().close();

        bot.waitUntil( new EditorActiveCondition( _editorName, false ) );
    }

    protected SWTBotEditor getEditor()
    {
        return ( (SWTWorkbenchBot) bot ).editorByTitle( _editorName );
    }

    public boolean isActive()
    {
        return getEditor().isActive();
    }

    public void setFocus()
    {
        getEditor().setFocus();
    }

    public void save()
    {
        getEditor().save();
    }

    public void waitForPageToOpen()
    {
        bot.waitUntil( new EditorActiveCondition( _editorName, true ) );
    }
}
