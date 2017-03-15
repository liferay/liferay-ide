package com.liferay.ide.kaleo.core.op;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

@Label( standard = "state type" )
public enum NewStateType {

	@Label( standard = "default" )
	@EnumSerialization( primary = "default" )
	DEFAULT,

	@Label( standard = "start" )
	@EnumSerialization( primary = "start" )
	START,

	@Label( standard = "end" )
	@EnumSerialization( primary = "end" )
	END,

}
