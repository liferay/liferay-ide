package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.services.DerivedValueService;


public class RelationshipLabelService extends DerivedValueService {

	@Override
	public String getDerivedValue() {
		IModelParticle parent = context( IModelElement.class ).parent();

		while ( parent != null && !( parent instanceof IServiceBuilder ) ) {
			parent = parent.parent();
		}

		if ( parent instanceof IServiceBuilder ) {
			IServiceBuilder sb = (IServiceBuilder) parent;

			if ( sb.getShowRelationshipLabels().getContent() ) {
				return ( context( IRelationship.class ) ).getForeignKeyColumnName().getContent();
			}
		}

		return "";
	}

}
