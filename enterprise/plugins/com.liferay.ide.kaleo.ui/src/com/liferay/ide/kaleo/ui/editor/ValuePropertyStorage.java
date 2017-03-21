/******************************************************************************
 * Copyright (c) 2014 Liferay, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package com.liferay.ide.kaleo.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;

/**
 * A storage object that can read a sapphire value property.
 *
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */
public class ValuePropertyStorage extends PlatformObject implements IStorage
{

    private static final String EMPTY_CONTENTS = "";

    private Element modelElement;
    private ValueProperty valueProperty;

    public ValuePropertyStorage( Element modelElement, ValueProperty valueProperty )
    {
        super();
        this.modelElement = modelElement;
        this.valueProperty = valueProperty;
    }

    protected Element element()
    {
        return this.modelElement;
    }

    public InputStream getContents() throws CoreException
    {
        Object content =  this.modelElement.property( this.valueProperty ).content();

        if( content == null )
        {
            content = EMPTY_CONTENTS;
        }

        return new ByteArrayInputStream( content.toString().getBytes() );
    }

    public IPath getFullPath()
    {
        IPath retval = null;

        String localPath = this.modelElement.type().getSimpleName() + "." + this.valueProperty.name();

        IFile file = this.modelElement.adapt( IFile.class );

        if( file != null )
        {
            retval = file.getFullPath().append( localPath );
        }
        else
        {
            retval = new Path( localPath );
        }

        return retval;
    }

    public String getName()
    {
        return this.valueProperty.name();
    }

    public boolean isReadOnly()
    {
        return false;
    }

}
