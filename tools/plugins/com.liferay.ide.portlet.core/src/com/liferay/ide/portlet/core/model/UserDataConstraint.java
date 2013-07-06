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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.InitialValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface UserDataConstraint extends Element, Identifiable, Describeable
{

    ElementType TYPE = new ElementType( UserDataConstraint.class );

    /*
     * Transport Gurantee
     */

    @Type( base = TransportGuarantee.class )
    @Label( standard = "Transport Guarantee" )
    @NoDuplicates
    @Required
    @XmlBinding( path = "transport-guarantee" )
    @InitialValue( text = "NONE" )
    ValueProperty PROP_TRANSPORT_GUARANTEE = new ValueProperty( TYPE, "TransportGuarantee" ); //$NON-NLS-1$

    Value<TransportGuarantee> getTransportGuarantee();

    void setTransportGuarantee( TransportGuarantee version );

    void setTransportGuarantee( String version );
}
