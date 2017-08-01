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

package com.liferay.ide.swtbot.liferay.ui.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import com.liferay.ide.swtbot.liferay.ui.page.button.CreateLifeayProjectToolbarDropDownButton;
import com.liferay.ide.swtbot.liferay.ui.page.button.NewToolbarDropDownButton;
import com.liferay.ide.swtbot.liferay.ui.page.view.CodeUpgradeView;
import com.liferay.ide.swtbot.ui.Eclipse;
import com.liferay.ide.swtbot.ui.page.Perspective;

/**
 * @author Terry Jia
 */
public class LiferayIDE extends Eclipse
{

    private CodeUpgradeView codeUpgradeView;
    private CreateLifeayProjectToolbarDropDownButton createLiferayProjectToolbar;
    protected Perspective liferayPerspective;
    protected Perspective liferayWorkspacePerspective;
    protected NewToolbarDropDownButton newBtn;

    public LiferayIDE( SWTWorkbenchBot bot )
    {
        super( bot );

        createLiferayProjectToolbar = new CreateLifeayProjectToolbarDropDownButton( bot );
        codeUpgradeView = new CodeUpgradeView( bot, LABEL_LIFERAY_CODE_UPGRADE );

        liferayPerspective = new Perspective( bot, LABEL_LIFERAY_PLUGINS );
        liferayWorkspacePerspective = new Perspective( bot, LABEL_LIFERAY_WORKSPACE );

        newBtn = new NewToolbarDropDownButton( bot );
    }

    public CreateLifeayProjectToolbarDropDownButton getCreateLiferayProjectToolbar()
    {
        return createLiferayProjectToolbar;
    }

    public Perspective getLiferayPerspective()
    {
        return liferayPerspective;
    }

    public Perspective getLiferayWorkspacePerspective()
    {
        return liferayWorkspacePerspective;
    }

    public NewToolbarDropDownButton getNewBtn()
    {
        return newBtn;
    }

    public CodeUpgradeView showCodeUpgradeView()
    {
        try
        {
            codeUpgradeView.show();
        }
        catch( Exception e )
        {
            otherMenu.click();

            showViewDialog.getSearch().setText( ( "Liferay Code Upgrade" ) );

            showViewDialog.confirm();

            codeUpgradeView.show();
        }

        return codeUpgradeView;
    }

}
