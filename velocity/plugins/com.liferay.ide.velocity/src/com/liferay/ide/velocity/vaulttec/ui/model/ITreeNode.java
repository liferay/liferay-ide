package com.liferay.ide.velocity.vaulttec.ui.model;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public interface ITreeNode
{

    public static final Object[] NO_CHILDREN = new Object[0];

    String getName();

    Object getParent();

    boolean hasChildren();

    Object[] getChildren();

    String getUniqueID();

    int getStartLine();

    int getEndLine();

    /**
     * Visitor design pattern.
     * @see ITreeVisitor#visit(ITreeNodeInfo)
     */
    boolean accept(ITreeVisitor aVisitor);
}
