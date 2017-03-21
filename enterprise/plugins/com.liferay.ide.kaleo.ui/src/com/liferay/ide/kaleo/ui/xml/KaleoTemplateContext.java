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

package com.liferay.ide.kaleo.ui.xml;

import com.liferay.ide.kaleo.ui.KaleoUI;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.templates.Template;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
public enum KaleoTemplateContext
{
    DOCUMENT("#document"),

    UNKNOWN("unknown"),

    WORKFLOW_DEFINITION("workflow-definition");

    private static final String PREFIX = KaleoUI.PLUGIN_ID + ".templates.contextType.";

    public static KaleoTemplateContext fromId( String contextTypeId )
    {
        for( KaleoTemplateContext context : values() )
        {
            if( context.getContextTypeId().equals( contextTypeId ) )
            {
                return context;
            }
        }
        return UNKNOWN;
    }

    public static KaleoTemplateContext fromNodeName( String idSuffix )
    {
        for( KaleoTemplateContext context : values() )
        {
            if( context.getNodeName().equals( idSuffix ) )
            {
                return context;
            }
        }
        return UNKNOWN;
    }

    private final String nodeName;

    private KaleoTemplateContext( String nodeName )
    {
        this.nodeName = nodeName;
    }

    /**
     * @param project
     * @param eclipsePrj
     *            only here because getSearchEngine() requires it as parameter.
     * @param templates
     * @param currentNode
     * @param prefix
     * @throws CoreException
     */
    protected void addTemplates( IProject eclipsePrj, Collection<Template> templates, Node currentNode, String prefix )
        throws CoreException
    {
    }

    public String getContextTypeId()
    {
        return PREFIX + nodeName;
    }

    protected String getNodeName()
    {
        return nodeName;
    }

    /**
     * Return templates depending on the context type.
     */
    public Template[] getTemplates( IProject eclipsePrj, Node node, String prefix )
    {
        Collection<Template> templates = new ArrayList<Template>();
        try
        {
            addTemplates( eclipsePrj, templates, node, prefix );
        }
        catch( CoreException e )
        {

        }
        return templates.toArray( new Template[templates.size()] );
    }
}
