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
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.editor;

import com.liferay.ide.xml.search.ui.TempMarker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenterExtension;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.IInformationControlExtension5;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

/**
 * @author Kuo Zhang
 * @see org.eclipse.m2e.editor.xml.internal.MarkerHoverControl.
 */
@SuppressWarnings( "restriction" )
public class LiferayCustomXmlHoverControl extends AbstractInformationControl
                                          implements IInformationControlExtension2,
                                                     IInformationControlExtension3,
                                                     IInformationControlExtension5
{

    private static final String ONE_QUICK_FIX  = "1 quick fix available:";
    private static final String MULTIPLE_QUICK_FIXES = "{0} quick fixes available:";

    private CompoundRegion region;
    private Control focusControl;
    private Composite parent;
    private final DefaultMarkerAnnotationAccess markerAccess;

    public LiferayCustomXmlHoverControl( Shell shell )
    {
        super( shell, EditorsUI.getTooltipAffordanceString() );
        markerAccess = new DefaultMarkerAnnotationAccess();
        create();
    }

    private void apply( ICompletionProposal p, ITextViewer viewer, int offset, boolean isMultiFix )
    {
        // Focus needs to be in the text viewer, otherwise linked mode does not work
        dispose();

        IRewriteTarget target = null;
        try
        {
            IDocument document = viewer.getDocument();

            if( viewer instanceof ITextViewerExtension )
            {
                ITextViewerExtension extension = (ITextViewerExtension) viewer;
                target = extension.getRewriteTarget();
            }

            if( target != null )
            {
                target.beginCompoundChange();
            }

            if( p instanceof ICompletionProposalExtension2 )
            {
                ICompletionProposalExtension2 e = (ICompletionProposalExtension2) p;
                e.apply( viewer, (char) 0, isMultiFix ? SWT.CONTROL : SWT.NONE, offset );
            }
            else if( p instanceof ICompletionProposalExtension )
            {
                ICompletionProposalExtension e = (ICompletionProposalExtension) p;
                e.apply( document, (char) 0, offset );
            }
            else
            {
                p.apply( document );
            }

            Point selection = p.getSelection( document );

            if( selection != null )
            {
                viewer.setSelectedRange( selection.x, selection.y );
                viewer.revealRange( selection.x, selection.y );
            }
        }
        catch( Exception e )
        {
        }
        finally
        {
            if( target != null )
            {
                target.endCompoundChange();
            }
        }
    }

    private void apply( IMarkerResolution res, IMarker mark, ITextViewer viewer, int offset )
    {
        if( res instanceof ICompletionProposal )
        {
            apply( (ICompletionProposal) res, viewer, offset, false );
        }
        else
        {
            dispose();
            res.run( mark );
        }
    }

    public Point computeSizeHint()
    {
        Point preferedSize = getShell().computeSize( SWT.DEFAULT, SWT.DEFAULT, true );

        Point constrains = getSizeConstraints();

        if( constrains == null )
        {
            return preferedSize;
        }

        Point constrainedSize = getShell().computeSize( constrains.x, SWT.DEFAULT, true );

        int width = Math.min( preferedSize.x, constrainedSize.x );
        int height = Math.max( preferedSize.y, constrainedSize.y );

        return new Point( width, height );
    }

    private void createAnnotationInformation( Composite parent, final Annotation annotation )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
        GridLayout layout = new GridLayout( 2, false );
        layout.marginHeight = 2;
        layout.marginWidth = 2;
        layout.horizontalSpacing = 0;
        composite.setLayout( layout );

        // this paints the icon..
        final Canvas canvas = new Canvas( composite, SWT.NO_FOCUS );
        GridData gridData = new GridData( SWT.BEGINNING, SWT.BEGINNING, false, false );
        gridData.widthHint = 17;
        gridData.heightHint = 16;
        canvas.setLayoutData( gridData );

        canvas.addPaintListener
        (
            new PaintListener()
            {
                public void paintControl( PaintEvent e )
                {
                    e.gc.setFont( null );
                    markerAccess.paint( annotation, e.gc, canvas, new Rectangle( 0, 0, 16, 16 ) );
                }
            }
        );

        // and now comes the text
        StyledText text = new StyledText( composite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY );
        GridData data = new GridData( SWT.FILL, SWT.FILL, true, true );
        text.setLayoutData( data );
        String annotationText = annotation.getText();

        if( annotationText != null )
        {
            text.setText( annotationText );
        }
    }

    private Link createCompletionProposalLink( Composite parent, final IMarker mark,
                                               final IMarkerResolution proposal, int count )
    {
        final boolean isMultiFix = count > 1;

        if( isMultiFix )
        {
            new Label( parent, SWT.NONE ); // spacer to fill image cell
            parent = new Composite( parent, SWT.NONE ); // indented composite for multi-fix
            GridLayout layout = new GridLayout( 2, false );
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            parent.setLayout( layout );
        }

        Label proposalImage = new Label( parent, SWT.NONE );
        proposalImage.setLayoutData( new GridData( SWT.BEGINNING, SWT.TOP, false, false ) );
        Image image = null;

        if( proposal instanceof ICompletionProposal )
        {
            image = ( (ICompletionProposal) proposal ).getImage();
        }
        else if( proposal instanceof IMarkerResolution2 )
        {
            image = ( (IMarkerResolution2) proposal ).getImage();
        }
        if( image != null )
        {
            proposalImage.setImage( image );

            proposalImage.addMouseListener( new MouseAdapter()
            {
                public void mouseUp( MouseEvent e )
                {
                    if( e.button == 1 )
                    {
                        apply( proposal, mark, region.textViewer, region.textOffset );
                    }
                }
            });
        }

        Link proposalLink = new Link( parent, SWT.WRAP );
        GridData layoutData = new GridData( SWT.BEGINNING, SWT.TOP, false, false );
        String linkText = proposal.getLabel();

        proposalLink.setText( "<a>" + linkText + "</a>" );
        proposalLink.setLayoutData( layoutData );

        proposalLink.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    apply( proposal, mark, region.textViewer, region.textOffset );
                }
            }
        );

        return proposalLink;
    }

    protected void createContent( Composite parent )
    {
        this.parent = parent;
        GridLayout layout = new GridLayout( 1, false );
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout( layout );
    }

    private void createResolutionsControl( Composite parent, IMarker marker, IMarkerResolution[] resolutions )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.verticalSpacing = 2;
        layout.marginHeight = 0;
        composite.setLayout( layout );

        Label quickFixLabel = new Label( composite, SWT.NONE );
        GridData layoutData = new GridData( SWT.BEGINNING, SWT.TOP, false, false );
        layoutData.horizontalIndent = 4;
        quickFixLabel.setLayoutData( layoutData );
        String text;

        if( resolutions.length == 1 )
        {
            text = ONE_QUICK_FIX;
        }
        else
        {
            text = NLS.bind( MULTIPLE_QUICK_FIXES, String.valueOf( resolutions.length ) );
        }

        quickFixLabel.setText( text );

        Composite composite2 = new Composite( parent, SWT.NONE );
        composite2.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        GridLayout layout2 = new GridLayout( 2, false );
        layout2.marginLeft = 5;
        layout2.verticalSpacing = 2;
        composite2.setLayout( layout2 );

        List<Link> list = new ArrayList<Link>();

        for( IMarkerResolution resolution : resolutions )
        {
            // Original link for single fix, hence pass 1 for count
            list.add( createCompletionProposalLink( composite2, marker, resolution, 1 ) );
        }

        final Link[] links = list.toArray( new Link[0] );

        focusControl = links[0];
        for( int i = 0; i < links.length; i++ )
        {
            final int index = i;
            final Link link = links[index];

            link.addKeyListener( new KeyListener()
            {
                public void keyPressed( KeyEvent e )
                {
                    switch( e.keyCode )
                    {
                        case SWT.ARROW_DOWN:
                        {
                            if( index + 1 < links.length )
                            {
                                links[index + 1].setFocus();
                            }

                            break;
                        }
                        case SWT.ARROW_UP:
                        {
                            if( index > 0 )
                            {
                                links[index - 1].setFocus();
                            }

                            break;
                        }

                        default: break;
                    }
                }

                public void keyReleased( KeyEvent e )
                {
                }
            } );

        }
    }

    protected void deferredCreateContent()
    {
        if( region != null )
        {
            final ScrolledComposite scrolledComposite = new ScrolledComposite( parent, SWT.V_SCROLL );
            GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
            scrolledComposite.setLayoutData( gridData );
            scrolledComposite.setExpandVertical( false );
            scrolledComposite.setExpandHorizontal( false );

            Composite composite = new Composite( scrolledComposite, SWT.NONE );
            composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
            GridLayout layout = new GridLayout( 1, false );
            composite.setLayout( layout );

            scrolledComposite.setContent( composite );

            for( IRegion reg : region.getRegions() )
            {
                if( reg instanceof MarkerRegion )
                {
                    final MarkerRegion markerReg = (MarkerRegion) reg;
                    createAnnotationInformation( composite, markerReg.getAnnotation() );
                    final IMarker marker = markerReg.getAnnotation().getMarker();
                    IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry().getResolutions( marker );

                    if( resolutions.length > 0 )
                    {
                        createResolutionsControl( composite, marker, resolutions );
                        // createSeparator( composite );
                    }
                }
                else if( reg instanceof TemporaryRegion )
                {
                    final TemporaryRegion tempReg = (TemporaryRegion) reg;
                    createAnnotationInformation( composite, tempReg.getAnnotation() );
                    final IMarker marker = new TempMarker( tempReg.getAnnotation() );

                    IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry().getResolutions( marker );

                    if( resolutions.length > 0 )
                    {
                        createResolutionsControl( composite, marker, resolutions );
                    }
                }
                else if( reg instanceof InfoRegion )
                {
                    String text ;

                    if( ( text = ( (InfoRegion) reg ).getInfo() ) != null )
                    {
                        setInformation( composite, text );
                    }
                }
            }

            Point constraints = getSizeConstraints();
            Point contentSize = composite.computeSize( constraints != null ? constraints.x : SWT.DEFAULT, SWT.DEFAULT );

            composite.setSize( new Point( contentSize.x, contentSize.y ) );
        }

        setColorAndFont( parent, parent.getForeground(), parent.getBackground(), JFaceResources.getDialogFont() );

        parent.layout( true );
    }

    protected void disposeDeferredCreatedContent()
    {
        Control[] children = parent.getChildren();

        for( Control child : children )
        {
            child.dispose();
        }

        ToolBarManager toolBarManager = getToolBarManager();

        if( toolBarManager != null )
        {
            toolBarManager.removeAll();
        }
    }


    public IInformationControlCreator getInformationPresenterControlCreator()
    {
        return new IInformationControlCreator()
        {
            public IInformationControl createInformationControl( Shell parent )
            {
                return new LiferayCustomXmlHoverControl( parent );
            }
        };
    }

    Shell getMyShell()
    {
        return super.getShell();
    }

    Control getRoot()
    {
        return parent;
    }

    public boolean hasContents()
    {
        return region != null;
    }

    private void setColorAndFont( Control control, Color foreground, Color background, Font font )
    {
        control.setForeground( foreground );
        control.setBackground( background );
        control.setFont( font );

        if( control instanceof Composite )
        {
            Control[] children = ( (Composite) control ).getChildren();

            for( Control child : children )
            {
                setColorAndFont( child, foreground, background, font );
            }
        }
    }

    public void setFocus()
    {
        super.setFocus();
        if( focusControl != null )
        {
            focusControl.setFocus();
        }
    }

    /**
     * @see org.eclipse.jface.text.DefaultInformationControl#setInformation(String)
     */
    private void setInformation( Composite composite, String content )
    {
        final HTMLTextPresenter presenter = new HTMLTextPresenter( true );

        final TextPresentation presentation = new TextPresentation();

        int maxWidth = -1;
        int maxHeight = -1;

        Point constraints = getSizeConstraints();
        StyledText styledText = new StyledText( composite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY );

        int innerBorder = 1;

        if( constraints != null )
        {
            maxWidth = constraints.x;
            maxHeight = constraints.y;

            if( styledText.getWordWrap() )
            {
                maxWidth -= innerBorder* 2;
                maxHeight -= innerBorder* 2;
            }
            else
            {
                maxWidth -= innerBorder;
            }

            Rectangle trim = computeTrim();
            maxWidth -= trim.width;
            maxHeight -= trim.height;
            maxWidth -= styledText.getCaret().getSize().x;
        }

        if( isResizable() )
        {
            maxHeight = Integer.MAX_VALUE;
        }

        if( presenter instanceof IInformationPresenterExtension )
        {
            content = ( (IInformationPresenterExtension) presenter ).
                updatePresentation( styledText, content, presentation, maxWidth, maxHeight );
        }

        if( content != null )
        {
            styledText.setText( content );
            TextPresentation.applyTextPresentation( presentation, styledText );
        }
        else
        {
            styledText.setText( "" );
        }
    }

    public void setInput( Object input )
    {
        if( input instanceof CompoundRegion )
        {
            region = (CompoundRegion) input;
        }
        else
        {
            // throw new IllegalStateException( "Not CompoundRegion" );
            return;
        }

        disposeDeferredCreatedContent();
        deferredCreateContent();
    }

    public final void setVisible( boolean visible )
    {
        if( ! visible )
        {
            disposeDeferredCreatedContent();
        }

        super.setVisible( visible );
    }

}
