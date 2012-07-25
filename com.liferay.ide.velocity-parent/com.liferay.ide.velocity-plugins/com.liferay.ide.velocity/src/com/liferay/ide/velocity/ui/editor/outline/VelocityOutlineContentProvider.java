package com.liferay.ide.velocity.ui.editor.outline;

import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.liferay.ide.velocity.editor.VelocityEditor;
import com.liferay.ide.velocity.vaulttec.ui.model.ITreeNode;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class VelocityOutlineContentProvider implements ITreeContentProvider
{

    public static final String VELOCITY_TEMPLATE = "__velocity_template";
    private VelocityEditor     fEditor;
    private IPositionUpdater   fPositionUpdater;

    public VelocityOutlineContentProvider(VelocityEditor anEditor)
    {
        fEditor = anEditor;
        fPositionUpdater = new DefaultPositionUpdater(VELOCITY_TEMPLATE);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer aViewer, Object anOldInput, Object aNewInput)
    {
        if (anOldInput != aNewInput)
        {
            if (anOldInput != null)
            {
                IDocument document = fEditor.getDocumentProvider().getDocument(anOldInput);
                if (document != null)
                {
                    try
                    {
                        document.removePositionCategory(VELOCITY_TEMPLATE);
                    }
                    catch (BadPositionCategoryException e)
                    {
                    }
                    document.removePositionUpdater(fPositionUpdater);
                }
            }
            if (aNewInput != null)
            {
                IDocument document = fEditor.getDocumentProvider().getDocument(aNewInput);
                if (document != null)
                {
                    document.addPositionCategory(VELOCITY_TEMPLATE);
                    document.addPositionUpdater(fPositionUpdater);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {
        return fEditor.getRootElements();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object anElement)
    {
        return (anElement instanceof ITreeNode) ? ((ITreeNode) anElement).getChildren() : ITreeNode.NO_CHILDREN;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object anElement)
    {
        return (anElement instanceof ITreeNode) ? ((ITreeNode) anElement).getParent() : null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object anElement)
    {
        return (anElement instanceof ITreeNode) ? ((ITreeNode) anElement).hasChildren() : false;
    }
}
