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
package com.liferay.ide.xml.search.core.validators;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ValidationPreferences.ValidationType;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.editor.references.IXMLReference;
import org.eclipse.wst.xml.search.editor.validation.XMLReferencesBatchValidator;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class LiferayPortletDescriptorValidator extends LiferayDescriptorBaseValidator
{
    public static final String MARKER_TYPE = "com.liferay.ide.xml.search.core.liferayPortletDescriptorMarker";
    public static final String MESSAGE_ENTRY_WEIGHT_SYNTAX_INVALID = Msgs.entryWeightSyntaxInvalid;

    @Override
    public void doValidate( IXMLReference reference, IDOMNode node, IFile file,
                          IValidator validator, IReporter reporter, boolean batchMode )
    {
        super.doValidate( reference, node, file, validator, reporter, batchMode );
    }

    @Override
    protected void setMarker( IValidator validator, IFile file )
    {
        if( validator instanceof XMLReferencesBatchValidator &&
            ILiferayConstants.LIFERAY_PORTLET_XML_FILE.equals( file.getName() ) )
        {
            ( (XMLReferencesBatchValidator) validator ).getParent().setMarkerId( MARKER_TYPE );
        }
    }

    @Override
    protected boolean validateSyntax( IXMLReference reference, IDOMNode node, IFile file,
                                      IValidator validator, IReporter reporter, boolean batchMode )
    {
        int severity = getServerity( ValidationType.SYNTAX_INVALID, file );

        if( severity != ValidationMessage.IGNORE )
        {
            if( node.getParentNode().getNodeName().equals( "control-panel-entry-weight" ) )
            {
                String validationMsg = null;

                final String nodeValue = node.getNodeValue().replaceAll( "(^\\s*)|(\\s*$)", StringPool.BLANK );;

                if( nodeValue != null )
                {
                    try
                    {
                        Double.parseDouble( nodeValue );
                    }
                    catch( NumberFormatException nfe )
                    {
                        validationMsg = NLS.bind( MESSAGE_ENTRY_WEIGHT_SYNTAX_INVALID, nodeValue );;
                    }
                }

                if( validationMsg != null )
                {
                    addMessage( node, file, validator, reporter, batchMode, validationMsg, severity );
                    return false;
                }
            }
        }

        return true;
    }

    private static class Msgs extends NLS
    {
        public static String entryWeightSyntaxInvalid;

        static
        {
            initializeMessages( LiferayPortletDescriptorValidator.class.getName(), Msgs.class );
        }
    }
}
