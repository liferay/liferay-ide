package com.liferay.ide.xml.search.ui.quickassist;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;
import com.liferay.ide.xml.search.ui.MarkerResolutionProposal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;


/**
 * @author Kuo Zhang
 */
public abstract class AbstractQuickAssistProcessorFromMarkerResolution implements IQuickAssistProcessor
{

    @Override
    @SuppressWarnings( "unchecked" )
    public ICompletionProposal[] computeQuickAssistProposals( IQuickAssistInvocationContext context )
    {
        ICompletionProposal[] retval = null;

        final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        final ISourceViewer sourceViewer = context.getSourceViewer();
        final IAnnotationModel annotationModel = sourceViewer.getAnnotationModel();
        final Iterator<Annotation> annotations = annotationModel.getAnnotationIterator();

        while( annotations.hasNext() )
        {
            final Annotation annotation = annotations.next();

            Position position = annotationModel.getPosition( annotation );

            try
            {
                final IMarker marker = getMarkerFromAnnotation( annotation );
                final int lineNum = sourceViewer.getDocument().getLineOfOffset( position.getOffset() ) + 1;
                final int currentLineNum = sourceViewer.getDocument().getLineOfOffset( context.getOffset() ) + 1;

                if( marker != null && currentLineNum == lineNum )
                {
                    final ICompletionProposal[] resolutions = createFromMarkerResolutions( marker );

                    if( !CoreUtil.isNullOrEmpty( resolutions ) )
                    {
                        Collections.addAll( proposals, resolutions );

                        if( annotation instanceof IQuickFixableAnnotation )
                        {
                            final IQuickFixableAnnotation quick = (IQuickFixableAnnotation) annotation;
                            quick.setQuickFixable( true );
                        }
                    }
                }
            }
            catch( BadLocationException e )
            {
                LiferayXMLSearchUI.logError( "Error finding quick assists", e );
            }
        }

        if( proposals.size() > 0 )
        {
            retval = proposals.toArray( new ICompletionProposal[0] );
        }

        return retval;
    }

    protected ICompletionProposal[] createFromMarkerResolutions( IMarker marker )
    {
        final List<ICompletionProposal> retval = new ArrayList<ICompletionProposal>();

        if( IDE.getMarkerHelpRegistry().hasResolutions( marker ) )
        {
            final IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry().getResolutions( marker );

            for( IMarkerResolution resolution : resolutions )
            {
                retval.add( new MarkerResolutionProposal( resolution, marker ) );
            }
        }

        return retval.toArray( new ICompletionProposal[0] );
    }


    protected abstract IMarker getMarkerFromAnnotation( Annotation annotation );
}
