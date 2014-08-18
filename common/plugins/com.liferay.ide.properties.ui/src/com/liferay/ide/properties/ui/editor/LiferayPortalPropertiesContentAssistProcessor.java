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
package com.liferay.ide.properties.ui.editor;

import java.io.File;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;


/**
 * @author Gregory Amerson
 */
public class LiferayPortalPropertiesContentAssistProcessor implements IContentAssistProcessor
{
    private final String contentType;
    private final File propertiesFile;


    public LiferayPortalPropertiesContentAssistProcessor( File propertiesFile , String contentType )
    {
        this.contentType = contentType;
        this.propertiesFile = propertiesFile;
    }

    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        return null;
    }

    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return null;
    }

    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }

    public String getErrorMessage()
    {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

}
