/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.project.core.util.VersionedSchemaRootElementController;

import java.util.regex.Pattern;

import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.w3c.dom.Document;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionRootElementController extends VersionedSchemaRootElementController
{
    static final String XML_1998_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    static final String xmlBindingPath = WorkflowDefinition.class.getAnnotation( XmlBinding.class ).path();
    static final Pattern namespacePattern = Pattern.compile( "urn:liferay.com:liferay-workflow_(.*)$" );
    static final Pattern schemaPattern = Pattern.compile( "urn:liferay.com:liferay-workflow_.*(http://www.liferay.com/dtd/liferay-workflow-definition_(.*).xsd)$" );
    static final String namespaceTemplate = "urn:liferay.com:liferay-workflow_{0}";
    static final String schemaTemplate = "http://www.liferay.com/dtd/liferay-workflow-definition_{0}.xsd";
    static final String defaultVersion = KaleoCore.DEFAULT_KALEO_VERSION;

    public WorkflowDefinitionRootElementController()
    {
        super( xmlBindingPath, namespacePattern, schemaPattern, namespaceTemplate, schemaTemplate, defaultVersion );
    }

    @Override
    protected void createRootElement( Document document, RootElementInfo rinfo )
    {
        super.createRootElement( document, rinfo );

        // remove the http://www.w3.org/XML/1998/namespace that breaks validation in the schemaLocation
        final String oldValue = document.getDocumentElement().getAttributeNS( XSI_NAMESPACE, "schemaLocation" );

        if( ! CoreUtil.isNullOrEmpty( oldValue ) && oldValue.startsWith( XML_1998_NAMESPACE ) )
        {
            final String newValue = oldValue.substring( XML_1998_NAMESPACE.length() ).trim();

            document.getDocumentElement().setAttributeNS( XSI_NAMESPACE, XSI_SCHEMA_LOCATION_ATTR, newValue );
        }
    }

}
