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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.CreateLayouttplWizard;
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Li Lu
 */
public class ChooseInitialTemplateWizard extends Wizard implements CreateLayouttplWizard
{

    public ChooseInitialTemplateWizard( SWTBot bot )
    {
        super( bot, TEXT_BLANK, INDEX_LAYOUTTPL_VALIDATION_MESSAGE2 );
    }

    public boolean isRadioSelected( int index )
    {
        return new Radio( bot, index ).isSelected();
    }

    public boolean isRadioSelected( String label )
    {
        return new Radio( bot, label ).isSelected();
    }

    public void selectRadio( int index )
    {
        new Radio( bot, index ).click();
    }

    public void selectRadio( String label )
    {
        new Radio( bot, label ).click();
    }

}
