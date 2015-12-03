
package com.liferay.ide.project.ui.tests;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.ui.tests.swtbot.page.impl.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.impl.WizardPageObject;

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
