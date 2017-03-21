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

package com.liferay.ide.kaleo.ui.helpers;

import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.editor.KaleoEditorPaletteFactory;
import com.liferay.ide.kaleo.ui.editor.KaleoPaletteViewerPage;
import com.liferay.ide.kaleo.ui.editor.ScriptCreationFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class KaleoPaletteHelper
{

    protected class TransferDragSourceListenerImpl implements TransferDragSourceListener
    {
        private Transfer fTransfer;

        public TransferDragSourceListenerImpl( Transfer xfer )
        {
            fTransfer = xfer;
        }

        public void dragFinished( DragSourceEvent event )
        {
            try
            {
                IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

                String editorId = activeEditor.getSite().getId();

                IKaleoEditorHelper helper = KaleoUI.getKaleoEditorHelperByEditorId( editorId );

                helper.handleDropFromPalette( activeEditor );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void dragSetData( DragSourceEvent event )
        {
            if( getSelectedEntry() == null )
            {
                return;
            }

            if( getSelectedEntry() instanceof CombinedTemplateCreationEntry )
            {
                CombinedTemplateCreationEntry tool = getSelectedEntry();
                Object tempalte = tool.getTemplate();
                ScriptCreationFactory scriptFactory = (ScriptCreationFactory) tempalte;
                event.data = scriptFactory.getNewObject().toString();
            }
            else
            {
                event.data = "";
            }
        }

        public void dragStart( DragSourceEvent event )
        {
        }

        /*
         * (non-Javadoc)
         * @see org.eclipse.jface.util.TransferDragSourceListener#getTransfer()
         */
        public Transfer getTransfer()
        {
            return fTransfer;
        }
    }
    private DefaultEditDomain editDomain;
    private IEditorPart editorPart;
    private KaleoPaletteViewerPage palettePage;
    private PaletteRoot paletteRoot;
    private PaletteViewerProvider provider;
    private CombinedTemplateCreationEntry selectedEntry;
    private ISelectionChangedListener selectionChangedListener;

    private List<TransferDragSourceListener> transferDragSourceListeners;

    public KaleoPaletteHelper( IEditorPart editorPart, AbstractUIPlugin uiBundle, String folderName, ImageDescriptor entryImage )
    {
        this.editorPart = editorPart;
        this.paletteRoot = KaleoEditorPaletteFactory.createPalette( uiBundle, folderName, entryImage);
        this.editDomain = new DefaultEditDomain( this.editorPart );
        this.editDomain.setPaletteRoot( this.paletteRoot );
    }

    protected void addTextTransferListener()
    {
        TransferDragSourceListener listener = new TransferDragSourceListenerImpl( TextTransfer.getInstance() );
        getPaletteViewer().addDragSourceListener( listener );
        getTransferDragSourceListeners().add( listener );
    }

    public KaleoPaletteViewerPage createPalettePage()
    {
        this.palettePage = new KaleoPaletteViewerPage( getPaletteViewerProvider() );
        return this.palettePage;
    }

    protected PaletteViewerProvider createPaletteViewerProvider()
    {
        return new PaletteViewerProvider( getEditDomain() )
        {

            @Override
            protected void configurePaletteViewer( PaletteViewer viewer )
            {
                super.configurePaletteViewer( viewer );
                viewer.addDragSourceListener( new TemplateTransferDragSourceListener( viewer ) );
                viewer.addSelectionChangedListener( getViewerSelectionChangedListener() );
            }
        };
    }

    public DefaultEditDomain getEditDomain()
    {
        return editDomain;
    }

    protected CombinedTemplateCreationEntry getEntryFromSelection( ISelection selection )
    {
        if( !selection.isEmpty() )
        {
            if( selection instanceof IStructuredSelection )
            {
                Object obj = ( (IStructuredSelection) selection ).getFirstElement();

                if( obj instanceof EditPart )
                {
                    if( ( (EditPart) obj ).getModel() instanceof CombinedTemplateCreationEntry )
                        return (CombinedTemplateCreationEntry) ( (EditPart) obj ).getModel();
                }
                else if( obj instanceof CombinedTemplateCreationEntry )
                {
                    return (CombinedTemplateCreationEntry) obj;
                }
            }
        }

        return null;
    }

    private PaletteViewer getPaletteViewer()
    {
        if( this.palettePage != null )
        {
            return this.palettePage.getViewer();
        }

        return null;
    }

    protected final PaletteViewerProvider getPaletteViewerProvider()
    {
        if( this.provider == null )
        {
            this.provider = createPaletteViewerProvider();
        }

        return this.provider;
    }

    public CombinedTemplateCreationEntry getSelectedEntry()
    {
        return this.selectedEntry;
    }

    public List<TransferDragSourceListener> getTransferDragSourceListeners()
    {
        if( this.transferDragSourceListeners == null )
        {
            this.transferDragSourceListeners = new ArrayList<TransferDragSourceListener>();
        }

        return this.transferDragSourceListeners;
    }

    private ISelectionChangedListener getViewerSelectionChangedListener()
    {
        if( this.selectionChangedListener == null )
        {
            this.selectionChangedListener = new ISelectionChangedListener()
            {

                public void selectionChanged( SelectionChangedEvent event )
                {
                    setSelectedEntry( getEntryFromSelection( event.getSelection() ) );
                }
            };
        }
        return this.selectionChangedListener;
    }

    public void setSelectedEntry( CombinedTemplateCreationEntry entry )
    {
        if( this.selectedEntry == entry )
        {
            return;
        }

        this.selectedEntry = entry;
        updateDragSource();
    }

    protected void updateDragSource()
    {
        Transfer[] supportedTypes = new Transfer[] { TextTransfer.getInstance() };

        /*
         * TRH suggested use of the event's doit field by the fListeners, but there's no other way to guarantee that
         * TextTransfer is considered last
         */
        Iterator<TransferDragSourceListener> iterator = getTransferDragSourceListeners().iterator();
        ArrayList<TransferDragSourceListener> oldListeners = new ArrayList<TransferDragSourceListener>();

        while( iterator.hasNext() )
        {
            TransferDragSourceListener listener = iterator.next();
            oldListeners.add( listener );
            iterator.remove();
        }

        boolean addTextTransfer = false;
        for( int i = 0; i < supportedTypes.length; i++ )
        {
            if( TextTransfer.class.equals( supportedTypes[i].getClass() ) )
            {
                addTextTransfer = true;
            }
            else
            {
                TransferDragSourceListener listener = new TransferDragSourceListenerImpl( supportedTypes[i] );
                getPaletteViewer().addDragSourceListener( listener );
                getTransferDragSourceListeners().add( listener );
            }
        }

        iterator = oldListeners.iterator();

        while( iterator.hasNext() )
        {
            TransferDragSourceListener listener = iterator.next();
            getPaletteViewer().removeDragSourceListener( listener );
            iterator.remove();
        }

        if( addTextTransfer )
        {
            addTextTransferListener();
        }

    }

}
