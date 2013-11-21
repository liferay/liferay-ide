/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.project.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;

import com.liferay.ide.project.core.util.ProjectUtil;

/**
 * 
 * @author Kuo Zhang
 *
 */
public class LiferayProjectLanguageFileValidator implements IFacetedProjectValidator
{
    public static final String ID_LANGUAGE_FILE_Encoding_NOT_DEFAULT= "language-file-encoding-not-default";

    public static final String MSG_LANGUAGE_FILE_ENCCODING_NOT_DEFAULT = Msgs.languageFileEncodingNotDefault;

    public void validate( IFacetedProject fproj ) throws CoreException
    {
        final IProject proj = fproj.getProject();

        ProjectUtil.deleteProjectMarkers( proj, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE, getMarkerSourceIds() );

        if( proj != null && ProjectUtil.hasNonDefaultEncodingLanguageFile( proj ) )
        {
            ProjectUtil.setProjectMarker(
                proj, LiferayProjectCore.LIFERAY_PROJECT_MARKR_TYPE, IMarker.SEVERITY_WARNING,
                MSG_LANGUAGE_FILE_ENCCODING_NOT_DEFAULT, "", ID_LANGUAGE_FILE_Encoding_NOT_DEFAULT );
        }
    }


    private Set<String> getMarkerSourceIds()
    {
        Set<String> retval = new HashSet<String>();

        retval.add( ID_LANGUAGE_FILE_Encoding_NOT_DEFAULT );

        return retval;
    }

    private static class Msgs extends NLS
    {

        public static String languageFileEncodingNotDefault;

        static
        {
            initializeMessages( LiferayProjectLanguageFileValidator.class.getName(), Msgs.class );
        }
    }

   

}
