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

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.ErrorLogViewPO;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.NewToolbarDropDownButtonPO;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.PackageExplorerViewPO;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.ProgressViewPO;
import com.liferay.ide.swtbot.ui.tests.liferay.page.CodeUpgradeViewPO;
import com.liferay.ide.swtbot.ui.tests.liferay.page.CreateLifeayProjectToolbarDropDownButtonPO;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Vicky Wang
 */
public class EclipsePO extends AbstractPO implements UIBase
{

    protected Keyboard keyPress = KeyboardFactory.getAWTKeyboard();
    protected KeyStroke ctrl = KeyStroke.getInstance( SWT.CTRL, 0 );
    protected KeyStroke M = KeyStroke.getInstance( 'M' );

    private CreateLifeayProjectToolbarDropDownButtonPO _createLiferayProjectToolbar;
    private NewToolbarDropDownButtonPO _newToolbar;
    private PerspectivePO _liferayPerspective;
    private PerspectivePO _liferayWorkspacePerspective;
    private PackageExplorerViewPO _packageExporerView;
    private TreePO _projectTree;
    private ViewPO _welcomeView;
    private ProgressViewPO _progressView;
    private MenuPO _otherMenu;
    private ShowViewDialogPO _showViewDialog;
    private ErrorLogViewPO _errorLogView;
    private MenuPO _fileMenu;
    private ViewPO _projectExplorerView;
    private CodeUpgradeViewPO _codeUpgradeView;
    private MenuPO _preferencesMenu;

    public EclipsePO( SWTWorkbenchBot bot )
    {
        super( bot );

        _createLiferayProjectToolbar = new CreateLifeayProjectToolbarDropDownButtonPO( bot );
        _packageExporerView = new PackageExplorerViewPO( bot );
        _welcomeView = new ViewPO( bot, LABEL_WELCOME );
        _progressView = new ProgressViewPO( bot );
        _liferayPerspective = new PerspectivePO( bot, LABEL_LIFERAY_PLUGINS );
        _liferayWorkspacePerspective = new PerspectivePO( bot, LABEL_LIFERAY_WORKSPACE );
        _projectTree = new TreePO( bot );
        _fileMenu = new MenuPO( bot, MENU_FILE );

        String[] otherLabel = { LABEL_WINDOW, LABEL_SHOW_VIEW, LABEL_OTHER };
        String[] preferencesLabel = { LABEL_WINDOW, LABEL_PREFERENCES };

        _preferencesMenu = new MenuPO( bot, preferencesLabel );
        _otherMenu = new MenuPO( bot, otherLabel );

        _showViewDialog = new ShowViewDialogPO( bot );
        _errorLogView = new ErrorLogViewPO( bot );
        _newToolbar = new NewToolbarDropDownButtonPO( bot );

        _projectExplorerView = new ViewPO( bot, LABEL_PROJECT_EXPLORER );
        _liferayWorkspacePerspective = new PerspectivePO( bot, LABEL_LIFERAY_WORKSPACE );
        _codeUpgradeView = new CodeUpgradeViewPO( bot, LABEL_LIFERAY_CODE_UPGRADE );
    }

    public void closeShell( String title )
    {
        DialogPO shell = new DialogPO( bot, title );

        shell.closeIfOpen();
    }

    public NewToolbarDropDownButtonPO getNewToolbar()
    {
        return _newToolbar;
    }

    public MenuPO getFileMenu()
    {
        return _fileMenu;
    }

    public CreateLifeayProjectToolbarDropDownButtonPO getCreateLiferayProjectToolbar()
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

    public PackageExplorerViewPO getPackageExporerView()
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

    public PackageExplorerViewPO showPackageExporerView()
    {
        _packageExporerView.show();

        return _packageExporerView;
    }

    public CodeUpgradeViewPO showCodeUpgradeView()
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

    public ProgressViewPO showProgressView()
    {
        try
        {
            _progressView.show();
        }
        catch( Exception e )
        {
            _otherMenu.click();

            _showViewDialog.setSearchText( LABEL_PROGRESS );

            sleep( 500 );

            _showViewDialog.confirm();

            _progressView.show();
        }

        return _progressView;
    }

    public ErrorLogViewPO showErrorLogView()
    {
        try
        {
            _errorLogView.show();
        }
        catch( Exception e )
        {
            _otherMenu.click();

            _showViewDialog.setSearchText( LABEL_ERROR_LOG );

            sleep( 500 );

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
