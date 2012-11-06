package com.liferay.ide.velocity.ui.editor.outline;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.liferay.ide.velocity.editor.VelocityEditor;
import com.liferay.ide.velocity.vaulttec.ui.editor.actions.CollapseAllAction;
import com.liferay.ide.velocity.vaulttec.ui.model.ITreeNode;

/**
 * A content outline page which represents the content of an Velocity template
 * file.
 */
public class VelocityOutlinePage extends ContentOutlinePage
{

    private VelocityEditor               fEditor;
    private Object                       fInput;
    private String                       fSelectedNodeID;
    private VelocityOutlineLabelProvider fLabelProvider;
    private boolean                      fIsDisposed;

    /**
     * Creates a content outline page using the given editor.
     */
    public VelocityOutlinePage(VelocityEditor anEditor)
    {
        fEditor = anEditor;
        fIsDisposed = true;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite aParent)
    {
        super.createControl(aParent);
        fLabelProvider = new VelocityOutlineLabelProvider();
        // Init tree viewer
        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new VelocityOutlineContentProvider(fEditor));
        viewer.setLabelProvider(fLabelProvider);
        viewer.addSelectionChangedListener(this);
        if (fInput != null)
        {
            viewer.setInput(fInput);
        }
        fIsDisposed = false;
        // Add collapse all button to viewer's toolbar
        IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
        mgr.add(new CollapseAllAction(viewer));
        // Refresh outline according to initial cursor position
        update();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent anEvent)
    {
        super.selectionChanged(anEvent);
        ISelection selection = anEvent.getSelection();
        if (!selection.isEmpty())
        {
            ITreeNode node = (ITreeNode) ((IStructuredSelection) selection).getFirstElement();
            if ((fSelectedNodeID == null) || isDifferentBlock(node))
            {
                fEditor.highlightNode(node, true);
                fSelectedNodeID = node.getUniqueID();
            } else
            {
                fEditor.revealNode(node);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aLine
     *            DOCUMENT ME!
     * @param aForceSelect
     *            DOCUMENT ME!
     */
    public void selectNode(int aLine, boolean aForceSelect)
    {
        if (aLine > 0)
        {
            TreeViewer viewer = getTreeViewer();
            ITreeNode node = fEditor.getNodeByLine(aLine);
            viewer.removeSelectionChangedListener(this);
            if (node == null)
            {
                if (fSelectedNodeID != null)
                {
                    viewer.setSelection(new StructuredSelection());
                    fEditor.resetHighlightRange();
                    fSelectedNodeID = null;
                }
            } else
            {
                if (aForceSelect || isDifferentBlock(node))
                {
                    viewer.setSelection(new StructuredSelection(node));
                    // here we should fold
                    fEditor.fold(node.getStartLine(), node.getEndLine());
                    fEditor.highlightNode(node, false);
                    fSelectedNodeID = node.getUniqueID();
                }
                viewer.reveal(node);
            }
            viewer.addSelectionChangedListener(this);
        }
    }

    private boolean isDifferentBlock(ITreeNode aNode)
    {
        return ((fSelectedNodeID == null) || !fSelectedNodeID.equals(aNode.getUniqueID()));
    }

    /**
     * Sets the input of the outline page.
     */
    public void setInput(Object aInput)
    {
        fInput = aInput;
        update();
    }

    /**
     * Updates the outline page.
     */
    public void update()
    {
        TreeViewer viewer = getTreeViewer();
        if (viewer != null)
        {
            Control control = viewer.getControl();
            if ((control != null) && !control.isDisposed())
            {
                viewer.removeSelectionChangedListener(this);
                control.setRedraw(false);
                viewer.setInput(fInput);
                // viewer.expandAll();
                control.setRedraw(true);
                selectNode(fEditor.getCursorLine(), true);
                viewer.addSelectionChangedListener(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.IPage#dispose()
     */
    public void dispose()
    {
        setInput(null);
        if (fLabelProvider != null)
        {
            fLabelProvider.dispose();
            fLabelProvider = null;
        }
        fIsDisposed = true;
        super.dispose();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isDisposed()
    {
        return fIsDisposed;
    }
}
