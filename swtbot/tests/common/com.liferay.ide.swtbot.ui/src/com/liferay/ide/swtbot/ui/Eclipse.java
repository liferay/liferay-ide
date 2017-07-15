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

package com.liferay.ide.swtbot.ui;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import com.liferay.ide.swtbot.ui.eclipse.page.ErrorLogView;
import com.liferay.ide.swtbot.ui.eclipse.page.NewToolbarDropDownButton;
import com.liferay.ide.swtbot.ui.eclipse.page.PackageExplorerView;
import com.liferay.ide.swtbot.ui.eclipse.page.ProgressView;
import com.liferay.ide.swtbot.ui.eclipse.page.ServersView;
import com.liferay.ide.swtbot.ui.eclipse.page.ShowViewDialog;
import com.liferay.ide.swtbot.ui.page.BasePageObject;
import com.liferay.ide.swtbot.ui.page.Browser;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Menu;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.View;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Vicky Wang
 */
public class Eclipse extends BasePageObject
{

    protected NewToolbarDropDownButton newBtn;
    protected PackageExplorerView packageExporerView;
    protected Tree projectTree;
    protected View welcomeView;
    protected ProgressView progressView;
    protected Menu otherMenu;
    protected ShowViewDialog showViewDialog;
    protected ErrorLogView errorLogView;
    protected Menu fileMenu;
    protected View projectExplorerView;
    protected Menu preferencesMenu;
    protected ServersView serversView;
    protected Browser browser;

    public Eclipse( SWTWorkbenchBot bot )
    {
        super( bot );

        packageExporerView = new PackageExplorerView( bot );
        welcomeView = new View( bot, LABEL_WELCOME );
        progressView = new ProgressView( bot );
        projectTree = new Tree( bot );
        fileMenu = new Menu( bot, MENU_FILE );

        String[] otherLabel = { LABEL_WINDOW, LABEL_SHOW_VIEW, LABEL_OTHER };
        String[] preferencesLabel = { LABEL_WINDOW, LABEL_PREFERENCES };

        preferencesMenu = new Menu( bot, preferencesLabel );
        otherMenu = new Menu( bot, otherLabel );

        showViewDialog = new ShowViewDialog( bot );
        errorLogView = new ErrorLogView( bot );
        newBtn = new NewToolbarDropDownButton( bot );

        projectExplorerView = new View( bot, LABEL_PROJECT_EXPLORER );

        serversView = new ServersView( bot );
        browser = new Browser( bot );
    }

    public void closeShell( String title )
    {
        Dialog shell = new Dialog( bot, title );

        shell.closeIfOpen();
    }

    public NewToolbarDropDownButton getNewToolbar()
    {
        return newBtn;
    }

    public Menu getFileMenu()
    {
        return fileMenu;
    }

    public PackageExplorerView getPackageExporerView()
    {
        return packageExporerView;
    }

    public Tree getProjectTree()
    {
        return projectTree;
    }

    public View getWelcomeView()
    {
        return welcomeView;
    }

    public View getProjectExplorerView()
    {
        return projectExplorerView;
    }

    public PackageExplorerView showPackageExporerView()
    {
        try
        {
            packageExporerView.show();
        }
        catch( Exception e )
        {
            otherMenu.click();
            showViewDialog.getSearch().setText( ( "Package Explorer" ) );

            showViewDialog.confirm();

            packageExporerView.show();
        }
        return packageExporerView;
    }

    public ServersView showServersView()
    {
        try
        {
            serversView.show();
        }
        catch( Exception e )
        {
            otherMenu.click();

            showViewDialog.getSearch().setText( ( "Servers" ) );

            sleep( 100 );

            showViewDialog.confirm();

            serversView.show();
        }

        return serversView;
    }

    public ServersView getServersView()
    {
        return serversView;
    }

    public ProgressView showProgressView()
    {
        try
        {
            progressView.show();
        }
        catch( Exception e )
        {
            otherMenu.click();

            showViewDialog.getSearch().setText( ( LABEL_PROGRESS ) );

            sleep( 100 );

            showViewDialog.confirm();

            progressView.show();
        }

        return progressView;
    }

    public ErrorLogView showErrorLogView()
    {
        try
        {
            errorLogView.show();
        }
        catch( Exception e )
        {
            otherMenu.click();

            showViewDialog.getSearch().setText( ( LABEL_ERROR_LOG ) );

            sleep( 100 );

            showViewDialog.confirm();

            errorLogView.show();
        }

        return errorLogView;
    }

    public Browser getBrowser()
    {
        return browser;
    }

    public boolean hasProjects()
    {
        packageExporerView.show();

        try
        {
            return projectTree.hasItems();
        }
        catch( Exception e )
        {
        }

        return false;
    }

    public Editor getEditor( String fileName )
    {
        return new Editor( bot, fileName );
    }

    public Menu getPreferencesMenu()
    {
        return preferencesMenu;
    }

}
