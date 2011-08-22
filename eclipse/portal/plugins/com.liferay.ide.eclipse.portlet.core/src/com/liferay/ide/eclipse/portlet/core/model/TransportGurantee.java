
package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author kamesh
 */
@Label( standard = "Transport Gurantee" )
public enum TransportGurantee {
	@Label( standard = "NONE" )
	@EnumSerialization( primary = "NONE" )
	NONE,

	@Label( standard = "INTEGRAL" )
	@EnumSerialization( primary = "INTEGRAL" )
	INTEGRAL,

	@Label( standard = "CONFIDENTIAL" )
	@EnumSerialization( primary = "CONFIDENTIAL" )
	CONFIDENTIAL

}
