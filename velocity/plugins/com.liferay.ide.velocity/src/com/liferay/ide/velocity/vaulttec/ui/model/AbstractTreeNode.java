package com.liferay.ide.velocity.vaulttec.ui.model;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public abstract class AbstractTreeNode implements ITreeNode
{

    private ITreeNode fParent;
    private int       fStartLine;
    private int       fEndLine;

    protected AbstractTreeNode(ITreeNode aParent, int aStartLine, int anEndLine)
    {
        fParent = aParent;
        fStartLine = aStartLine;
        fEndLine = anEndLine;
    }

    /**
     * @see ITreeNode#getName()
     */
    public abstract String getName();

    /**
     * @see ITreeNode#getParent()
     */
    public Object getParent()
    {
        return fParent;
    }

    /**
     * @see ITreeNodeInfo#getStartLine()
     */
    public int getStartLine()
    {
        return fStartLine;
    }

    /**
     * @see ITreeNodeInfo#getEndLine()
     */
    public int getEndLine()
    {
        return fEndLine;
    }
}
