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
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.wst.html.ui.internal.edit.ui.ActionContributorHTML;
import org.eclipse.wst.sse.ui.internal.ISourceViewerActionBarContributor;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LayoutTplMultiPageEditorActionBarContributor extends MultiPageEditorActionBarContributor
{
    protected IEditorActionBarContributor sourceEditorContributor;
    protected IEditorActionBarContributor visualEditorContributor;
    protected LayoutTplMultiPageEditor layoutTplMultiPageEditor;
    protected boolean needsMultiInit;

    public LayoutTplMultiPageEditorActionBarContributor()
    {
        super();

        this.sourceEditorContributor = new ActionContributorHTML();
    }

    public void init( IActionBars actionBars )
    {
        super.init( actionBars );

        if( actionBars != null )
        {
            initDesignViewerActionBarContributor( actionBars );
            initSourceViewerActionContributor( actionBars );
        }

        needsMultiInit = true;
    }

    protected void initDesignViewerActionBarContributor( IActionBars actionBars )
    {
        if( visualEditorContributor != null )
        {
            visualEditorContributor.init( actionBars, getPage() );
        }
    }

    protected void initSourceViewerActionContributor( IActionBars actionBars )
    {
        if( sourceEditorContributor != null )
        {
            sourceEditorContributor.init( actionBars, getPage() );
        }
    }

    public void dispose()
    {
        super.dispose();

        if( visualEditorContributor != null )
        {
            visualEditorContributor.dispose();
        }

        if( sourceEditorContributor != null )
        {
            sourceEditorContributor.dispose();
        }

        layoutTplMultiPageEditor = null;
    }

    @Override
    public void setActivePage( IEditorPart activeEditor )
    {
        if( layoutTplMultiPageEditor != null )
        {
            if( ( activeEditor != null ) && ( activeEditor instanceof ITextEditor ) )
            {
                activateSourcePage( activeEditor );
            }
            else
            {
                activateVisualPage( activeEditor );
            }
        }

        IActionBars actionBars = getActionBars();
        if( actionBars != null )
        {
            actionBars.clearGlobalActionHandlers();

            if( layoutTplMultiPageEditor.getSelectedPage() instanceof LayoutTplEditor )
            {
                actionBars.setGlobalActionHandler(
                    ActionFactory.UNDO.getId(), getLayoutEditorAction( ActionFactory.UNDO.getId() ) );
                actionBars.setGlobalActionHandler(
                    ActionFactory.REDO.getId(), getLayoutEditorAction( ActionFactory.REDO.getId() ) );
                actionBars.setGlobalActionHandler(
                    ActionFactory.DELETE.getId(), getLayoutEditorAction( ActionFactory.DELETE.getId() ) );
                actionBars.setGlobalActionHandler(
                    ActionFactory.SELECT_ALL.getId(), getLayoutEditorAction( ActionFactory.SELECT_ALL.getId() ) );
            }

            if( layoutTplMultiPageEditor.getSelectedPage() instanceof ITextEditor )
            {
                actionBars.setGlobalActionHandler(
                    ActionFactory.UNDO.getId(), getTextEditorAction( ITextEditorActionConstants.UNDO ) );
                actionBars.setGlobalActionHandler(
                    ActionFactory.REDO.getId(), getTextEditorAction( ITextEditorActionConstants.REDO ) );
                actionBars.setGlobalActionHandler(
                    ActionFactory.DELETE.getId(), getTextEditorAction( ITextEditorActionConstants.DELETE ) );
                actionBars.setGlobalActionHandler(
                    ActionFactory.SELECT_ALL.getId(), getTextEditorAction( ITextEditorActionConstants.SELECT_ALL ) );
            }

            // update menu bar and tool bar
            actionBars.updateActionBars();
        }
    }

    protected IAction getTextEditorAction( String actionId )
    {
        if( layoutTplMultiPageEditor != null )
        {
            try
            {
                return layoutTplMultiPageEditor.getSourceEditor().getAction( actionId );
            }
            catch( NullPointerException e )
            {
                //editor has been disposed, ignore
            }
        }

        return null;
    }

    protected IAction getLayoutEditorAction( String actionId )
    {
        if( layoutTplMultiPageEditor != null )
        {
            return layoutTplMultiPageEditor.getVisualEditor().getActionRegistry().getAction( actionId );
        }

        return null;
    }

    @Override
    public void setActiveEditor( IEditorPart part )
    {
        if( part instanceof MultiPageEditorPart )
        {
            this.layoutTplMultiPageEditor = (LayoutTplMultiPageEditor) part;
        }

        if( needsMultiInit )
        {
            visualEditorContributor = new LayoutTplEditorActionBarContributor();
            initDesignViewerActionBarContributor( getActionBars() );
            needsMultiInit = false;
        }

        super.setActiveEditor( part );
    }

    protected void activateVisualPage( IEditorPart activeEditor )
    {
        if( ( sourceEditorContributor != null ) &&
            ( sourceEditorContributor instanceof ISourceViewerActionBarContributor ) )
        {
            // if design page is not really an IEditorPart, activeEditor ==
            // null, so pass in multiPageEditor instead (d282414)
            if( activeEditor == null )
            {
                sourceEditorContributor.setActiveEditor( layoutTplMultiPageEditor );
            }
            else
            {
                sourceEditorContributor.setActiveEditor( activeEditor );
            }
            ( (ISourceViewerActionBarContributor) sourceEditorContributor ).setViewerSpecificContributionsEnabled( false );
        }
    }

    protected void activateSourcePage( IEditorPart activeEditor )
    {
        if( visualEditorContributor != null )
        {
            visualEditorContributor.setActiveEditor( layoutTplMultiPageEditor );
        }

        if( ( sourceEditorContributor != null ) &&
            ( sourceEditorContributor instanceof ISourceViewerActionBarContributor ) )
        {
            sourceEditorContributor.setActiveEditor( activeEditor );
            ( (ISourceViewerActionBarContributor) sourceEditorContributor ).setViewerSpecificContributionsEnabled( true );
        }
    }

}
