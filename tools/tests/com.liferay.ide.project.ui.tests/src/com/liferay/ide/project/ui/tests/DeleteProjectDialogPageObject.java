
package com.liferay.ide.project.ui.tests;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.DialogPageObject;

public class DeleteProjectDialogPageObject<T extends SWTBot> extends DialogPageObject<SWTBot>
    implements ProjectBuildAction
{

    CheckBoxPageObject<SWTBot> deleteFromDisk;

    public DeleteProjectDialogPageObject( SWTBot bot )
    {
        this( bot, DELETE_RESOURCE, BUTTON_CANCEL, BUTTON_OK );

    }

    public DeleteProjectDialogPageObject( SWTBot bot, String title, String cancelButtonText, String confirmButtonText )
    {
        super( bot, title, cancelButtonText, confirmButtonText );

        deleteFromDisk = new CheckBoxPageObject<SWTBot>( bot, DELETE_FROM_DISK );
    }

    public void confirmDeleteFromDisk()
    {
        deleteFromDisk.select();

        clickClosingButton( confirmButton() );
    }

}
