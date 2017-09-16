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

package com.liferay.ide.swtbot.liferay.ui.page.dialog;

import com.liferay.ide.swtbot.ui.eclipse.page.TreeDialog;
import com.liferay.ide.swtbot.ui.page.Table;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 */
public class SuperClassSelectionDialog extends TreeDialog
{

    private Table availableSuperClasses;

    public SuperClassSelectionDialog( SWTWorkbenchBot bot )
    {
        super( bot );

        availableSuperClasses = new Table( bot );
    }

    public Table getAvailableSuperClasses()
    {
        return availableSuperClasses;
    }

}
