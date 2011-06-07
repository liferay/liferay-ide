package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.ReferenceService;


public class EntityRelationshipService extends ReferenceService {

    @Override
	public Object resolve(final String reference) {
		if (reference != null) {
			IServiceBuilder serviceBuilder = element().nearest( IServiceBuilder.class );

			if ( serviceBuilder != null ) {
				for (IEntity entity : serviceBuilder.getEntities()) {
					if ( reference.equals( entity.getName().getContent() ) ) {
						return entity;
					}
				}
			}
        }
        
        return null;
    }

}
