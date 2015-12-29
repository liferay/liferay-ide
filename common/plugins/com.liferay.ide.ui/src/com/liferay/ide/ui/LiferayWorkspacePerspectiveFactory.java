package com.liferay.ide.ui;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Lovett Li
 */
public class LiferayWorkspacePerspectiveFactory extends AbstractPerspectiveFactory
{

    @Override
    public void createInitialLayout( IPageLayout layout )
    {
        createLayout(layout);
        addShortcuts(layout);
        setupActions(layout);
    }

    protected void createLayout( IPageLayout layout )
    {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Top left.
        IFolderLayout topLeft = layout.createFolder( "topLeft", IPageLayout.LEFT, 0.20f, editorArea );//$NON-NLS-1$
        topLeft.addView( ID_PROJECT_EXPLORER_VIEW );
        topLeft.addPlaceholder( ID_PACKAGE_EXPLORER_VIEW );
        topLeft.addPlaceholder( ID_J2EE_HIERARCHY_VIEW );
        topLeft.addPlaceholder( JavaUI.ID_TYPE_HIERARCHY );
        topLeft.addPlaceholder( JavaUI.ID_PACKAGES_VIEW );

        // Top right.
        IFolderLayout topRight = layout.createFolder( "topRight", IPageLayout.RIGHT, 0.68f, editorArea );//$NON-NLS-1$

        addViewIfExist(layout, topRight, ID_GRADLE_TASK_VIEW);

        topRight.addPlaceholder( IPageLayout.ID_BOOKMARKS );

        IFolderLayout topRightBottom = layout.createFolder( "topRightBottom", IPageLayout.BOTTOM, 0.7f, "topRight" ); //$NON-NLS-1$ //$NON-NLS-2$
        addViewIfExist(layout, topRightBottom, ID_GRADLE_EXECUTIONS_VIEW);

        IFolderLayout bottomTopLeft = layout.createFolder( "bottomTopLeft", IPageLayout.BOTTOM, 0.7f, "topLeft" ); //$NON-NLS-1$ //$NON-NLS-2$

        bottomTopLeft.addView( ID_SERVERS_VIEW );

        // Bottom
        IFolderLayout bottom = layout.createFolder( "bottom", IPageLayout.BOTTOM, 0.7f, editorArea );//$NON-NLS-1$
        bottom.addView( ID_MARKERS_VIEW );
        bottom.addView( ID_CONSOLE_VIEW );

        bottom.addPlaceholder( IPageLayout.ID_PROBLEM_VIEW );
        bottom.addPlaceholder( IProgressConstants.PROGRESS_VIEW_ID );
        bottom.addPlaceholder( ID_SEARCH_VIEW );

    }
}
