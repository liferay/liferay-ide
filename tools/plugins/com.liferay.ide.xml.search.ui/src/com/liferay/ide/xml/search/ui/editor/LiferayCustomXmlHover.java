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

import java.util.Iterator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.wst.xml.search.editor.hover.XMLReferencesInfoHoverProcessor;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( {"unchecked", "restriction"} )
public class LiferayCustomXmlHover extends XMLReferencesInfoHoverProcessor implements ITextHoverExtension2
{

    public LiferayCustomXmlHover()
    {
    }

    public LiferayCustomXmlHover( ISourceViewer sourceViewer, String contentType, int stateMask )
    {
    }

    @Override
    public IRegion getHoverRegion( final ITextViewer textViewer, final int offset )
    {
        IDocument document = textViewer.getDocument();

        if( document == null )
        {
            return null;
        }

        CompoundRegion compoundRegion = new CompoundRegion( textViewer, offset );

        if( textViewer instanceof ISourceViewer )
        {
            ISourceViewer sourceViewer = (ISourceViewer) textViewer;
            IAnnotationModel model = sourceViewer.getAnnotationModel();
            if( model != null )
            {
                Iterator<Annotation> it = model.getAnnotationIterator();

                while( it.hasNext() )
                {
                    Annotation annotation = it.next();

                    if( annotation instanceof MarkerAnnotation )
                    {
                        Position pos = sourceViewer.getAnnotationModel().getPosition( annotation );

                        if( pos.includes( offset ) )
                        {
                            compoundRegion.addRegion(
                                new MarkerRegion( pos.getOffset(), pos.getLength(), (MarkerAnnotation) annotation ) );
                        }
                    }
                }
            }

            final IRegion normalRegion = super.getHoverRegion( textViewer, offset );

            if( normalRegion != null )
            {
                String content = getHoverInfo( textViewer, normalRegion );

                if( content != null )
                {
                    compoundRegion.addRegion( new InfoRegion(
                        normalRegion.getOffset(), normalRegion.getLength(), getHoverInfo( textViewer, normalRegion ) ) );
                }
            }
        }

        return compoundRegion.getRegions().size() > 0 ? compoundRegion : null;
    }

    @Override
    public IInformationControlCreator getHoverControlCreator()
    {
        return new IInformationControlCreator()
        {
            public IInformationControl createInformationControl( Shell parent )
            {
                return new LiferayCustomXmlHoverControl( parent );
            }
        };
    }

    @Override
    public Object getHoverInfo2( ITextViewer textViewer, IRegion hoverRegion )
    {
        return hoverRegion;
    }
}
