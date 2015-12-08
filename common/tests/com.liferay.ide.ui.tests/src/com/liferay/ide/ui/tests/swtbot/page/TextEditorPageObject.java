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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * base implementation of the ITextEditorPageObject interface
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class TextEditorPageObject extends EditorPageObject
{

    public TextEditorPageObject( SWTWorkbenchBot bot, String name )
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
