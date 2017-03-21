/******************************************************************************
 * Copyright (c) 2014 Liferay, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;

import java.net.URI;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;

/**
 * A storage editor input that can read a sapphire value property.
 *
 * @author Gregory Amerson
 */
public class ScriptPropertyEditorInput extends PlatformObject implements IStorageEditorInput, ILocationProvider
{

    private Element modelElement;
    private ValueProperty valueProperty;

    public ScriptPropertyEditorInput( Element modelElement, ValueProperty property )
    {
        super();
        this.modelElement = modelElement;
        this.valueProperty = property;
    }

    public boolean exists()
    {
        return this.modelElement != null && this.valueProperty != null;
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    public Object getAdapter( Class adapter )
    {
        if( ILocationProvider.class.equals( adapter ) )
        {
            return this;
        }

        return super.getAdapter( adapter );
    }

    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    public String getName()
    {
        return this.valueProperty.name();
    }

    public IPath getPath( Object element )
    {
        return getStorage().getFullPath();
    }

    public IPersistableElement getPersistable()
    {
        return null;
    }

    public ValueProperty getProperty()
    {
        return this.valueProperty;
    }

    public String getScriptLanguage()
    {
        String retval = null;

        try
        {
            ScriptLanguageType scriptType =
                this.modelElement.nearest( Scriptable.class ).getScriptLanguage().content();

            EnumSerialization enumValue =
                scriptType.getClass().getFields()[scriptType.ordinal()].getAnnotation( EnumSerialization.class );

            retval = enumValue.primary();
        }
        catch( Exception e )
        {
        }

        return retval;
    }

    public IStorage getStorage()
    {
        return new ScriptPropertyStorage( this.modelElement, this.valueProperty );
    }

    public String getToolTipText()
    {
        return getStorage().getFullPath().toPortableString();
    }

    public URI getURI( Object element )
    {
        return getStorage().getFullPath().toFile().toURI();
    }

}
