package com.liferay.ide.eclipse.service.core.model;

import org.eclipse.sapphire.modeling.DerivedValueService;


public class RelationshipLabelService extends DerivedValueService {

	@Override
	public String getDerivedValue() {
		return ( (IRelationship) element() ).getForeignKeyColumnName().getContent();
	}

}
