/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.hook.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/index_16x16.gif")
public interface IndexerPostProcessor extends Element {

	public ElementType TYPE = new ElementType(IndexerPostProcessor.class);

	public ReferenceValue<JavaTypeName, JavaType> getIndexerClassImpl();

	public ReferenceValue<JavaTypeName, JavaType> getIndexerClassName();

	public void setIndexerClassImpl(JavaTypeName value);

	public void setIndexerClassImpl(String value);

	public void setIndexerClassName(JavaTypeName value);

	public void setIndexerClassName(String value);

	// *** Implementation Class ***

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.search.IndexerPostProcessor")
	@Label(standard = "Indexer Post Processor Impl")
	@MustExist
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "indexer-post-processor-impl")
	public ValueProperty PROP_INDEXER_CLASS_IMPL = new ValueProperty(TYPE, "IndexerClassImpl");

	// *** IndexerClassName ***

	@JavaTypeConstraint(kind = JavaTypeKind.INTERFACE, type = "com.liferay.portal.model.BaseModel")
	@Label(standard = "Indexer Class Name")
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "indexer-class-name")
	public ValueProperty PROP_INDEXER_CLASS_NAME = new ValueProperty(TYPE, "IndexerClassName");

}