/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 *******************************************************************************/
package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class TempMarker implements IMarker
{
    private final TemporaryAnnotation annotation;
    private final Map<String, Object> attributes;
    private final long creationTime;
    private final IFile file;
    private final String type;

    public TempMarker( TemporaryAnnotation temp )
    {
        this.annotation = temp;
        this.attributes = new HashMap<String, Object>();
        this.creationTime = System.currentTimeMillis();

        for( Object key : this.annotation.getAttributes().keySet() )
        {
            this.attributes.put( key.toString(), this.annotation.getAttributes().get( key ) );
        }

        this.file =
            CoreUtil.getWorkspaceRoot().getFile(
                Path.fromPortableString( (String) this.attributes.get( XMLSearchConstants.FULL_PATH ) ) );

        this.type = (String) this.attributes.get( XMLSearchConstants.MARKER_TYPE );
    }

    @Override
    public void delete() throws CoreException
    {
    }

    @Override
    public boolean exists()
    {
        return false;
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    public Object getAdapter( Class adapter )
    {
        return null;
    }

    @Override
    public Object getAttribute( String attributeName ) throws CoreException
    {
        return annotation.getAttributes().get( attributeName );
    }

    @Override
    public boolean getAttribute( String attributeName, boolean defaultValue )
    {
        final Object value = annotation.getAttributes().get( attributeName );

        return value instanceof Boolean ? Boolean.parseBoolean( value.toString() ) : defaultValue;
    }

    @Override
    public int getAttribute( String attributeName, int defaultValue )
    {
        final Object value = annotation.getAttributes().get( attributeName );

        return value instanceof Integer ? Integer.parseInt( value.toString() ) : defaultValue;
    }

    @Override
    public String getAttribute( String attributeName, String defaultValue )
    {
        final Object value = annotation.getAttributes().get( attributeName );

        return value != null ? value.toString() : defaultValue;
    }

    @Override
    public Map<String, Object> getAttributes() throws CoreException
    {
        return this.attributes;
    }

    @Override
    public Object[] getAttributes( String[] attributeNames ) throws CoreException
    {
        final List<Object> retval = new ArrayList<Object>();

        for( String attributeName : attributeNames )
        {
            if( this.attributes.get( attributeName ) != null )
            {
                retval.add( this.attributes.get( attributeName ) );
            }
        }

        return retval.toArray( new Object[0] );
    }

    @Override
    public long getCreationTime() throws CoreException
    {
        return this.creationTime;
    }

    @Override
    public long getId()
    {
        return -1;
    }

    @Override
    public IResource getResource()
    {
        return this.file;
    }

    @Override
    public String getType() throws CoreException
    {
        return this.type;
    }

    @Override
    public boolean isSubtypeOf( String superType ) throws CoreException
    {
        return false;
    }

    @Override
    public void setAttribute( String attributeName, boolean value ) throws CoreException
    {
    }

    @Override
    public void setAttribute( String attributeName, int value ) throws CoreException
    {
    }

    @Override
    public void setAttribute( String attributeName, Object value ) throws CoreException
    {
    }

    @Override
    public void setAttributes( Map<String, ? extends Object> attributes ) throws CoreException
    {
    }

    @Override
    public void setAttributes( String[] attributeNames, Object[] values ) throws CoreException
    {
    }

}
