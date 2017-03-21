/******************************************************************************
 * Copyright (c) 2014 Liferay, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package com.liferay.ide.kaleo.ui.editor;

import static org.eclipse.sapphire.ui.forms.PropertyEditorPart.RELATED_CONTROLS;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvfill;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glspacing;

import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.PropertyEditorDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentationFactory;
import org.eclipse.sapphire.ui.forms.swt.SapphireToolBarActionPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.sapphire.ui.listeners.ValuePropertyEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Create a full-featured code editor part based on platform default text editor. Adopters can specify contentType and
 * fileExtension hints that will be used when querying the platform for the correct editor descriptor.
 *
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */
@SuppressWarnings( "restriction" )
public class ScriptPropertyEditorRenderer extends PropertyEditorPresentation
{

    public static final class Factory extends PropertyEditorPresentationFactory
    {
        @Override
        public PropertyEditorPresentation create( PropertyEditorPart part, SwtPresentation parent, Composite composite )
        {
            return new ScriptPropertyEditorRenderer( part, parent, composite );
        }
    }

    private IEditorPart editorPart;

    public ScriptPropertyEditorRenderer( FormComponentPart part, SwtPresentation context, Composite composite )
    {
        super( part, context, composite );
    }

    @Override
    protected boolean canScaleVertically()
    {
        return true;
    }

    @Override
    protected void createContents( Composite parent )
    {
        final PropertyEditorPart part = part();
        final Element element = part.getLocalModelElement();
        final ValueProperty property = part.property().nearest( ValueProperty.class );

        final Composite codeEditorParent = createMainComposite
        (
            parent,
            new CreateMainCompositeDelegate( part )
            {
                @Override
                public boolean canScaleVertically()
                {
                    return true;
                }
            }
        );

//        this.context.adapt( codeEditorParent );

        int codeEditorParentColumns = 1;
        final SapphireToolBarActionPresentation toolBarActionsPresentation =
            new SapphireToolBarActionPresentation( getActionPresentationManager() );

        final boolean isActionsToolBarNeeded = toolBarActionsPresentation.hasActions();
        if( isActionsToolBarNeeded )
        {
            codeEditorParentColumns++;
        }

        codeEditorParent.setLayout( glayout( codeEditorParentColumns, 0, 0, 0, 0 ) );

        final Composite nestedComposite = new Composite( codeEditorParent, SWT.NONE );
        nestedComposite.setLayoutData( gdfill() );
        // nestedComposite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );

        addControl( nestedComposite );

        final PropertyEditorAssistDecorator decorator = createDecorator( nestedComposite );

        decorator.control().setLayoutData( gdvalign( gd(), SWT.TOP ) );
        decorator.addEditorControl( nestedComposite );

        final ScriptPropertyEditorInput editorInput = new ScriptPropertyEditorInput( element, property );
        final List<Control> relatedControls = new ArrayList<Control>();

        try
        {
            final IEditorSite editorSite = this.part().adapt( IEditorSite.class );

            this.editorPart = createEditorPart( editorInput, editorSite );

            this.editorPart.createPartControl( nestedComposite );

            Control editorControl = (Control) this.editorPart.getAdapter( Control.class );

            // need to find the first child of nestedComposite to relayout editor control

            Composite editorControlParent = null;
            Control control = editorControl;

            while( editorControlParent == null && control != null && !nestedComposite.equals( control.getParent() ) )
            {
                control = control.getParent();
            }

            nestedComposite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );
            control.setLayoutData( gdfill() );

            decorator.addEditorControl( editorControl, true );

            editorControl.setData( RELATED_CONTROLS, relatedControls );

        }
        catch( Exception e )
        {
            KaleoUI.logError( e );
        }

        if( isActionsToolBarNeeded )
        {
            final ToolBar toolbar = new ToolBar( codeEditorParent, SWT.FLAT | SWT.HORIZONTAL );
            toolbar.setLayoutData( gdvfill() );
            toolBarActionsPresentation.setToolBar( toolbar );
            toolBarActionsPresentation.render();

            addControl( toolbar );

            decorator.addEditorControl( toolbar );
            relatedControls.add( toolbar );
        }

        final List<Class<?>> listenerClasses =
            part.getRenderingHint( PropertyEditorDef.HINT_LISTENERS, Collections.<Class<?>> emptyList() );
        final List<ValuePropertyEditorListener> listeners = new ArrayList<ValuePropertyEditorListener>();

        if( !listenerClasses.isEmpty() )
        {
            for( Class<?> cl : listenerClasses )
            {
                try
                {
                    final ValuePropertyEditorListener listener = (ValuePropertyEditorListener) cl.newInstance();
                    listener.initialize( this );
                    listeners.add( listener );
                }
                catch( Exception e )
                {
                    KaleoUI.logError( e );
                }
            }
        }

        ITextEditor textEditor = null;

        if( this.editorPart instanceof ITextEditor )
        {
            textEditor = (ITextEditor) this.editorPart;
        }
        else
        {
            textEditor = (ITextEditor) this.editorPart.getAdapter( ITextEditor.class );
        }

        addControl( (Control) textEditor.getAdapter( Control.class ) );

        textEditor.getDocumentProvider().getDocument( this.editorPart.getEditorInput() ).addDocumentListener(
            new IDocumentListener()
            {

                public void documentAboutToBeChanged( DocumentEvent event )
                {
                }

                public void documentChanged( DocumentEvent event )
                {
                    element.property( property ).write( event.getDocument().get() );

                    if( !listeners.isEmpty() )
                    {
                        for( ValuePropertyEditorListener listener : listeners )
                        {
                            try
                            {
                                listener.handleValueChanged();
                            }
                            catch( Exception e )
                            {
                                KaleoUI.logError( e );
                            }
                        }
                    }
                }
            } );
    }

    protected IEditorPart createEditorPart( ScriptPropertyEditorInput editorInput, IEditorSite editorSite )
    {
        IKaleoEditorHelper scriptEditorHelper = KaleoUI.getKaleoEditorHelper( editorInput.getScriptLanguage() );

        if( scriptEditorHelper == null )
        {
            scriptEditorHelper = new DefaultScriptEditorHelper();
        }

        return scriptEditorHelper.createEditorPart( editorInput, editorSite );
    }

    // public static final class Groovy extends PropertyEditorRendererFactory
    // {
    //
    // @Override
    // public boolean isApplicableTo( final PropertyEditorPart propertyEditorDefinition )
    // {
    // return ( propertyEditorDefinition.getProperty() instanceof ValueProperty );
    // }
    //
    // @Override
    // public PropertyEditorRenderer create( final SapphireRenderingContext context, final PropertyEditorPart part )
    // {
    // return new ScriptPropertyEditorRenderer( context, part, ScriptLanguageType.GROOVY );
    // }
    // }
    //
    // public static final class Javascript extends PropertyEditorRendererFactory
    // {
    //
    // @Override
    // public boolean isApplicableTo( final PropertyEditorPart propertyEditorDefinition )
    // {
    // return ( propertyEditorDefinition.getProperty() instanceof ValueProperty );
    // }
    //
    // @Override
    // public PropertyEditorRenderer create( final SapphireRenderingContext context, final PropertyEditorPart part )
    // {
    // return new ScriptPropertyEditorRenderer( context, part, ScriptLanguageType.JAVASCRIPT );
    // }
    // }

}
