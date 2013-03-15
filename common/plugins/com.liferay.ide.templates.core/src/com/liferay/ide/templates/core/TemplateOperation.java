/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.templates.core;

import com.liferay.ide.core.util.CoreUtil;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Gregory Amerson
 */
public class TemplateOperation implements ITemplateOperation
{

    protected ITemplateContext context;
    protected TemplateModel model;
    protected StringBuffer outputBuffer;
    protected IFile outputFile;
    protected Template template;

    public TemplateOperation( TemplateModel model )
    {
        super();
        this.model = model;
    }

    public boolean canExecute()
    {
        try
        {
            if( ( this.outputFile == null && this.outputBuffer == null ) || this.model == null || getTemplate() == null )
            {
                return false;
            }

            String[] names = model.getRequiredVarNames();

            if( !CoreUtil.isNullOrEmpty( names ) )
            {
                for( String name : names )
                {
                    if( !( getContext().containsKey( name ) ) )
                    {
                        TemplatesCore.logError( "Could not execute template operation: context var " + name + //$NON-NLS-1$
                            " not found." ); //$NON-NLS-1$
                        return false;
                    }
                }
            }

            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    public void execute( IProgressMonitor monitor ) throws Exception
    {
        if( !canExecute() )
        {
            return;
        }

        StringWriter writer = new StringWriter();
        getTemplate().merge( (Context) getContext(), writer );
        String result = writer.toString();

        if( this.outputFile != null )
        {
            if( this.outputFile.exists() )
            {
                this.outputFile.setContents( new ByteArrayInputStream( result.getBytes() ), true, true, monitor );
            }
            else
            {
                this.outputFile.create( new ByteArrayInputStream( result.getBytes() ), true, monitor );
            }
        }
        else if( this.outputBuffer != null )
        {
            this.outputBuffer.delete( 0, this.outputBuffer.length() );
            this.outputBuffer.append( result );
        }
    }

    public ITemplateContext getContext()
    {
        if( context == null )
        {
            context = createContext();
        }

        return context;
    }

    public void setOutputBuffer( StringBuffer buffer )
    {
        this.outputBuffer = buffer;
    }

    public void setOutputFile( IFile file )
    {
        this.outputFile = file;
    }

    protected TemplateContext createContext()
    {
        return new TemplateContext();

    }

    protected Template getTemplate() throws Exception
    {

        if( this.model == null )
        {
            return null;
        }

        if( template == null )
        {
            template = this.model.getEngine().getTemplate( this.model.getResource() );
        }

        return template;
    }
}
