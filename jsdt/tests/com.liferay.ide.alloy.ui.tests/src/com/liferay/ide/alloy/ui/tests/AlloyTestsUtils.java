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

package com.liferay.ide.alloy.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.getEditor;
import static com.liferay.ide.ui.tests.UITestsUtils.getElementContentEndOffset;
import static com.liferay.ide.ui.tests.UITestsUtils.getSourceViewerConfiguraionFromOpenedEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

/**
 * Some methods are modified from eclipse wst sse tests
 *
 * @author Kuo Zhang
 * @author Terry Jia
 */

@SuppressWarnings( "restriction" )
public class AlloyTestsUtils
{

    public static ICompletionProposal[] getWebSevicesProposals( IFile file , String elementName ) throws Exception
    {
        final StructuredTextViewer viewer = getEditor( file ).getTextViewer();
        final SourceViewerConfiguration srcViewConf = getSourceViewerConfiguraionFromOpenedEditor( file );

        final ContentAssistant contentAssistant = (ContentAssistant) srcViewConf.getContentAssistant( viewer );

        final String partitionTypeID =
            viewer.getDocument().getPartition( getElementContentEndOffset( file, elementName ) ).getType();

        final IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor( partitionTypeID );

        final ICompletionProposal[] proposals =
            processor.computeCompletionProposals( viewer, getElementContentEndOffset( file, elementName ) );

        return proposals;
    }
}