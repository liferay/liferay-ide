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

package com.liferay.ide.kaleo.ui.xml;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

public class KaleoStructuredTextViewConfiguration extends StructuredTextViewerConfigurationXML
{

	public KaleoStructuredTextViewConfiguration()
	{
		super();
	}

	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors( ISourceViewer sourceViewer, String partitionType )
	{
		if( partitionType == IStructuredPartitions.DEFAULT_PARTITION || partitionType == IXMLPartitions.XML_DEFAULT )
		{
			return new IContentAssistProcessor[] { new KaleoContentAssistProcessor( sourceViewer ) };
		}

		return super.getContentAssistProcessors( sourceViewer, partitionType );
	}

}
