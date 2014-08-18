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

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileSourceViewerConfiguration;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayPortalPropertiesSourceViewerConfiguration extends PropertiesFileSourceViewerConfiguration
{
    private File propertiesFile;

    public LiferayPortalPropertiesSourceViewerConfiguration( ITextEditor editor )
    {
        super( JavaPlugin.getDefault().getJavaTextTools().getColorManager(),
            JavaPlugin.getDefault().getCombinedPreferenceStore(), editor,
            IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING );
    }

    @Override
    public IContentAssistant getContentAssistant( final ISourceViewer sourceViewer )
    {
        return new ContentAssistant()
        {
            @Override
            public IContentAssistProcessor getContentAssistProcessor( final String contentType )
            {
                return new LiferayPortalPropertiesContentAssistProcessor( propertiesFile, contentType );
            }
        };
    }

    void setPropertilesFile( File file )
    {
        this.propertiesFile = file;
    }
}
