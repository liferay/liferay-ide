package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.services.ReferenceService;

public class EntityRelationshipService extends ReferenceService
{

    public static IEntity findEntity(final String entityName, final IServiceBuilder serviceBuilder)
    {
        if( entityName != null && serviceBuilder != null)
        {
            if( serviceBuilder != null )
            {
                for( IEntity entity : serviceBuilder.getEntities() )
                {
                    if( entityName.equals( entity.getName().getContent() ) )
                    {
                        return entity;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Object resolve( final String reference )
    {
        return findEntity( reference, context( IServiceBuilder.class ) );
    }

}
