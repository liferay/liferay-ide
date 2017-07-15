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

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.LiferayPortletWizardUI;
import com.liferay.ide.swtbot.ui.page.SelectionDialog;
import com.liferay.ide.swtbot.ui.page.Table;

/**
 * @author Ashley Yuan
 */
public class SuperClassSelectionDialog extends SelectionDialog implements LiferayPortletWizardUI
{

    private Table availableSuperClasses;

    public SuperClassSelectionDialog( SWTBot bot, String title, int labelIndex )
    {
        super( bot, title, labelIndex );

        availableSuperClasses = new Table( bot, 0 );
    }

    public Table getAvailableSuperClasses()
    {
        return availableSuperClasses;
    }

}
