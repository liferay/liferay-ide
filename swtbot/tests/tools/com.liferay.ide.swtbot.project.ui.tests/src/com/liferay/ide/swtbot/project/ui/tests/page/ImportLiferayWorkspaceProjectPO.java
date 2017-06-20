
package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.ImportLiferayWorkspaceProject;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

public class ImportLiferayWorkspaceProjectPO extends WizardPO implements ImportLiferayWorkspaceProject
{

    private TextPO _workspaceLocation;
    private TextPO _buildTypeText;
    private TextPO _serverName;
    private TextPO _bundleUrl;
    private CheckBoxPO _downloadLiferaybundle;
    private CheckBoxPO _addProjectToWorkingSet;
    private ButtonPO _backButton;
    private ButtonPO _nextButton;
    private ButtonPO _finishButton;
    private ButtonPO _cancelButton;

    public ImportLiferayWorkspaceProjectPO( SWTBot bot, String title, int validationMessageIndex )
    {

        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );
        _buildTypeText = new TextPO( bot, LABEL_BUILD_TYPE );
        _workspaceLocation = new TextPO( bot, TEXT_WORKSPACE_LOCATION );
        _downloadLiferaybundle = new CheckBoxPO( bot, CHECKBOX_DOWNLOAD_LIFERAY_BUNDLE );
        _serverName = new TextPO( bot, TEXT_SERVER_NAME );
        _bundleUrl = new TextPO( bot, TEXT_BUNDLE_URL );
        _addProjectToWorkingSet = new CheckBoxPO( bot, CHECKBOX_ADD_PROJECT_TO_WORKING_SET );
        _backButton = new ButtonPO( bot, BUTTON_BACK );
        _nextButton = new ButtonPO( bot, BUTTON_NEXT );
        _finishButton = new ButtonPO( bot, BUTTON_FINISH );
        _cancelButton = new ButtonPO( bot, BUTTON_CANCEL );
    }

    public ButtonPO getBackButton()
    {
        return _backButton;
    }

    public ButtonPO getNextButton()
    {
        return _nextButton;
    }

    public ButtonPO getFinishButton()
    {
        return _finishButton;
    }

    public ButtonPO getCancelButton()
    {
        return _cancelButton;
    }

    public CheckBoxPO getAddProjectToWorkingSet()
    {
        return _addProjectToWorkingSet;
    }

    public CheckBoxPO getDownloadLiferaybundle()
    {
        return _downloadLiferaybundle;
    }

    public void setDownloadLiferaybundle( CheckBoxPO downloadLiferaybundle )
    {
        this._downloadLiferaybundle = downloadLiferaybundle;
    }

    public TextPO getWorkspaceLocation()
    {
        return _workspaceLocation;
    }

    public void setWorkspaceLocation( String workspaceLocation )
    {
        this._workspaceLocation.setText( workspaceLocation );;
    }

    public TextPO getBuildTypeText()
    {
        return _buildTypeText;
    }

    public void setBuildTypeText( String buildTypeText )
    {
        this._buildTypeText.setText( buildTypeText );
    }

    public TextPO getServerName()
    {
        return _serverName;
    }

    public void setServerName( String serverName )
    {
        this._serverName.setText( serverName );
    }

    public TextPO getBundleUrl()
    {
        return _bundleUrl;
    }

    public void setBundleUrl( String bundleUrl )
    {
        this._bundleUrl.setText( bundleUrl );
    }

}
