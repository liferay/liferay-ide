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
 *
 *******************************************************************************/

package com.liferay.ide.project.core.upgrade;

import com.liferay.blade.api.Problem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Terry Jia
 */
public class FileProblemsUtil
{
    public static List<FileProblems> getFileProblemsArray( Problem[] problems )
    {
        List<FileProblems> fileProblemsList = new ArrayList<FileProblems>();

        for( Problem problem : problems )
        {
            boolean added = false;

            for( FileProblems fileProblems : fileProblemsList )
            {
                if( fileProblems.getFile().getPath().equals( problem.getFile().getPath() ) )
                {
                    fileProblems.addProblem( problem );

                    added = true;
                    break;
                }
            }

            if( !added )
            {
                FileProblems fileProblems = new FileProblems();
                fileProblems.addProblem( problem );
                fileProblems.setFile( problem.getFile() );
                fileProblemsList.add( fileProblems );
            }
        }

        return fileProblemsList;
    }

}
