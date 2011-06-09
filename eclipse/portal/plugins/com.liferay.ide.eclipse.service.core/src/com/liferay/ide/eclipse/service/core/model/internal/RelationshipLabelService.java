package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.DerivedValueService;
import org.eclipse.sapphire.modeling.IModelParticle;


public class RelationshipLabelService extends DerivedValueService {

	@Override
	public String getDerivedValue() {
		IModelParticle parent = element().parent();

		while ( parent != null && !( parent instanceof IServiceBuilder ) ) {
			parent = parent.parent();
		}

		if ( parent instanceof IServiceBuilder ) {
			IServiceBuilder sb = (IServiceBuilder) parent;

			if ( sb.getShowRelationshipLabels().getContent() ) {
				return ( (IRelationship) element() ).getForeignKeyColumnName().getContent();
			}
		}

		return "";
	}

}
