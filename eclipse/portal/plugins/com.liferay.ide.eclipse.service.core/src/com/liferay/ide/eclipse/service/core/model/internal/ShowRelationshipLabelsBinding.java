package com.liferay.ide.eclipse.service.core.model.internal;

import org.eclipse.sapphire.modeling.ValueBindingImpl;


public class ShowRelationshipLabelsBinding extends ValueBindingImpl {

	protected boolean showRelationshipLabels = true;

	@Override
	public String read() {
		return Boolean.toString( showRelationshipLabels );
	}

	@Override
	public void write( String value ) {
		showRelationshipLabels = Boolean.parseBoolean( value );
	}

}
