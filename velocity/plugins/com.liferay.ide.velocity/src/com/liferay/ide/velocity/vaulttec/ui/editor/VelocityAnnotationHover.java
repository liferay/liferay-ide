package com.liferay.ide.velocity.vaulttec.ui.editor;

import java.util.Iterator;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * Determines marker for the given line and formates the according message.
 */
public class VelocityAnnotationHover implements IAnnotationHover
{

    /**
     * @see org.eclipse.jface.text.source.IAnnotationHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer,
     *      int)
     */
    public String getHoverInfo(ISourceViewer aViewer, int aLine)
    {
        String info = null;
        IMarker marker = getMarkerForLine(aViewer, aLine);
        if (marker != null)
        {
            String message = marker.getAttribute(IMarker.MESSAGE, (String) null);
            if ((message != null) && (message.trim().length() > 0))
            {
                info = message.trim();
            }
        }
        return info;
    }

    /**
     * Returns one marker which includes the ruler's line of activity.
     */
    protected IMarker getMarkerForLine(ISourceViewer aViewer, int aLine)
    {
        IMarker marker = null;
        IAnnotationModel model = aViewer.getAnnotationModel();
        if (model != null)
        {
            Iterator e = model.getAnnotationIterator();
            while (e.hasNext())
            {
                Object o = e.next();
                if (o instanceof MarkerAnnotation)
                {
                    MarkerAnnotation a = (MarkerAnnotation) o;
                    if (compareRulerLine(model.getPosition(a), aViewer.getDocument(), aLine) != 0)
                    {
                        marker = a.getMarker();
                    }
                }
            }
        }
        return marker;
    }

    /**
     * Returns distance of given line to specified position (1 = same line, 2 =
     * included in given position, 0 = not related).
     */
    protected int compareRulerLine(Position aPosition, IDocument aDocument, int aLine)
    {
        int distance = 0;
        if ((aPosition.getOffset() > -1) && (aPosition.getLength() > -1))
        {
            try
            {
                int markerLine = aDocument.getLineOfOffset(aPosition.getOffset());
                if (aLine == markerLine)
                {
                    distance = 1;
                } else if ((markerLine <= aLine) && (aLine <= aDocument.getLineOfOffset(aPosition.getOffset() + aPosition.getLength())))
                {
                    distance = 2;
                }
            }
            catch (BadLocationException e)
            {
            }
        }
        return distance;
    }
}
