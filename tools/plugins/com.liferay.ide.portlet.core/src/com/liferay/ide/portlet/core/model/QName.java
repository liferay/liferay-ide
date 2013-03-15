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
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.NameOrQnameValidationService;
import com.liferay.ide.portlet.core.model.internal.QNameLocalPartValueBinding;
import com.liferay.ide.portlet.core.model.internal.QNamespaceValueBinding;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface QName extends IModelElement
{

    ModelElementType TYPE = new ModelElementType( QName.class );

    // *** NamespaceURI ***

    @Label( standard = "Namespace URI" )
    @XmlBinding( path = "qname" )
    @Service( impl = NameOrQnameValidationService.class )
    @CustomXmlValueBinding( impl = QNamespaceValueBinding.class, params = { "qname" } )
    ValueProperty PROP_NAMESPACE_URI = new ValueProperty( TYPE, "NamespaceURI" ); //$NON-NLS-1$

    Value<String> getNamespaceURI();

    void setNamespaceURI( String value );

    // *** LocalPart ***

    @Label( standard = "Local Part" )
    @XmlBinding( path = "qname" )
    @Service( impl = NameOrQnameValidationService.class )
    @CustomXmlValueBinding( impl = QNameLocalPartValueBinding.class, params = { "qname", "localpart" } )
    ValueProperty PROP_LOCAL_PART = new ValueProperty( TYPE, "LocalPart" ); //$NON-NLS-1$

    Value<String> getLocalPart();

    void setLocalPart( String value );

}
