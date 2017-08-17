/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.studio.ui;

import com.liferay.ide.project.ui.workspace.ImportLiferayWorkspaceWizard;

import java.util.Properties;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.internal.intro.impl.IntroPlugin;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class ImportLiferayWorkspaceAction implements IIntroAction
{

    @Override
    public void run( IIntroSite site, Properties params )
    {
        ImportLiferayWorkspaceWizard wizard = new ImportLiferayWorkspaceWizard();

        WizardDialog dialog = new WizardDialog( site.getShell(), wizard );

        if( dialog.open() == Window.OK )
        {
            IntroPlugin.closeIntro();
        }
    }

}
