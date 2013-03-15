/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Gregory Amerson
 */
public class LayoutTplMultiOutlinePage implements IContentOutlinePage, IPageBookViewPage, ISelectionChangedListener
{

    private IPartListener2 partListener = new IPartListener2()
    {

        public void partActivated( IWorkbenchPartReference partRef )
        {
            showPage( partRef );

        }

        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
            showPage( partRef );
        }

        public void partClosed( IWorkbenchPartReference partRef )
        {
        }

        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }

        public void partHidden( IWorkbenchPartReference partRef )
        {
        }

        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }

        public void partOpened( IWorkbenchPartReference partRef )
        {
        }

        public void partVisible( IWorkbenchPartReference partRef )
        {
        }
    };

    protected LayoutTplMultiPageEditor multiEditor;

    protected IContentOutlinePage sourceOutlinePage;

    protected IContentOutlinePage visualOutlinePage;

    protected IContentOutlinePage activeOutlinePage;

    private IPageSite pageSite;

    private PageBook pageBook;

    public LayoutTplMultiOutlinePage(
        LayoutTplMultiPageEditor multiEditor, IContentOutlinePage sourceOutlinePage,
        IContentOutlinePage visualOutlinePage )
    {

        this.multiEditor = multiEditor;
        this.sourceOutlinePage = sourceOutlinePage;
        this.visualOutlinePage = visualOutlinePage;
        this.activeOutlinePage = getActiveOutlinePage();
    }

    protected void showPage( IWorkbenchPartReference partRef )
    {
        IWorkbenchPart part = partRef.getPart( false );
        if( part instanceof LayoutTplMultiPageEditor )
        {
            refreshOutline();
        }
    }

    private IContentOutlinePage getActiveOutlinePage()
    {
        return multiEditor.getActivePage() == LayoutTplMultiPageEditor.SOURCE_PAGE_INDEX
            ? sourceOutlinePage : visualOutlinePage;
    }

    public void createControl( Composite parent )
    {
        pageBook = new PageBook( parent, SWT.NONE );
        sourceOutlinePage.createControl( pageBook );
        visualOutlinePage.createControl( pageBook );

        getSite().getPage().addPartListener( partListener );
    }

    public void dispose()
    {
        getSite().getPage().removePartListener( partListener );
        sourceOutlinePage.dispose();
        visualOutlinePage.dispose();
        pageBook.dispose();
    }

    public Control getControl()
    {
        return pageBook;
    }

    public void setActionBars( IActionBars actionBars )
    {
        getActiveOutlinePage().setActionBars( actionBars );
    }

    public void setFocus()
    {
        getActiveOutlinePage().setFocus();
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener )
    {
        getActiveOutlinePage().addSelectionChangedListener( listener );
    }

    public ISelection getSelection()
    {
        return getActiveOutlinePage().getSelection();
    }

    public void removeSelectionChangedListener( ISelectionChangedListener listener )
    {
        getActiveOutlinePage().removeSelectionChangedListener( listener );
    }

    public void setSelection( ISelection selection )
    {
        getActiveOutlinePage().setSelection( selection );
    }

    public void selectionChanged( SelectionChangedEvent event )
    {
        ( (ISelectionChangedListener) getActiveOutlinePage() ).selectionChanged( event );
    }

    public IPageSite getSite()
    {
        return pageSite;
    }

    public void init( IPageSite site ) throws PartInitException
    {
        this.pageSite = site;
        ( (IPageBookViewPage) sourceOutlinePage ).init( site );
        ( (IPageBookViewPage) visualOutlinePage ).init( site );
    }

    public void refreshOutline()
    {
        if( pageBook != null )
        {
            pageBook.showPage( getActiveOutlinePage().getControl() );
            pageBook.redraw();
        }
    }

}
