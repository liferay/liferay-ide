package com.liferay.ide.velocity.vaulttec.ui.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Preferences;

import com.liferay.ide.velocity.editor.VelocityEditor;
import com.liferay.ide.velocity.vaulttec.ui.IPreferencesConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityTextGuesser;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class ModelTools
{

    private VelocityEditor fEditor;

    public ModelTools(VelocityEditor anEditor)
    {
        fEditor = anEditor;
    }

    /**
     * Uses visitor design pattern to find tree node which contains given line.
     * 
     * @param aLine
     *            line to find according tree node for
     * @return tree node containing given line or null if no tree node found
     */
    public ITreeNode getNodeByLine(int aLine)
    {
        ITreeNode node = fEditor.getRootNode();
        if (node != null)
        {
            TreeNodeLineVisitor visitor = new TreeNodeLineVisitor(aLine);
            node.accept(visitor);
            return visitor.getNode();
        }
        return null;
    }

    /**
     * Uses visitor design pattern to find tree node which contains given
     * guessed text.
     * 
     * @param aGuess
     *            document region marking a reference to find according tree
     *            node with referenced id for
     * @return tree node containing referenced id or null if no tree node found
     */
    public ITreeNode getNodeByGuess(VelocityTextGuesser aGuess)
    {
        ITreeNode node = fEditor.getRootNode();
        if (node != null)
        {
            String id;
            if (aGuess.getType() == VelocityTextGuesser.TYPE_VARIABLE)
            {
                id = "$" + aGuess.getText();
            } else if (aGuess.getType() == VelocityTextGuesser.TYPE_DIRECTIVE)
            {
                id = aGuess.getText();
            } else
            {
                return null;
            }
            // Search the model tree for a node with given ID
            TreeNodeIdVisitor visitor = new TreeNodeIdVisitor(id, aGuess.getLine());
            node.accept(visitor);
            return visitor.getNode();
        }
        return null;
    }

    /**
     * Returns true if specified line belongs to a <code>#foreach</code>
     * block.
     */
    public boolean isLineWithinLoop(int aLine)
    {
        ITreeNode node = fEditor.getRootNode();
        if (node != null)
        {
            if (aLine > 0)
            {
                // Use visitor pattern to find node which contains given line
                TreeNodeLineVisitor visitor = new TreeNodeLineVisitor(aLine);
                node.accept(visitor);
                node = visitor.getNode();
                while (node != null)
                {
                    if (node instanceof Directive)
                    {
                        Directive directive = (Directive) node;
                        if (directive.getType() == Directive.TYPE_FOREACH) { return true; }
                    }
                    node = (ITreeNode) node.getParent();
                }
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aLine
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public List getVariables(int aLine)
    {
        ITreeNode node = fEditor.getLastRootNode();
        if (node != null)
        {
            TreeNodeVariableVisitor visitor = new TreeNodeVariableVisitor(aLine);
            node.accept(visitor);
            List variables = visitor.getVariables();
            if (isLineWithinLoop(fEditor.getCursorLine()))
            {
                Preferences prefs = VelocityPlugin.getDefault().getPluginPreferences();
                String countName = "$" + prefs.getString(IPreferencesConstants.VELOCITY_COUNTER_NAME);
                variables.add(countName);
            }
            return variables;
        }
        return new ArrayList();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public List getMacros()
    {
        ITreeNode node = fEditor.getLastRootNode();
        if (node != null)
        {
            TreeNodeMacroVisitor visitor = new TreeNodeMacroVisitor();
            node.accept(visitor);
            return visitor.getMacros();
        }
        return new ArrayList();
    }

    private class TreeNodeLineVisitor implements ITreeVisitor
    {

        private int       fLine;
        private ITreeNode fNode;

        public TreeNodeLineVisitor(int aLine)
        {
            fLine = aLine;
            fNode = null;
        }

        public boolean visit(ITreeNode aNode)
        {
            boolean more;
            if ((fLine >= aNode.getStartLine()) && (fLine <= aNode.getEndLine()))
            {
                fNode = aNode;
                more = false;
            } else
            {
                more = true;
            }
            return more;
        }

        public ITreeNode getNode()
        {
            return fNode;
        }
    }

    private class TreeNodeIdVisitor implements ITreeVisitor
    {

        private String    fId;
        private int       fLine;
        private ITreeNode fNode;

        public TreeNodeIdVisitor(String anId, int aLine)
        {
            fId = anId;
            fLine = aLine;
            fNode = null;
        }

        public boolean visit(ITreeNode aNode)
        {
            if ((aNode.getStartLine() < fLine) && aNode instanceof Directive)
            {
                Directive directive = ((Directive) aNode);
                // If within macro block then check macro parameters instead
                // of ID
                if (directive.getType() == Directive.TYPE_MACRO)
                {
                    if (directive.getId().equals(fId))
                    {
                        fNode = aNode;
                    } else if ((fLine >= aNode.getStartLine()) && (fLine <= aNode.getEndLine()))
                    {
                        List parameters = ((Directive) aNode).getParameters();
                        if ((parameters != null) && parameters.contains(fId))
                        {
                            fNode = aNode;
                        }
                    }
                } else
                {
                    String id = directive.getId();
                    if ((id != null) && id.equals(fId))
                    {
                        fNode = aNode;
                    }
                }
            }
            return true;
        }

        public ITreeNode getNode()
        {
            return fNode;
        }
    }

    private class TreeNodeVariableVisitor implements ITreeVisitor
    {

        private int  fLine;
        private List fVariables;

        public TreeNodeVariableVisitor(int aLine)
        {
            fLine = aLine;
            fVariables = new ArrayList();
        }

        public boolean visit(ITreeNode aNode)
        {
            if (aNode instanceof Directive)
            {
                int type = ((Directive) aNode).getType();
                if ((type == Directive.TYPE_FOREACH) || (type == Directive.TYPE_SET))
                {
                    String variable = ((Directive) aNode).getId();
                    if (!fVariables.contains(variable))
                    {
                        fVariables.add(variable);
                    }
                } else if (type == Directive.TYPE_MACRO)
                {
                    if ((fLine >= aNode.getStartLine()) && (fLine <= aNode.getEndLine()))
                    {
                        List parameters = ((Directive) aNode).getParameters();
                        if (parameters != null)
                        {
                            fVariables.addAll(parameters);
                        }
                    }
                }
            }
            return true;
        }

        public List getVariables()
        {
            return fVariables;
        }
    }

    private class TreeNodeMacroVisitor implements ITreeVisitor
    {

        private List fMacros;

        public TreeNodeMacroVisitor()
        {
            fMacros = new ArrayList();
        }

        public boolean visit(ITreeNode aNode)
        {
            if (aNode instanceof Directive)
            {
                int type = ((Directive) aNode).getType();
                if (type == Directive.TYPE_MACRO)
                {
                    fMacros.add(((Directive) aNode).getId());
                }
            }
            return true;
        }

        public List getMacros()
        {
            return fMacros;
        }
    }
}
