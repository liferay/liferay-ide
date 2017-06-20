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

package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.RadioPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public class SelectPortletFrameworkPO extends WizardPO implements ProjectWizard
{

    private TextPO _displayNameText;
    private RadioPO _jsfRadio;
    private RadioPO _liferayMVCRadio;
    private TextPO _portletNameText;
    private RadioPO _springMVCRadio;
    private RadioPO _vaadinRadio;

    public SelectPortletFrameworkPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public SelectPortletFrameworkPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public SelectPortletFrameworkPO( SWTBot bot, String title, int indexValidationMessage )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL );

        _liferayMVCRadio = new RadioPO( bot, LABEL_LIFERAY_MVC_FRAMEWORK );
        _jsfRadio = new RadioPO( bot, LABEL_JSF_FRAMEWORK );
        _springMVCRadio = new RadioPO( bot, LABEL_SPRING_MVC_FRAMEWORK );
        _vaadinRadio = new RadioPO( bot, LABEL_VAADIN_FRAMEWORK );
        _portletNameText = new TextPO( bot, LABEL_PORTLET_NAME );
        _displayNameText = new TextPO( bot, LABEL_DISPLAY_NAME );
    }

    public SelectPortletFrameworkPO( SWTBot bot, String title, String radio )
    {
        this( bot, title );

        selectFramework( radio );
    }

    public boolean IsLiferayMVCRadioSelected()
    {
        return _liferayMVCRadio.isSelected();
    }

    public boolean IsJSFRadioSelected()
    {
        return _jsfRadio.isSelected();
    }

    public boolean IsSpringMVCRadioSelected()
    {
        return _springMVCRadio.isSelected();
    }

    public boolean IsVaadinRadioSelected()
    {
        return _vaadinRadio.isSelected();
    }

    public void selectFramework( String radio )
    {
        if( radio.equals( _liferayMVCRadio.getLabel() ) )
        {
            _liferayMVCRadio.click();
        }
        else if( radio.equals( _jsfRadio.getLabel() ) )
        {
            _jsfRadio.click();
        }
        else if( radio.equals( _springMVCRadio.getLabel() ) )
        {
            _springMVCRadio.click();
        }
        else if( radio.equals( _vaadinRadio.getLabel() ) )
        {
            _vaadinRadio.click();
        }
    }

    public void setProjectNameAndDisplayName( String projectName, String displayName )
    {
        _portletNameText.setText( projectName );
        _displayNameText.setText( displayName );
    }

}
