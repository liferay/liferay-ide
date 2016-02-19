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

package com.liferay.ide.project.ui.tests.page;

import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.LabelPageObject;
import com.liferay.ide.ui.tests.swtbot.page.RadioPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class SelectPortletFrameworkPageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    private LabelPageObject<SWTBot> displayNameLabel;

    private TextPageObject<SWTBot> displayNameText;
    private RadioPageObject<SWTBot> jsfRadio;
    private RadioPageObject<SWTBot> liferayMVCRadio;
    private RadioPageObject<SWTBot> portletFrameworkRadio;
    private LabelPageObject<SWTBot> portletNameLabel;
    private TextPageObject<SWTBot> portletNameText;
    private RadioPageObject<SWTBot> springMVCRadio;
    private RadioPageObject<SWTBot> vaadinRadio;

    public SelectPortletFrameworkPageObject( T bot )
    {
        this( bot, TEXT_BLANK, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public SelectPortletFrameworkPageObject( T bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public SelectPortletFrameworkPageObject( T bot, String title, int indexValidationMessage )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL );

        liferayMVCRadio = new RadioPageObject<SWTBot>( bot, TEXT_LIFERAY_MVC_FRAMEWORK );
        jsfRadio = new RadioPageObject<SWTBot>( bot, TEXT_JSF_FRAMEWORK );
        springMVCRadio = new RadioPageObject<SWTBot>( bot, TEXT_SPRING_MVC_FRAMEWORK );
        vaadinRadio = new RadioPageObject<SWTBot>( bot, TEXT_VAADIN_FRAMEWORK );
        portletNameText = new TextPageObject<SWTBot>( bot, LABEL_PORTLET_NAME );
        displayNameText = new TextPageObject<SWTBot>( bot, LABEL_DISPLAY_NAME );
        portletNameLabel = new LabelPageObject<SWTBot>( bot, LABEL_PORTLET_NAME );
        displayNameLabel = new LabelPageObject<SWTBot>( bot, LABEL_DISPLAY_NAME );
    }

    public SelectPortletFrameworkPageObject( T bot, String title, String radio )
    {
        this( bot, title );

        selectFramework( radio );
    }

    public LabelPageObject<SWTBot> getDisplayNameLabel()
    {
        return displayNameLabel;
    }

    public TextPageObject<SWTBot> getDisplayNameText()
    {
        return displayNameText;
    }

    public RadioPageObject<SWTBot> getJsfRadio()
    {
        return jsfRadio;
    }

    public RadioPageObject<SWTBot> getLiferayMVCRadio()
    {
        return liferayMVCRadio;
    }

    public RadioPageObject<SWTBot> getPortletFrameworkRadio()
    {
        return portletFrameworkRadio;
    }

    public LabelPageObject<SWTBot> getPortletNameLabel()
    {
        return portletNameLabel;
    }

    public TextPageObject<SWTBot> getPortletNameText()
    {
        return portletNameText;
    }

    public RadioPageObject<SWTBot> getSpringMVCRadio()
    {
        return springMVCRadio;
    }

    public RadioPageObject<SWTBot> getVaadinRadio()
    {
        return vaadinRadio;
    }

    public boolean IsLiferayMVCRadioSelected()
    {
        return liferayMVCRadio.isSelected();
    }

    public boolean isVisibleProjectNameAndDisplayName()
    {

        if( portletNameLabel.isVisible( LABEL_PORTLET_NAME ) && displayNameLabel.isVisible( LABEL_DISPLAY_NAME ) )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    public void selectFramework( String radio )
    {
        if( radio.equals( liferayMVCRadio.getLabel() ) )
        {
            liferayMVCRadio.click();
        }
        else if( radio.equals( jsfRadio.getLabel() ) )
        {
            jsfRadio.click();
        }
        else if( radio.equals( springMVCRadio.getLabel() ) )
        {
            springMVCRadio.click();
        }
        else if( radio.equals( vaadinRadio.getLabel() ) )
        {
            vaadinRadio.click();
        }
    }

    public void setProjectNameAndDisplayName( String projectName, String displayName )
    {
        if( isVisibleProjectNameAndDisplayName() )
        {
            portletNameText.setText( projectName );
            displayNameText.setText( displayName );
        }
    }
}
