package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.ReferenceService;


public class EntityRelationshipService extends ReferenceService {

    @Override
	public Object resolve(final String reference) {
		if (reference != null) {
			final IServiceBuilder serviceBuilder = element().nearest(IServiceBuilder.class);
            
            for (IEntity entity : serviceBuilder.getEntities()) {
				if (reference.equals(entity.getName().getContent())) {
					return entity;
                }
            }
        }
        
        return null;
    }

}
