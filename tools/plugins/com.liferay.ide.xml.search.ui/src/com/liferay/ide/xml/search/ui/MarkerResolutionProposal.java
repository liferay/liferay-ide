/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.xml.search.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

public class MarkerResolutionProposal implements ICompletionProposal
{

    private final IMarker marker;
    private final IMarkerResolution resolution;

    public MarkerResolutionProposal( IMarkerResolution resolution, IMarker marker )
    {
        this.resolution = resolution;
        this.marker = marker;
    }

    public void apply( IDocument document )
    {
        resolution.run( marker );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( !( obj instanceof MarkerResolutionProposal ) )
            return false;
        MarkerResolutionProposal other = (MarkerResolutionProposal) obj;
        if( resolution == null )
        {
            if( other.resolution != null )
                return false;
        }
        else if( !resolution.equals( other.resolution ) )
            return false;
        return true;
    }

    public String getAdditionalProposalInfo()
    {
        if( resolution instanceof IMarkerResolution2 )
        {
            return ( (IMarkerResolution2) resolution ).getDescription();
        }
        String problemDesc = marker.getAttribute( IMarker.MESSAGE, null );
        if( problemDesc != null )
        {
            return problemDesc;
        }
        return null;
    }

    public IContextInformation getContextInformation()
    {
        return null;
    }

    public String getDisplayString()
    {
        return resolution.getLabel();
    }

    public Image getImage()
    {
        if( resolution instanceof IMarkerResolution2 )
        {
            return ( (IMarkerResolution2) resolution ).getImage();
        }

        return null;
    }

    public Point getSelection( IDocument document )
    {
        return null;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( resolution == null ) ? 0 : resolution.hashCode() );
        return result;
    }
}
