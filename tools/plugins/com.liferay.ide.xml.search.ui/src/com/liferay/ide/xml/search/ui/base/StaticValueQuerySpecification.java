/*******************************************************************************
 * Copyright (c) 2011 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.base;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.search.core.statics.IStaticValueQuerySpecification;
import org.eclipse.wst.xml.search.core.statics.IStaticValueVisitor;
import org.eclipse.wst.xml.search.core.statics.IStaticValueVisitorProvider;

/**
 * Implementation of {@link IStaticValueQuerySpecification}.
 */
public class StaticValueQuerySpecification implements IStaticValueQuerySpecification
{

    private final IStaticValueVisitorProvider provider;
    private final IStaticValueVisitor visitor;

    public StaticValueQuerySpecification( IStaticValueVisitorProvider provider )
    {
        this.visitor = null;
        this.provider = provider;
    }

    public StaticValueQuerySpecification( IStaticValueVisitor visitor )
    {
        this.visitor = visitor;
        this.provider = null;
    }

    public IStaticValueVisitor getVisitor( Object selectedNode, IFile file )
    {
        if( provider != null )
        {
            return provider.getVisitor( selectedNode, file );
        }

        return visitor;
    }

    public static IStaticValueQuerySpecification newQuerySpecification( Object createExecutableExtension )
    {
        if( createExecutableExtension instanceof IStaticValueVisitorProvider )
        {
            return new StaticValueQuerySpecification( (IStaticValueVisitorProvider) createExecutableExtension );
        }

        if( createExecutableExtension instanceof IStaticValueVisitor )
        {
            return new StaticValueQuerySpecification( (IStaticValueVisitor) createExecutableExtension );
        }

        return null;
    }

}
