/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.services.DerivedValueService;
import org.eclipse.sapphire.services.DerivedValueServiceData;

/**
 * @author Gregory Amerson
 */
public class RelationshipLabelService extends DerivedValueService
{

	@Override
	protected void initDerivedValueService()
	{
	}

	@Override
	protected DerivedValueServiceData compute()
	{
		String value = null;
		IServiceBuilder sb = context( IModelElement.class ).nearest( IServiceBuilder.class );

		if ( sb != null )
		{
			if ( sb.getShowRelationshipLabels().getContent() )
			{
				value = ( context( IRelationship.class ) ).getForeignKeyColumnName().getContent();
			}
		}

		if ( value == null )
		{
			value = "";
		}

		return new DerivedValueServiceData( value );
	}

}
