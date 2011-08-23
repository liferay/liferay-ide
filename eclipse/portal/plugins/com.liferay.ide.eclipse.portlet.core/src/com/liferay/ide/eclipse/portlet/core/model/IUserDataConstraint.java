/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface IUserDataConstraint extends IModelElement, IIdentifiable, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IUserDataConstraint.class );

	/*
	 * Transport Gurantee
	 */

	@Type( base = TransportGurantee.class )
	@Label( standard = "Transport Gurantee" )
	@NoDuplicates
	@XmlBinding( path = "transport-guarantee" )
	@MustExist
	@DefaultValue( text = "NONE" )
	ValueProperty PROP_TRANSPORT_GURANTEE = new ValueProperty( TYPE, "TransportGurantee" );

	Value<TransportGurantee> getTransportGurantee();

	void setTransportGurantee( TransportGurantee version );

	void setTransportGurantee( String version );
}
