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

package com.liferay.ide.kaleo.ui.action;


import com.liferay.ide.kaleo.core.model.Node;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;


/**
 * @author Gregory Amerson
 */
public abstract class DefaultListAddActionHandler extends PropertyEditorActionHandler
{

    private final ElementType type;
    private final ListProperty property;

    public DefaultListAddActionHandler( final ElementType type, final ListProperty property )
    {
        this.type = type;
        this.property = property;
    }

    public static String getDefaultName( final String initialName, final Node newNode, final Node[] nodes )
    {
        String newName = initialName;
        int count = 1;
        boolean newNameIsValid = false;

        do
        {
            newNameIsValid = true;

            for (Node node : nodes)
            {
               if (newName.equals(node.getName().content()))
               {
                   newNameIsValid = false;
                   break;
               }
            }

            if (!newNameIsValid)
            {
                newName = newName.replace( Integer.toString( count ), "" ) + (++count);
            }
        }
        while (!newNameIsValid);

        return newName;
    }

    @Override
    public void init( final SapphireAction action,
                      final ActionHandlerDef def )
    {
        super.init( action, def );

        final ImageData typeSpecificAddImage = this.type.image();

        if( typeSpecificAddImage != null )
        {
            addImage( typeSpecificAddImage );
        }

        setLabel( this.type.getLabel( false, CapitalizationType.TITLE_STYLE, false ) );
    }

    public final ElementList<Element> getList()
    {
        final Element modelElement = getModelElement();

        if( modelElement != null )
        {
            return modelElement.property( this.property );
        }

        return null;
    }

    @Override
    protected Object run( Presentation context )
    {
        return getList().insert( this.type );
    }

}
