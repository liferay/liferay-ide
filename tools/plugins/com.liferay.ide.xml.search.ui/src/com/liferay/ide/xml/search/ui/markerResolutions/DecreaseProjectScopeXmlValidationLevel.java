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

package com.liferay.ide.xml.search.ui.markerResolutions;

import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;


/**
 * @author Kuo Zhang
 */
public class DecreaseProjectScopeXmlValidationLevel implements IMarkerResolution2
{

    private final static String MESSAGE = "Decrease validation level of this marker for this project to Ignore";

    public DecreaseProjectScopeXmlValidationLevel()
    {
    }

    @Override
    public String getDescription()
    {
        return MESSAGE;
    }

    @Override
    public Image getImage()
    {
        final URL url = LiferayXMLSearchUI.getDefault().getBundle().getEntry( "/icons/arrow_down.png" );
        return ImageDescriptor.createFromURL( url ).createImage();
    }

    @Override
    public String getLabel()
    {
        return MESSAGE;
    }

    @Override
    public void run( IMarker marker )
    {
        ValidationPreferences.setProjectScopeValLevel( marker.getResource().getProject(), -1 );
        ComponentUtil.validateFile( (IFile) marker.getResource(), new NullProgressMonitor() );
    }

}
