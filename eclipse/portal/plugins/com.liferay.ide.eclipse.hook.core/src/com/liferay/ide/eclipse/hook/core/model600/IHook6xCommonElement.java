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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model600;

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.hook.core.model.IHookCommonElement;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface IHook6xCommonElement extends IHookCommonElement {

	ModelElementType TYPE = new ModelElementType( IHook6xCommonElement.class );

	// *** IndexerPostProcessors ***

	@Type( base = IIndexerPostProcessor.class )
	@Label( standard = "Index Post Processors" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "indexer-post-processor", type = IIndexerPostProcessor.class ) } )
	ListProperty PROP_INDEXER_POST_PROCESSORS = new ListProperty( TYPE, "IndexerPostProcessors" );

	ModelElementList<IIndexerPostProcessor> getIndexerPostProcessors();

}
