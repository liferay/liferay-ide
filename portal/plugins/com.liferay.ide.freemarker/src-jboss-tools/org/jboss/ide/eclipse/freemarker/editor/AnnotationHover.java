/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.editor;

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
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class AnnotationHover implements IAnnotationHover {

	/**
	 * @see org.eclipse.jface.text.source.IAnnotationHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, int)
	 */
	public String getHoverInfo(ISourceViewer aViewer, int aLine) {
		String info = null;
		IMarker marker = getMarkerForLine(aViewer, aLine);
		if (marker != null) {
			String message = marker.getAttribute(IMarker.MESSAGE, (String)null);
			if (message != null && message.trim().length() > 0) {
				info = message.trim();
			}
		}
		return info;
	}
	
	/**
	 * Returns one marker which includes the ruler's line of activity.
	 */
	protected IMarker getMarkerForLine(ISourceViewer aViewer, int aLine) {
		IMarker marker = null;
		IAnnotationModel model = aViewer.getAnnotationModel();
		if (model != null) {
			Iterator e = model.getAnnotationIterator();
			while (e.hasNext()) {
				Object o = e.next();
				if (o instanceof MarkerAnnotation) {
					MarkerAnnotation a = (MarkerAnnotation)o;
					if (compareRulerLine(model.getPosition(a),
							aViewer.getDocument(), aLine) != 0) {
						marker = a.getMarker();
					}
				}
			}
		}
		return marker;
	}

	/**
	 * Returns distance of given line to specified position (1 = same line,
	 * 2 = included in given position, 0 = not related).
	 */
	protected int compareRulerLine(Position aPosition, IDocument aDocument,
			int aLine) {
		int distance = 0;
		if (aPosition.getOffset() > -1 && aPosition.getLength() > -1) {
			try {
				int markerLine = aDocument.getLineOfOffset(
						aPosition.getOffset());
				if (aLine == markerLine) {
					distance = 1;
				} else if (markerLine <= aLine && aLine <=
					aDocument.getLineOfOffset(aPosition.getOffset() +
							aPosition.getLength())) {
					distance = 2;
				}
			} catch (BadLocationException e) {
			}
		}
		return distance;
	}
}
