/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.services.ValueImageService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.sapphire.ui.forms.PropertyEditorDef;
import org.eclipse.sapphire.ui.forms.swt.AbstractBinding;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.RadioButtonsGroup;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */
public final class PossibleValuesRadioButtonsGroupBinding<T> extends AbstractBinding
{
    @Text( "<value not set>" )
    private static LocalizableText nullValueLabel;

    static
    {
        LocalizableText.init( PossibleValuesRadioButtonsGroupBinding.class );
    }

    private RadioButtonsGroup buttonsGroup;
    private Button badValueButton;
    private List<String> possibleValues;

    public PossibleValuesRadioButtonsGroupBinding( final PropertyEditorPresentation propertyEditorPresentation,
        final RadioButtonsGroup buttonsGroup )
    {
        super( propertyEditorPresentation, buttonsGroup );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected void initialize( PropertyEditorPresentation propertyEditorPresentation, Control control )
    {
        super.initialize( propertyEditorPresentation, control );

        final PossibleValuesService possibleValuesService = propertyEditorPresentation.property().service( PossibleValuesService.class );
        this.possibleValues = new ArrayList<String>( possibleValuesService.values() );

        this.buttonsGroup = (RadioButtonsGroup) control;

        final Property property = propertyEditorPresentation.property();

        String auxTextProviderName = propertyEditorPresentation.part().getRenderingHint( "possible.values.aux.text.provider", (String)null ); //$NON-NLS-1
        PossibleValuesAuxTextProvider auxTextProvider = null;

        if( auxTextProviderName != null )
        {
            try
            {
                Class<PossibleValuesAuxTextProvider> providerClass =
                    (Class<PossibleValuesAuxTextProvider>) ProjectUI.getDefault().getBundle().loadClass(
                        auxTextProviderName );
                auxTextProvider = providerClass.newInstance();
            }
            catch( Exception e )
            {
            }
        }

        for( String possibleValue : this.possibleValues )
        {
            final ValueLabelService labelService = property.service( ValueLabelService.class );
            final String possibleValueText = labelService.provide( possibleValue );
            String auxText = propertyEditorPresentation.part().getRenderingHint( PropertyEditorDef.HINT_AUX_TEXT + "." + possibleValue, null ); //$NON-NLS-1$

            if( auxText == null && auxTextProvider != null )
            {
                auxText = auxTextProvider.getAuxText( this.element(), property.definition(), possibleValue );
            }

            ValueImageService imageService = property.service( ValueImageService.class );
            ImageData imageData = imageService.provide( possibleValue );
            Image image = presentation().resources().image( imageData );
            final Button button = this.buttonsGroup.addRadioButton( possibleValueText, auxText, image );
            button.setData( possibleValue );
        }

        this.buttonsGroup.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent event )
                {
                    updateModel();
                    updateTargetAttributes();
                }
            }
        );
    }

    private int getSelectionIndex()
    {
        return this.buttonsGroup.getSelectionIndex();
    }

    private void setSelectionIndex( final int index )
    {
        this.buttonsGroup.setSelectionIndex( index );
    }

    private void createMalformedItem( String label )
    {
        if( this.badValueButton == null )
        {
            this.badValueButton = this.buttonsGroup.addRadioButton( MiscUtil.EMPTY_STRING );
        }

        this.badValueButton.setText( label );
        presentation().layout();
    }

    private void removeMalformedItem()
    {
        if( ! this.buttonsGroup.isDisposed() )
        {
            if( this.badValueButton != null )
            {
                this.badValueButton.dispose();
                this.badValueButton = null;
                presentation().layout();
            }
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Value<T> property()
    {
        return (Value<T>) super.property();
    }

    @Override

    protected final void doUpdateModel()
    {
        final int index = getSelectionIndex();

        if( index >= 0 && index < this.possibleValues.size() )
        {
            property().write( this.possibleValues.get( index ) );
            removeMalformedItem();
        }
    }

    @Override

    protected final void doUpdateTarget()
    {
        final int existingSelection = getSelectionIndex();
        final Value<T> value = property();

        int newSelection = this.possibleValues.size();

        if( ! value.malformed() )
        {
            final T newValue = value.content( true );

            for( int i = 0, n = this.possibleValues.size(); i < n; i++ )
            {
                if( this.possibleValues.get( i ).equals( newValue.toString() ) )
                {
                    newSelection = i;
                    break;
                }
            }
        }

        if( newSelection == this.possibleValues.size() )
        {
            final String newValueString = value.text( true );
            final String label = ( newValueString == null ? nullValueLabel.text() : newValueString );

            createMalformedItem( label );
        }
        else
        {
            removeMalformedItem();
        }

        if( existingSelection != newSelection )
        {
            setSelectionIndex( newSelection );
        }
    }

}
