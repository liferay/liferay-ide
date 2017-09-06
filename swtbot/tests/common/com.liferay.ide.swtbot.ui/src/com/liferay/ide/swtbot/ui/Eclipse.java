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

import com.liferay.ide.swtbot.ui.eclipse.page.ErrorLogView;
import com.liferay.ide.swtbot.ui.eclipse.page.PackageExplorerView;
import com.liferay.ide.swtbot.ui.eclipse.page.ProgressView;
import com.liferay.ide.swtbot.ui.eclipse.page.ProjectExplorerView;
import com.liferay.ide.swtbot.ui.eclipse.page.ServersView;
import com.liferay.ide.swtbot.ui.eclipse.page.TextDialog;
import com.liferay.ide.swtbot.ui.page.BasePageObject;
import com.liferay.ide.swtbot.ui.page.Browser;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Menu;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Vicky Wang
 */
public class Eclipse extends BasePageObject
{

    protected final PackageExplorerView packageExporerView;
    protected final Tree projectTree;
    protected final View welcomeView;
    protected final ProgressView progressView;
    protected final Menu otherMenu;
    protected final TextDialog showViewDialog;
    protected final ErrorLogView errorLogView;
    protected final Menu fileMenu;
    protected final ProjectExplorerView projectExplorerView;
    protected final Menu preferencesMenu;
    protected final ServersView serversView;
    protected final Browser browser;

    public Eclipse( final SWTWorkbenchBot bot )
    {
        super( bot );

        packageExporerView = new PackageExplorerView( bot );
        welcomeView = new View( bot, WELCOME );
        progressView = new ProgressView( bot );
        projectTree = new Tree( bot );
        fileMenu = new Menu( bot, FILE );

        String[] otherLabel = { WINDOW, SHOW_VIEW, OTHER };
        String[] preferencesLabel = { WINDOW, PREFERENCES };

        preferencesMenu = new Menu( bot, preferencesLabel );
        otherMenu = new Menu( bot, otherLabel );

        showViewDialog = new TextDialog( bot );
        errorLogView = new ErrorLogView( bot );
        projectExplorerView = new ProjectExplorerView( bot );
        serversView = new ServersView( bot );
        browser = new Browser( bot );
    }

    public void closeShell( String title )
    {
        Dialog shell = new Dialog( bot, title );

        shell.closeIfOpen();
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

    public ProjectExplorerView getProjectExplorerView()
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
            showViewDialog.getText().setText( ( "Package Explorer" ) );

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

            showViewDialog.getText().setText( ( "Servers" ) );

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

            showViewDialog.getText().setText( ( PROGRESS ) );

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

            showViewDialog.getText().setText( ( ERROR_LOG ) );

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
