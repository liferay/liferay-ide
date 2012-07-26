package com.liferay.ide.velocity.vaulttec.ui.editor.parser;

import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityEditorEnvironment;
import com.liferay.ide.velocity.vaulttec.ui.model.Directive;
import com.liferay.ide.velocity.vaulttec.ui.model.IBlock;
import com.liferay.ide.velocity.vaulttec.ui.model.ITreeNode;
import com.liferay.ide.velocity.vaulttec.ui.model.Template;

import java.util.Stack;

import org.apache.velocity.runtime.parser.node.ASTAddNode;
import org.apache.velocity.runtime.parser.node.ASTAndNode;
import org.apache.velocity.runtime.parser.node.ASTAssignment;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTComment;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTDivNode;
import org.apache.velocity.runtime.parser.node.ASTEQNode;
import org.apache.velocity.runtime.parser.node.ASTElseIfStatement;
import org.apache.velocity.runtime.parser.node.ASTElseStatement;
import org.apache.velocity.runtime.parser.node.ASTEscape;
import org.apache.velocity.runtime.parser.node.ASTEscapedDirective;
import org.apache.velocity.runtime.parser.node.ASTExpression;
import org.apache.velocity.runtime.parser.node.ASTFalse;
import org.apache.velocity.runtime.parser.node.ASTFloatingPointLiteral;
import org.apache.velocity.runtime.parser.node.ASTGENode;
import org.apache.velocity.runtime.parser.node.ASTGTNode;
import org.apache.velocity.runtime.parser.node.ASTIdentifier;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTIntegerLiteral;
import org.apache.velocity.runtime.parser.node.ASTIntegerRange;
import org.apache.velocity.runtime.parser.node.ASTLENode;
import org.apache.velocity.runtime.parser.node.ASTLTNode;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.ASTModNode;
import org.apache.velocity.runtime.parser.node.ASTMulNode;
import org.apache.velocity.runtime.parser.node.ASTNENode;
import org.apache.velocity.runtime.parser.node.ASTNotNode;
import org.apache.velocity.runtime.parser.node.ASTObjectArray;
import org.apache.velocity.runtime.parser.node.ASTOrNode;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.ASTSubtractNode;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTTrue;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;


/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 26 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class NodeVisitor implements ParserVisitor
{

    private String   fName;
    private Template fTemplate;
    private Stack    fBlocks = new Stack();
    private IBlock   fCurrentBlock;

    public NodeVisitor(String aName)
    {
        fName = aName;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Template getTemplate()
    {
        return fTemplate;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.SimpleNode,
     *      java.lang.Object)
     */
    public Object visit(SimpleNode aNode, Object aData)
    {
        return aNode.childrenAccept(this, aData);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTprocess,
     *      java.lang.Object)
     */
    public Object visit(ASTprocess aNode, Object aData)
    {
        fTemplate = new Template(fName);
        fCurrentBlock = fTemplate;
        return aNode.childrenAccept(this, aData);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTComment,
     *      java.lang.Object)
     */
    public Object visit(ASTComment aNode, Object aData)
    {
        return null;
    }

    
    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTStringLiteral,
     *      java.lang.Object)
     */
    public Object visit(ASTStringLiteral aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTIdentifier,
     *      java.lang.Object)
     */
    public Object visit(ASTIdentifier aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTWord,
     *      java.lang.Object)
     */
    public Object visit(ASTWord aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTDirective,
     *      java.lang.Object)
     */
    public Object visit(ASTDirective aNode, Object aData)
    {
        String name = aNode.getDirectiveName();
        int type = Directive.getType('#' + name);
        Directive directive;
        String id = null;
        switch (type)
        {
            case Directive.TYPE_MACRO:
                if ((aNode.jjtGetNumChildren() > 0) && aNode.jjtGetChild(0) instanceof ASTWord)
                {
                    name = aNode.jjtGetChild(0).literal();
                } else
                {
                    name = "";
                }
                id = name;
                break;
            case Directive.TYPE_FOREACH:
                if (aNode.jjtGetNumChildren() > 2)
                {
                    name = aNode.jjtGetChild(2).literal();
                } else
                {
                    name = "";
                }
                if ((aNode.jjtGetNumChildren() > 0) && aNode.jjtGetChild(0) instanceof ASTReference)
                {
                    id = aNode.jjtGetChild(0).literal();
                } else
                {
                    id = "";
                }
                break;
            case Directive.TYPE_INCLUDE:
            case Directive.TYPE_PARSE:
                if (aNode.jjtGetNumChildren() > 0)
                {
                    name = aNode.jjtGetChild(0).literal();
                } else
                {
                    name = "";
                }
                id = "";
                break;
            case Directive.TYPE_MACRO_CALL:
                // Check if an already defined macro is referenced
                if (VelocityEditorEnvironment.getParser().isVelocimacro(name, fTemplate.getName()))
                {
                    id = "";
                } else
                {
                    id = null;
                }
                break;
            case Directive.TYPE_USER_DIRECTIVE:
                id = "";
                break;
        }
        // If valid directive then visit embedded nodes too
        if (id != null)
        {
            directive = new Directive(type, name, id, (ITreeNode) fCurrentBlock, aNode.getFirstToken().beginLine, aNode.getLastToken().endLine);
            aData = visitBlockDirective(aNode, aData, directive, false);
            // Add parameters of macro definition
            if ((type == Directive.TYPE_MACRO) && (aNode.jjtGetNumChildren() > 1))
            {
                for (int i = 1; i < aNode.jjtGetNumChildren(); i++)
                {
                    Node node = aNode.jjtGetChild(i);
                    if (node instanceof ASTReference)
                    {
                        directive.addParameter(node.literal());
                    } else
                    {
                        break;
                    }
                }
            }
        }
        return aData;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTBlock,
     *      java.lang.Object)
     */
    public Object visit(ASTBlock aNode, Object aData)
    {
        return aNode.childrenAccept(this, aData);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTObjectArray,
     *      java.lang.Object)
     */
    public Object visit(ASTObjectArray aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTMethod,
     *      java.lang.Object)
     */
    public Object visit(ASTMethod aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTReference,
     *      java.lang.Object)
     */
    public Object visit(ASTReference aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTTrue,
     *      java.lang.Object)
     */
    public Object visit(ASTTrue aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTFalse,
     *      java.lang.Object)
     */
    public Object visit(ASTFalse aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTText,
     *      java.lang.Object)
     */
    public Object visit(ASTText aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTIfStatement,
     *      java.lang.Object)
     */
    public Object visit(ASTIfStatement aNode, Object aData)
    {
        // Check for first embedded #ELSE or #ELSEIF directive to get end line
        int endLine = aNode.getLastToken().endLine;
        int numChildren = aNode.jjtGetNumChildren();
        for (int i = 1; i < numChildren; i++)
        {
            Node node = aNode.jjtGetChild(i);
            if (node instanceof ASTElseStatement || node instanceof ASTElseIfStatement)
            {
                endLine = node.getFirstToken().beginLine - 1;
                break;
            }
        }
        Directive directive = new Directive(Directive.TYPE_IF, aNode.jjtGetChild(0).literal(), "", (ITreeNode) fCurrentBlock, aNode.getFirstToken().beginLine, endLine);
        return visitBlockDirective(aNode, aData, directive, false);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTElseStatement,
     *      java.lang.Object)
     */
    public Object visit(ASTElseStatement aNode, Object aData)
    {
        Directive directive = new Directive(Directive.TYPE_ELSE, null, "", (ITreeNode) fCurrentBlock, aNode.getFirstToken().beginLine, aNode.getLastToken().next.endLine);
        return visitBlockDirective(aNode, aData, directive, true);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTElseIfStatement,
     *      java.lang.Object)
     */
    public Object visit(ASTElseIfStatement aNode, Object aData)
    {
        Directive directive = new Directive(Directive.TYPE_ELSEIF, aNode.jjtGetChild(0).literal(), "", (ITreeNode) fCurrentBlock, aNode.getFirstToken().beginLine, aNode.getLastToken().endLine);
        return visitBlockDirective(aNode, aData, directive, true);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTSetDirective,
     *      java.lang.Object)
     */
    public Object visit(ASTSetDirective aNode, Object aData)
    {
        String expr = aNode.jjtGetChild(0).literal();
        int pos = expr.indexOf('=');
        if (pos >= 0)
        {
            expr = expr.substring(0, pos).trim();
        }
        Directive directive = new Directive(Directive.TYPE_SET, expr, expr, (ITreeNode) fCurrentBlock, aNode.getFirstToken().beginLine, aNode.getLastToken().endLine);
        fCurrentBlock.addDirective(directive);
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTExpression,
     *      java.lang.Object)
     */
    public Object visit(ASTExpression aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTAssignment,
     *      java.lang.Object)
     */
    public Object visit(ASTAssignment aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTOrNode,
     *      java.lang.Object)
     */
    public Object visit(ASTOrNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTAndNode,
     *      java.lang.Object)
     */
    public Object visit(ASTAndNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTEQNode,
     *      java.lang.Object)
     */
    public Object visit(ASTEQNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTNENode,
     *      java.lang.Object)
     */
    public Object visit(ASTNENode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTLTNode,
     *      java.lang.Object)
     */
    public Object visit(ASTLTNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTGTNode,
     *      java.lang.Object)
     */
    public Object visit(ASTGTNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTLENode,
     *      java.lang.Object)
     */
    public Object visit(ASTLENode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTGENode,
     *      java.lang.Object)
     */
    public Object visit(ASTGENode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTAddNode,
     *      java.lang.Object)
     */
    public Object visit(ASTAddNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTSubtractNode,
     *      java.lang.Object)
     */
    public Object visit(ASTSubtractNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTMulNode,
     *      java.lang.Object)
     */
    public Object visit(ASTMulNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTDivNode,
     *      java.lang.Object)
     */
    public Object visit(ASTDivNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTModNode,
     *      java.lang.Object)
     */
    public Object visit(ASTModNode aNode, Object aData)
    {
        return null;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.ParserVisitor#visit(org.apache.velocity.runtime.parser.node.ASTNotNode,
     *      java.lang.Object)
     */
    public Object visit(ASTNotNode aNode, Object aData)
    {
        return null;
    }

    private Object visitBlockDirective(Node aNode, Object aData, Directive aDirective, boolean anAddToParentBlock)
    {
        if (anAddToParentBlock && fCurrentBlock instanceof Directive)
        {
            IBlock parent = (IBlock) ((Directive) fCurrentBlock).getParent();
            parent.addDirective(aDirective);
        } else
        {
            fCurrentBlock.addDirective(aDirective);
        }
        fBlocks.push(fCurrentBlock);
        fCurrentBlock = aDirective;
        aData = aNode.childrenAccept(this, aData);
        fCurrentBlock = (IBlock) fBlocks.pop();
        return aData;
    }

    public Object visit(ASTEscapedDirective arg0, Object arg1)
    {
	// TODO Auto-generated method stub
	return null;
    }

    public Object visit(ASTEscape arg0, Object arg1)
    {
	// TODO Auto-generated method stub
	return null;
    }

    public Object visit(ASTFloatingPointLiteral arg0, Object arg1)
    {
	// TODO Auto-generated method stub
	return null;
    }

    public Object visit(ASTIntegerLiteral arg0, Object arg1)
    {
	// TODO Auto-generated method stub
	return null;
    }

    public Object visit(ASTMap arg0, Object arg1)
    {
	// TODO Auto-generated method stub
	return null;
    }

    public Object visit(ASTIntegerRange arg0, Object arg1)
    {
	// TODO Auto-generated method stub
	return null;
    }

}
