/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kay-Uwe Graw - initial API and implementation

 *******************************************************************************/

package com.liferay.ide.ui.tests.swtbot.page;

import com.liferay.ide.ui.tests.swtbot.condition.EditorActiveCondition;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

/**
 * base implementation of the IEditorPageObject interface
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class EditorPageObject extends AbstractPageObject<SWTWorkbenchBot>
{

    private final String name;

    public EditorPageObject( SWTWorkbenchBot bot, String name )
    {
        super( bot );

        this.name = name;
    }

    public void save()
    {
        getEditor().save();
    }

    public void close()
    {
        getEditor().close();

        bot.waitUntil( new EditorActiveCondition( name, false ) );
    }

    public void waitForPageToOpen()
    {
        bot.waitUntil( new EditorActiveCondition( name, true ) );
    }

    protected SWTBotEditor getEditor()
    {
        return bot.editorByTitle( name );
    }

    public boolean isActive()
    {
        return getEditor().isActive();
    }
}
