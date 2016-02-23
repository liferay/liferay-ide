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

package com.liferay.ide.portlet.ui.tests.page;

import com.liferay.ide.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.ui.tests.swtbot.page.AbstractSelectionPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TablePageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class SuperclassSelectionPageObject extends AbstractSelectionPageObject<SWTBot> implements LiferayPortletWizard
{

    TextPageObject<SWTBot> superclassToSelectText;
    TablePageObject<SWTBot> availableSupercalssesTable;

    public SuperclassSelectionPageObject( SWTBot bot, String title, int labelIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK, labelIndex );

        superclassToSelectText = new TextPageObject<SWTBot>( bot );
        availableSupercalssesTable = new TablePageObject<SWTBot>( bot, 0 );
    }

    public void clickSuperclass( int superclassRow )
    {
        availableSupercalssesTable.click( superclassRow );
    }

    public void clickSuperclass( String superclassName )
    {
        availableSupercalssesTable.click( superclassName );
    }

    public void setSuperclassToSelect( String superclassName )
    {
        this.superclassToSelectText.setTextWithoutLabel( superclassName );
    }

}
