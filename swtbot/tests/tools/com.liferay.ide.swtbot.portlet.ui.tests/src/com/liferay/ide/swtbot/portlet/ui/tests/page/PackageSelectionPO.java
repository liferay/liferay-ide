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

package com.liferay.ide.swtbot.portlet.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.swtbot.ui.tests.page.SelectionDialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TablePO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Ashley Yuan
 */
public class PackageSelectionPO extends SelectionDialogPO implements LiferayPortletWizard
{

    private TextPO _packageToSelectText;
    private TablePO _availablePackagesTable;

    public PackageSelectionPO( SWTBot bot, String title, int labelIndex )
    {
        super( bot, title, labelIndex );

        _packageToSelectText = new TextPO( bot );
        _availablePackagesTable = new TablePO( bot, 0 );
    }

    public void clickPackage( int packageRow )
    {
        _availablePackagesTable.click( packageRow );
    }

    public void clickPackage( String packageName )
    {
        _availablePackagesTable.click( packageName );
    }

    public TextPO getPackageToSelectText()
    {
        return _packageToSelectText;
    }

}
