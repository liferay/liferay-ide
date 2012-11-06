/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.theme.core.operation;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.DescriptorHelper;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public class ThemeDescriptorHelper extends DescriptorHelper 
{

    public static final String DEFUALT_FILE_TEMPLATE =
        "<?xml version=\"1.0\"?>\n<!DOCTYPE look-and-feel PUBLIC \"-//Liferay//DTD Look and Feel 6.0.0//EN\" \"http://www.liferay.com/dtd/liferay-look-and-feel_6_0_0.dtd\">\n\n<look-and-feel>\n\t<compatibility>\n\t\t<version>__VERSION__</version>\n\t</compatibility>\n\t<theme id=\"__ID__\" name=\"__NAME__\" />\n</look-and-feel>";

    public ThemeDescriptorHelper( IProject project )
    {
        super( project );
    }

    public static void createDefaultFile( IFile lookAndFeelFile, String version, String id, String name )
    {
        if( lookAndFeelFile == null || id == null || name == null )
        {
            return;
        }

        try
        {
            CoreUtil.prepareFolder( (IFolder) lookAndFeelFile.getParent() );
            String contents =
                DEFUALT_FILE_TEMPLATE.replaceAll( "__VERSION__", version ).replaceAll( "__ID__", id ).replaceAll(
                    "__NAME__", name );
            lookAndFeelFile.create( new ByteArrayInputStream( contents.getBytes() ), true, null );
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }
    }

}
