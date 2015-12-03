
package com.liferay.ide.project.ui.tests;

import com.liferay.ide.ui.tests.swtbot.page.impl.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.impl.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.impl.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.impl.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

public class CreateProjectWizardPageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    TextPageObject<SWTBot> projectNameText;
    TextPageObject<SWTBot> displayNameText;
    ComboBoxPageObject<SWTBot> buildTypeComboBox;
    TextPageObject<SWTBot> artifactVersionText;
    TextPageObject<SWTBot> groupIdText;
    TextPageObject<SWTBot> activeProfilesText;
    CheckBoxPageObject<SWTBot> useDefaultLoactionCheckBox;
    TextPageObject<SWTBot> location;
    ComboBoxPageObject<SWTBot> pluginTypeComboBox;
    CheckBoxPageObject<SWTBot> includeSimpleCodeCheckBox;
    CheckBoxPageObject<SWTBot> launchNewPortletWizardCheckBox;

    public CreateProjectWizardPageObject( T bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT );

        projectNameText = new TextPageObject<SWTBot>( bot, LABEL_PROJECT_NAME );
        displayNameText = new TextPageObject<SWTBot>( bot, LABEL_DISPLAY_NAME );
        buildTypeComboBox = new ComboBoxPageObject<SWTBot>( bot, LABEL_BUILD_TYPE );
        artifactVersionText = new TextPageObject<>( bot, LABEL_ACTIVE_PROFILES );
        groupIdText = new TextPageObject<SWTBot>( bot, LABEL_GROUP_ID );
        activeProfilesText = new TextPageObject<SWTBot>( bot, LABEL_ACTIVE_PROFILES );
        useDefaultLoactionCheckBox = new CheckBoxPageObject<SWTBot>( bot, LABEL_USE_DEFAULT_LOCATION );
        location = new TextPageObject<SWTBot>( bot, LABEL_LOCATION );
        pluginTypeComboBox = new ComboBoxPageObject<SWTBot>( bot, LABEL_PLUGIN_TYPE );
        includeSimpleCodeCheckBox = new CheckBoxPageObject<>( bot, LABEL_INCLUDE_SAMPLE_CODE );
        launchNewPortletWizardCheckBox =
            new CheckBoxPageObject<SWTBot>( bot, LABEL_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT );
    }

    public CreateProjectWizardPageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        projectNameText = new TextPageObject<SWTBot>( bot, LABEL_PROJECT_NAME );
        displayNameText = new TextPageObject<SWTBot>( bot, LABEL_DISPLAY_NAME );
        buildTypeComboBox = new ComboBoxPageObject<SWTBot>( bot, LABEL_BUILD_TYPE );
        artifactVersionText = new TextPageObject<>( bot, LABEL_ACTIVE_PROFILES );
        groupIdText = new TextPageObject<SWTBot>( bot, LABEL_GROUP_ID );
        activeProfilesText = new TextPageObject<SWTBot>( bot, LABEL_ACTIVE_PROFILES );
        useDefaultLoactionCheckBox = new CheckBoxPageObject<SWTBot>( bot, LABEL_USE_DEFAULT_LOCATION );
        location = new TextPageObject<SWTBot>( bot, LABEL_LOCATION );
        pluginTypeComboBox = new ComboBoxPageObject<SWTBot>( bot, LABEL_PLUGIN_TYPE );
        includeSimpleCodeCheckBox = new CheckBoxPageObject<>( bot, LABEL_INCLUDE_SAMPLE_CODE );
        launchNewPortletWizardCheckBox =
            new CheckBoxPageObject<SWTBot>( bot, LABEL_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT );
    }

    public void createSDKProject( String projectName )
    {
        createSDKProject( projectName, MENU_PORTLET );
    }

    public void createSDKProject( String projectName, String pluginType )
    {
        createSDKProject( projectName, "", pluginType );
    }

    public void createSDKProject( String projectName, String displayName, String pluginType )
    {
        createSDKProject( projectName, displayName, pluginType, false, false );
    }

    public void createSDKProject( String projectName, String pluginType, boolean includeSimpleCode )
    {
        createSDKProject( projectName, pluginType, includeSimpleCode, false );
    }

    public void createSDKProject(
        String projectName, String pluginType, boolean includeSimpleCode, boolean launchNewPortletWizard )
    {
        createSDKProject( projectName, "", pluginType, includeSimpleCode, launchNewPortletWizard );
    }

    public void createSDKProject(
        String projectName, String displayName, String pluginType, boolean includeSimpleCode,
        boolean launchNewPortletWizard )
    {
        projectNameText.setText( projectName );

        if( displayName != null && !displayName.equals( "" ) )
        {
            displayNameText.setText( displayName );
        }

        buildTypeComboBox.setSelection( MENU_BUILD_TYPE_ANT );

        pluginTypeComboBox.setSelection( pluginType );

        if( pluginType.equals( MENU_PORTLET ) || pluginType.equals( MENU_SERVICE_BUILDER_PORTLET ) )
        {
            if( includeSimpleCode )
            {
                includeSimpleCodeCheckBox.select();
            }
            else
            {
                includeSimpleCodeCheckBox.deselect();
            }
        }

        if( pluginType.equals( MENU_PORTLET ) )
        {
            if( launchNewPortletWizard )
            {
                launchNewPortletWizardCheckBox.select();
            }
            else
            {
                launchNewPortletWizardCheckBox.deselect();
            }
        }
    }

}
