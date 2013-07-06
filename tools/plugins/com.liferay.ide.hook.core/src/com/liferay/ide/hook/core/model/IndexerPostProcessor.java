/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *    Gregory Amerson - IDE-355
 ******************************************************************************/

package com.liferay.ide.hook.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
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
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/elcl16/index_16x16.gif" )
public interface IndexerPostProcessor extends Element
{

    ElementType TYPE = new ElementType( IndexerPostProcessor.class );

    // *** IndexerClassName ***

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "com.liferay.portal.model.BaseModel" )
    @Label( standard = "Indexer Class Name" )
    @XmlBinding( path = "indexer-class-name" )
    @Required
    ValueProperty PROP_INDEXER_CLASS_NAME = new ValueProperty( TYPE, "IndexerClassName" ); //$NON-NLS-1$

    ReferenceValue<JavaTypeName, JavaType> getIndexerClassName();

    void setIndexerClassName( String value );

    void setIndexerClassName( JavaTypeName value );

    // *** Implementation Class ***

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "Indexer Post Processor Impl" )
    @JavaTypeConstraint( kind = { JavaTypeKind.CLASS }, type = "com.liferay.portal.kernel.search.IndexerPostProcessor" )
    @MustExist
    @Required
    @XmlBinding( path = "indexer-post-processor-impl" )
    ValueProperty PROP_INDEXER_CLASS_IMPL = new ValueProperty( TYPE, "IndexerClassImpl" ); //$NON-NLS-1$

    ReferenceValue<JavaTypeName, JavaType> getIndexerClassImpl();

    void setIndexerClassImpl( String value );

    void setIndexerClassImpl( JavaTypeName value );

}
