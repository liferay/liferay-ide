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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.migration.MigrationProblemsContainer;
import com.liferay.ide.project.ui.upgrade.CustomJspConverter;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
public class WelcomePage extends Page
{

    @SuppressWarnings( "unused" )
    public WelcomePage( final Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, WELCOME_PAGE_ID, false );

        Control createHorizontalSpacer = createHorizontalSpacer( this, 3 );
        Control createHorizontalSperator = createSeparator( this, 3 );

        Label blankLabel = new Label( this, SWT.LEFT_TO_RIGHT );
        Button reRunButton = SWTUtil.createButton( this, "Restart..." );
        reRunButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
                    UIUtil.getActiveShell(), "Restart code upgrade?",
                    "All previous configuration files will be deleted. Do you want to restart the code upgrade tool?" );

                if( openNewLiferayProjectWizard )
                {
                    CustomJspConverter.clearConvertResults();

                    try
                    {
                        MigrationProblemsContainer container =
                            UpgradeAssistantSettingsUtil.getObjectFromStore( MigrationProblemsContainer.class );

                        if( container != null )
                        {
                            UpgradeAssistantSettingsUtil.setObjectToStore( MigrationProblemsContainer.class, null );
                        }
                    }
                    catch( IOException excepiton )
                    {
                        ProjectUI.logError( excepiton );
                    }

                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                    UpgradeView view = (UpgradeView) UIUtil.findView( UpgradeView.ID );

                    CustomJspConverter.clearConvertResults();

                    UpgradeSettingsUtil.resetStoreProperties();

                    page.hideView( view );

                    try
                    {
                        page.showView( UpgradeView.ID );
                    }
                    catch( PartInitException e1 )
                    {
                        e1.printStackTrace();
                    }
                }

            }
        } );

        Label blankLabel2 = new Label( this, SWT.LEFT_TO_RIGHT );
        Button showAllPagesButton = SWTUtil.createButton( this, "Show All Pages..." );
        showAllPagesButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
                    UIUtil.getActiveShell(), "Show All Pages",
                    "If you fail to import projects, you can click this button to finish the upgrade prossess, "+
                    "as shown in the following steps:\n" +
                    "   1.upgrade SDK 6.2 to SDK 7.0 manually\n" +
                    "   or use blade cli to create a Liferay workspace for your SDK\n" +
                    "   2.import projects you want to upgrade into Eclipse workspace\n" +
                    "   3.choose \"yes\" to finish the following steps");

                if( openNewLiferayProjectWizard )
                {
                    UpgradeView.resumePages();

                    PageNavigateEvent event = new PageNavigateEvent();

                    event.setTargetPage( 1 );

                    for( PageNavigatorListener listener : naviListeners )
                    {
                        listener.onPageNavigate( event );
                    }

                    InitConfigureProjectPage importPage = UpgradeView.getPage( INIT_CONFIGURE_PROJECT_PAGE_ID,  InitConfigureProjectPage.class );
                    importPage.setNextPage( true );

                    dataModel.setImportFinished( true );

                }
            }
        } );
    }

    @Override
    public int getGridLayoutCount()
    {
        return 2;
    }

    @Override
    public boolean getGridLayoutEqualWidth()
    {
        return false;
    }

    @Override
    public String getPageTitle()
    {
        return "Welcome to Liferay Code Upgrade";
    }

    public void createSpecialDescriptor( Composite parent, int style )
    {
        final String desriptor =
            "Liferay Code Upgrade will help you to convert Liferay 6.2 projects into Liferay 7.0 projects.\n" +
                "The key functions are described below:\n" +
                "       1. Convert Liferay Plugins SDK 6.2 to Liferay Plugins SDK 7.0 or to Liferay Workspace\n" +
                "       2. Find Breaking Changes in all projects\n" +
                "       3. Update Descriptor files from 6.2 to 7.0\n" +
                "       4. Update Layout Template files from 6.2 to 7.0\n" +
                "       5. Convert Custom JSP Hooks to OSGi modules\n" +
                "Note:\n" +
                "       It is highly recommended that you make back-up copies of your important files.\n" +
                "       Theme and Ext projects are not supported to upgrade in this tool currently.\n" +
                "       For more details, please see <a>From Liferay 6 to Liferay 7</a>.\n" +
                "Instructions:\n" +
                "       In order to move through various upgrade steps,\n" +
                "       use left, right, ✓, X and clicking on each gear to move between the upgrade steps.\n" +
                "       What's more, you can mark with ✓ when one step is well done and mark with X when it is not completed or failed.";

        String url = "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/from-liferay-6-to-liferay-7";

        Link link = SWTUtil.createHyperLink( this, style, desriptor, 1, url );

        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 1, 1 ) );
    }
}