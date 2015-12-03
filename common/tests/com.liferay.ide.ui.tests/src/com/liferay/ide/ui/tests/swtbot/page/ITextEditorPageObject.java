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

/**
 * interface for a page object representing an eclipse text editor
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */

public interface ITextEditorPageObject extends IEditorPageObject
{

    /**
     * @return the text of the editor
     */
    public String getText();

    /**
     * set the text of the editor
     *
     * @param text
     *            - the new text of the editor
     */
    public void setText( String text );
}
