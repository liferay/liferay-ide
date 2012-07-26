package com.liferay.ide.velocity.vaulttec.ui.editor.parser;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * Dummy implementation of a Velocity user directive. It only provides a name
 * and a type but no rendering.
 */
public class VelocityDirective extends Directive
{
    private String fName = "<NOSET>";
    private int    fType = -1;

    public VelocityDirective()
    {
      
    }
    
    public VelocityDirective(String aName, int aType)
    {
        fName = aName;
        fType = aType;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return fName;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getType()
    {
        return fType;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aContext
     *            DOCUMENT ME!
     * @param aWriter
     *            DOCUMENT ME!
     * @param aNode
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws IOException
     *             DOCUMENT ME!
     * @throws ResourceNotFoundException
     *             DOCUMENT ME!
     * @throws ParseErrorException
     *             DOCUMENT ME!
     * @throws MethodInvocationException
     *             DOCUMENT ME!
     */
    public boolean render(InternalContextAdapter aContext, Writer aWriter, Node aNode)
    {
        return true;
    }
}
