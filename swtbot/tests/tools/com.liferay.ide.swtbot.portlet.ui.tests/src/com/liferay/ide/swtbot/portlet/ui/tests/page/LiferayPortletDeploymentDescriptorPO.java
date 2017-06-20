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

package com.liferay.ide.swtbot.portlet.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */

public class LiferayPortletDeploymentDescriptorPO extends WizardPO implements LiferayPortletWizard, ProjectWizard
{

    private TextPO _iconText;
    private CheckBoxPO _allowMultipleInstancesCheckbox;
    private TextPO _cssText;
    private TextPO _javaScriptText;
    private TextPO _cssClassWrapperText;
    private ComboBoxPO _displayCategoryCombobox;
    private CheckBoxPO _addToControlPanelCheckbox;
    private ComboBoxPO _entryCategoryCombobox;
    private TextPO _entryWeightText;
    private CheckBoxPO _createEntryClassCheckbox;
    private TextPO _entryClassText;
    private ButtonPO _browseIconButton;
    private ButtonPO _browseCssButton;
    private ButtonPO _browseJavaScriptButton;

    public LiferayPortletDeploymentDescriptorPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, INDEX_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );
    }

    public LiferayPortletDeploymentDescriptorPO( SWTBot bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );
    }

    public LiferayPortletDeploymentDescriptorPO(
        SWTBot bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );

        _iconText = new TextPO( bot, LABEL_ICON );
        _allowMultipleInstancesCheckbox = new CheckBoxPO( bot, CHECKBOX_ALLOW_MULTIPLE_INSTANCES );
        _cssText = new TextPO( bot, LABEL_CSS );
        _javaScriptText = new TextPO( bot, LABEL_JAVASCRIPT );
        _cssClassWrapperText = new TextPO( bot, LABEL_CSS_CLASS_WRAPPER );
        _displayCategoryCombobox = new ComboBoxPO( bot, LABEL_DISPLAY_CATEGORY );
        _addToControlPanelCheckbox = new CheckBoxPO( bot, CHECKBOX_ADD_TO_CONTROL_PANEL );
        _entryCategoryCombobox = new ComboBoxPO( bot, LABEL_ENTRY_CATEGORY );
        _entryWeightText = new TextPO( bot, LABEL_ENTRY_WEIGHT );
        _createEntryClassCheckbox = new CheckBoxPO( bot, CHECKBOX_CREATE_ENTRY_CLASS );
        _entryClassText = new TextPO( bot, LABEL_ENTRY_CLASS );
        _browseIconButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT, 0 );
        _browseCssButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT, 1 );
        _browseJavaScriptButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT, 2 );
    }

    public ButtonPO get_browseIconButton()
    {
        return _browseIconButton;
    }

    public ButtonPO get_browseCssButton()
    {
        return _browseCssButton;
    }

    public ButtonPO get_browseJavaScriptButton()
    {
        return _browseJavaScriptButton;
    }

    public String getCssClassWrapperText()
    {
        return _cssClassWrapperText.getText();
    }

    public String getCssText()
    {
        return _cssText.getText();
    }

    public String[] getDisplayCategoryAvailableComboValues()
    {
        return _displayCategoryCombobox.getAvailableComboValues();
    }

    public String getDisplayCategoryCombobox()
    {
        return _displayCategoryCombobox.getText();
    }

    public String[] getEntryCategoryAvailableComboValues()
    {
        return _entryCategoryCombobox.getAvailableComboValues();
    }

    public String getEntryCategoryCombobox()
    {
        return _entryCategoryCombobox.getText();
    }

    public String getEntryClassText()
    {
        return _entryClassText.getText();
    }

    public String getEntryWeightText()
    {
        return _entryWeightText.getText();
    }

    public String getIconText()
    {
        return _iconText.getText();
    }

    public String getJavaScriptText()
    {
        return _javaScriptText.getText();
    }

    public boolean isAddToControlPanelChecked()
    {
        return _addToControlPanelCheckbox.isChecked();
    }

    public boolean isAllowMultipleInstancesChecked()
    {
        return _allowMultipleInstancesCheckbox.isChecked();
    }

    public boolean isCreateEntryClassChecked()
    {
        return _createEntryClassCheckbox.isChecked();
    }

    public boolean isCreateEntryClassEnabled()
    {
        return _createEntryClassCheckbox.isEnabled();
    }

    public boolean isEntryCategoryEnabled()
    {
        return _entryCategoryCombobox.isEnabled();
    }

    public boolean isEntryClassEnabled()
    {
        return _entryClassText.isEnabled();
    }

    public boolean isEntryWeightEnabled()
    {
        return _entryWeightText.isEnabled();
    }

    public void setCssClassWrapperText( String cssClassWrapperText )
    {
        _cssClassWrapperText.setText( cssClassWrapperText );;
    }

    public void setCssText( String cssText )
    {
        _cssText.setText( cssText );
    }

    public void setDisplayCategoryCombobox( String displayCategoryCombobox )
    {
        String[] avilableDisplayCategoryValues = getDisplayCategoryAvailableComboValues();

        for( String mycategory : avilableDisplayCategoryValues )
        {
            if( mycategory.equals( displayCategoryCombobox ) )
            {
                _displayCategoryCombobox.setSelection( displayCategoryCombobox );

                return;
            }
        }

        _displayCategoryCombobox.setText( displayCategoryCombobox );
    }

    public void setEntryCategoryCombobox( String entryCategoryCombobox )
    {
        String[] avilableEntryCategoryValues = getEntryCategoryAvailableComboValues();

        for( String myEnteryCategory : avilableEntryCategoryValues )
        {
            if( myEnteryCategory.equals( entryCategoryCombobox ) )
            {
                _entryCategoryCombobox.setSelection( entryCategoryCombobox );

                return;
            }
        }

        _entryCategoryCombobox.setText( entryCategoryCombobox );
    }

    public void setEntryClassText( String entryClassText )
    {
        this._entryClassText.setText( entryClassText );
    }

    public void setEntryWeightText( String entryWeightText )
    {
        this._entryWeightText.setText( entryWeightText );
    }

    public void setIconText( String icon )
    {
        this._iconText.setText( icon );;
    }

    public void setJavaScriptText( String javaScript )
    {
        this._javaScriptText.setText( javaScript );
    }

    public void specifyLiferayDisplay(
        String displaycategory, boolean addToControlPanel, String entryCategory, String entryWeight,
        boolean createEntryClass, String entryClass )
    {
        String[] avilableDisplayCategoryValues = getDisplayCategoryAvailableComboValues();

        if( displaycategory != null )
        {

            for( String myDisplayCategory : avilableDisplayCategoryValues )
            {

                if( myDisplayCategory.equals( displaycategory ) )
                {
                    _displayCategoryCombobox.setSelection( displaycategory );
                }
            }

            _displayCategoryCombobox.setText( displaycategory );

        }

        if( addToControlPanel )
        {
            _addToControlPanelCheckbox.select();

            if( createEntryClass )
            {
                _createEntryClassCheckbox.select();
            }
            else
            {
                _createEntryClassCheckbox.deselect();
            }

        }
        else
        {
            _addToControlPanelCheckbox.deselect();
        }

        if( entryCategory != null )
        {
            String[] avilableEntryCategoryValues = getDisplayCategoryAvailableComboValues();

            for( String myEntryCategory : avilableEntryCategoryValues )
            {
                if( myEntryCategory.equals( entryCategory ) )
                {
                    _entryCategoryCombobox.setSelection( entryCategory );
                }
            }
            _entryCategoryCombobox.setText( entryCategory );

        }

        if( entryWeight != null )
        {
            _entryWeightText.setText( entryWeight );
        }

        if( !addToControlPanel )
        {

            _addToControlPanelCheckbox.select();
            if( createEntryClass )
            {
                _createEntryClassCheckbox.select();
            }
            else
            {
                _createEntryClassCheckbox.deselect();
            }
            _addToControlPanelCheckbox.deselect();
        }

        if( entryClass != null )
        {
            _entryClassText.setText( entryClass );
        }
    }

    public void specifyLiferayPortletInfo(
        String liferayPortletIcon, boolean allowMultipleInstances, String liferayPortletCss,
        String liferayPortletJavaScript, String cssClassWrapper )
    {
        if( liferayPortletIcon != null )
        {
            _iconText.setText( liferayPortletIcon );
        }

        if( allowMultipleInstances )
        {
            _allowMultipleInstancesCheckbox.select();
        }
        else
        {
            _allowMultipleInstancesCheckbox.deselect();
        }

        if( liferayPortletCss != null )
        {
            _cssText.setText( liferayPortletCss );
        }

        if( liferayPortletJavaScript != null )
        {
            _javaScriptText.setText( liferayPortletJavaScript );
        }

        if( cssClassWrapper != null )
        {
            _cssClassWrapperText.setText( cssClassWrapper );
        }
    }

}
