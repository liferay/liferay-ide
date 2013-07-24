/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.portal.ui.templates;

import com.liferay.ide.portal.ui.PortalUI;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.templates.Template;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
public enum StructuresTemplateContext
{
    DOCUMENT("#document"), //$NON-NLS-1$

    UNKNOWN("unknown"), //$NON-NLS-1$

    // STRUCTURES_ROOT("structuresRoot"), //$NON-NLS-1$

    STRUCTURES("structures"), //$NON-NLS-1$

    ROOT("root"), //$NON-NLS-1$

    DYNAMIC_ELEMENT("dynamic-element"); //$NON-NLS-1$

    private static final String PREFIX = PortalUI.PLUGIN_ID + ".templates.structures.contextType."; //$NON-NLS-1$

    public static StructuresTemplateContext fromId( String contextTypeId )
    {
        for( StructuresTemplateContext context : values() )
        {
            if( context.getContextTypeId().equals( contextTypeId ) )
            {
                return context;
            }
        }

        return UNKNOWN;
    }

    public static StructuresTemplateContext fromNodeName( String idSuffix )
    {
        for( StructuresTemplateContext context : values() )
        {
            if( context.getNodeName().equals( idSuffix ) )
            {
                return context;
            }
        }

        return UNKNOWN;
    }

    private final String nodeName;

    private StructuresTemplateContext( String nodeName )
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
