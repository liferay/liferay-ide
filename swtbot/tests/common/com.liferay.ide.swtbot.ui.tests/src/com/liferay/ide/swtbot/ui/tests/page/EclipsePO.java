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

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.ErrorLogView;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.NewToolbarDropDownButton;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.PackageExplorerView;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.ProgressView;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.ServersView;
import com.liferay.ide.swtbot.ui.tests.liferay.page.CodeUpgradeView;
import com.liferay.ide.swtbot.ui.tests.liferay.page.CreateLifeayProjectToolbarDropDownButton;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Vicky Wang
 */
public class EclipsePO extends AbstractPO implements UIBase
{

    private CreateLifeayProjectToolbarDropDownButton _createLiferayProjectToolbar;
    private NewToolbarDropDownButton _newToolbar;
    private PerspectivePO _liferayPerspective;
    private PerspectivePO _liferayWorkspacePerspective;
    private PackageExplorerView _packageExporerView;
    private TreePO _projectTree;
    private ViewPO _welcomeView;
    private ProgressView _progressView;
    private MenuPO _otherMenu;
    private ShowViewDialogPO _showViewDialog;
    private ErrorLogView _errorLogView;
    private MenuPO _fileMenu;
    private ViewPO _projectExplorerView;
    private CodeUpgradeView _codeUpgradeView;
    private MenuPO _preferencesMenu;
    private ServersView _serversView;

    public EclipsePO( SWTWorkbenchBot bot )
    {
        super( bot );

        _createLiferayProjectToolbar = new CreateLifeayProjectToolbarDropDownButton( bot );
        _packageExporerView = new PackageExplorerView( bot );
        _welcomeView = new ViewPO( bot, LABEL_WELCOME );
        _progressView = new ProgressView( bot );
        _liferayPerspective = new PerspectivePO( bot, LABEL_LIFERAY_PLUGINS );
        _liferayWorkspacePerspective = new PerspectivePO( bot, LABEL_LIFERAY_WORKSPACE );
        _projectTree = new TreePO( bot );
        _fileMenu = new MenuPO( bot, MENU_FILE );

        String[] otherLabel = { LABEL_WINDOW, LABEL_SHOW_VIEW, LABEL_OTHER };
        String[] preferencesLabel = { LABEL_WINDOW, LABEL_PREFERENCES };

        _preferencesMenu = new MenuPO( bot, preferencesLabel );
        _otherMenu = new MenuPO( bot, otherLabel );

        _showViewDialog = new ShowViewDialogPO( bot );
        _errorLogView = new ErrorLogView( bot );
        _newToolbar = new NewToolbarDropDownButton( bot );

        _projectExplorerView = new ViewPO( bot, LABEL_PROJECT_EXPLORER );
        _liferayWorkspacePerspective = new PerspectivePO( bot, LABEL_LIFERAY_WORKSPACE );
        _codeUpgradeView = new CodeUpgradeView( bot, LABEL_LIFERAY_CODE_UPGRADE );
        _serversView = new ServersView( bot );
    }

    public void closeShell( String title )
    {
        DialogPO shell = new DialogPO( bot, title );

        shell.closeIfOpen();
    }

    public NewToolbarDropDownButton getNewToolbar()
    {
        return _newToolbar;
    }

    public MenuPO getFileMenu()
    {
        return _fileMenu;
    }

    public CreateLifeayProjectToolbarDropDownButton getCreateLiferayProjectToolbar()
    {
        return _createLiferayProjectToolbar;
    }

    public PerspectivePO getLiferayPerspective()
    {
        return _liferayPerspective;
    }

    public PerspectivePO getLiferayWorkspacePerspective()
    {
        return _liferayWorkspacePerspective;
    }

    public PackageExplorerView getPackageExporerView()
    {
        return _packageExporerView;
    }

    public TreePO getProjectTree()
    {
        return _projectTree;
    }

    public ViewPO getWelcomeView()
    {
        return _welcomeView;
    }

    public ViewPO getProjectExplorerView()
    {
        return _projectExplorerView;
    }

    public PackageExplorerView showPackageExporerView()
    {
        _packageExporerView.show();

        return _packageExporerView;
    }

    public CodeUpgradeView showCodeUpgradeView()
    {
        try
        {
            _codeUpgradeView.show();
        }
        catch( Exception e )
        {
            _otherMenu.click();

            _showViewDialog.setSearchText( "Liferay Code Upgrade" );

            sleep( 500 );

            _showViewDialog.confirm();

            _codeUpgradeView.show();
        }

        return _codeUpgradeView;
    }

    public ServersView showServersView()
    {
        try
        {
            _serversView.show();
        }
        catch( Exception e )
        {
            _otherMenu.click();

            _showViewDialog.setSearchText( "Servers" );

            sleep( 100 );

            _showViewDialog.confirm();

            _serversView.show();
        }

        return _serversView;
    }

    public ServersView getServersView()
    {
        return _serversView;
    }

    public ProgressView showProgressView()
    {
        try
        {
            _progressView.show();
        }
        catch( Exception e )
        {
            _otherMenu.click();

            _showViewDialog.setSearchText( LABEL_PROGRESS );

            sleep( 100 );

            _showViewDialog.confirm();

            _progressView.show();
        }

        return _progressView;
    }

    public ErrorLogView showErrorLogView()
    {
        try
        {
            _errorLogView.show();
        }
        catch( Exception e )
        {
            _otherMenu.click();

            _showViewDialog.setSearchText( LABEL_ERROR_LOG );

            sleep( 100 );

            _showViewDialog.confirm();

            _errorLogView.show();
        }

        return _errorLogView;
    }

    public boolean hasProjects()
    {
        _packageExporerView.show();

        try
        {
            return _projectTree.getWidget().hasItems();
        }
        catch( Exception e )
        {
        }

        return false;
    }

    public TextEditorPO getTextEditor( String fileName )
    {
        return new TextEditorPO( bot, fileName );
    }

    public MenuPO getPreferencesMenu()
    {
        return _preferencesMenu;
    }

}
