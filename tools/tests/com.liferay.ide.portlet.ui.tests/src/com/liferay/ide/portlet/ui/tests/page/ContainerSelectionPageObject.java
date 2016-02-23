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

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class ContainerSelectionPageObject extends AbstractSelectionPageObject<SWTBot> implements LiferayPortletWizard
{

    public ContainerSelectionPageObject( SWTBot bot, String title, int labelIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK, labelIndex );
    }

}
