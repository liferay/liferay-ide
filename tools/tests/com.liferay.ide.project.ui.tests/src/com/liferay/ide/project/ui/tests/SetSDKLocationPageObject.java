
package com.liferay.ide.project.ui.tests;

import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

public class SetSDKLocationPageObject<T extends SWTBot> extends WizardPageObject<T>
{

    TextPageObject<SWTBot> sdkLocationText;

    public SetSDKLocationPageObject( T bot, String title )
    {
        super( bot, title, "Cancel", "Finish", "< Back", "Next >" );

        sdkLocationText = new TextPageObject<SWTBot>( bot, "SDK Location:" );
    }

    public void setSdkLocation( String sdkLocation )
    {
        sdkLocationText.setText( sdkLocation );
    }
}
