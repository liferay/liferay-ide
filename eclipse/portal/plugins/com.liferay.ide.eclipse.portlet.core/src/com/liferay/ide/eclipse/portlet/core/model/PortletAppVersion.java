
package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author kamesh
 */
@Label( standard = "Portlet Version" )
public enum PortletAppVersion {
	@Label( standard = "1.0" )
	@EnumSerialization( primary = "1.0" )
	V_1_0,

	@Label( standard = "2.0" )
	@EnumSerialization( primary = "2.0" )
	V_2_0,

}
