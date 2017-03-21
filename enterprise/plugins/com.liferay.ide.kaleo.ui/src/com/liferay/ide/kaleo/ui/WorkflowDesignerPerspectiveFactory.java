/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.ui.LiferayPerspectiveFactory;

import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Gregory Amerson
 */
public class WorkflowDesignerPerspectiveFactory extends LiferayPerspectiveFactory
{

    public static final String ID = "com.liferay.ide.eclipse.kaleo.ui.perspective.designer";
    public static final String ID_NEW_WORKFLOW_DEFINITION_WIZARD = "com.liferay.ide.kaleo.ui.new.definition";

    protected void addShortcuts( IPageLayout layout )
    {
        layout.addNewWizardShortcut( ID_NEW_PLUGIN_PROJECT_WIZARD );
        layout.addNewWizardShortcut( ID_NEW_PLUGIN_PROJECT_WIZARD_EXISTING_SOURCE );
        layout.addNewWizardShortcut( ID_NEW_WORKFLOW_DEFINITION_WIZARD );
        layout.addNewWizardShortcut( "org.eclipse.ui.wizards.new.folder" );//$NON-NLS-1$
        layout.addNewWizardShortcut( "org.eclipse.ui.wizards.new.file" );//$NON-NLS-1$
        layout.addNewWizardShortcut( "org.eclipse.ui.editors.wizards.UntitledTextFileWizard" );//$NON-NLS-1$
        layout.addPerspectiveShortcut( "com.liferay.ide.eclipse.ui.perspective.liferay" );
        layout.addPerspectiveShortcut( "org.eclipse.jst.j2ee.J2EEPerspective" );
        layout.addPerspectiveShortcut( "org.eclipse.jdt.ui.JavaPerspective" );
        layout.addPerspectiveShortcut( "org.eclipse.debug.ui.DebugPerspective" );
        layout.addShowViewShortcut( ANT_VIEW_ID );

        IPerspectiveDescriptor desc =
            PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(
                "org.eclipse.team.cvs.ui.cvsPerspective" );

        if( desc != null )
        {
            layout.addPerspectiveShortcut( "org.eclipse.team.cvs.ui.cvsPerspective" );
        }

        desc =
            PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(
                "org.tigris.subversion.subclipse.ui.svnPerspective" );

        if( desc != null )
        {
            layout.addPerspectiveShortcut( "org.tigris.subversion.subclipse.ui.svnPerspective" );
        }

        desc =
            PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(
                "org.eclipse.team.svn.ui.repository.RepositoryPerspective" );

        if( desc != null )
        {
            layout.addPerspectiveShortcut( "org.eclipse.team.svn.ui.repository.RepositoryPerspective" );
        }
    }

    @SuppressWarnings( "deprecation" )
    protected void createLayout( IPageLayout layout )
    {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Top left.
        IFolderLayout topLeft = layout.createFolder( "topLeft", IPageLayout.LEFT, 0.20f, editorArea );//$NON-NLS-1$
        topLeft.addView( ID_PACKAGE_EXPLORER_VIEW );
        // topLeft.addView(ID_J2EE_HIERARCHY_VIEW);
        topLeft.addPlaceholder( ID_J2EE_HIERARCHY_VIEW );
        topLeft.addPlaceholder( IPageLayout.ID_RES_NAV );
        topLeft.addPlaceholder( JavaUI.ID_TYPE_HIERARCHY );
        topLeft.addPlaceholder( JavaUI.ID_PACKAGES_VIEW );

        // Top right.
        IFolderLayout topRight = layout.createFolder( "topRight", IPageLayout.RIGHT, 0.68f, editorArea );//$NON-NLS-1$
        topRight.addView( PaletteView.ID );
        topRight.addPlaceholder( IPageLayout.ID_BOOKMARKS );

        IFolderLayout topRightBottom = layout.createFolder( "topRightBottom", IPageLayout.BOTTOM, 0.55f, "topRight" );
        topRightBottom.addView( IPageLayout.ID_OUTLINE );

        IFolderLayout bottomTopLeft = layout.createFolder( "bottomTopLeft", IPageLayout.BOTTOM, 0.7f, "topLeft" );
        bottomTopLeft.addView( ID_SERVERS_VIEW );

        // Bottom
        IFolderLayout bottom = layout.createFolder( "bottom", IPageLayout.BOTTOM, 0.55f, editorArea );//$NON-NLS-1$
        bottom.addView( IPageLayout.ID_PROP_SHEET );
        bottom.addView( ID_MARKERS_VIEW );
        bottom.addPlaceholder( IPageLayout.ID_PROBLEM_VIEW );
        bottom.addPlaceholder( IProgressConstants.PROGRESS_VIEW_ID );
        bottom.addPlaceholder( ID_SEARCH_VIEW );
    }

}
