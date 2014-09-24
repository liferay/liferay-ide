/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.service.core.model;

import com.liferay.ide.service.core.model.internal.NamespaceValidationService;
import com.liferay.ide.service.core.model.internal.PackagePathValidationService;
import com.liferay.ide.service.core.model.internal.RelationshipsBindingImpl;
import com.liferay.ide.service.core.model.internal.ShowRelationshipLabelsBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.VersionCompatibilityTarget;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@VersionCompatibilityTarget( version = "${ Version }", versioned = "Service Builder" )
public interface ServiceBuilder extends Element
{

    ElementType TYPE = new ElementType( ServiceBuilder.class );

    // *** Package-path ***

    @XmlBinding( path = "@package-path" )
    @Label( standard = "&Package path" )
    @Service( impl = PackagePathValidationService.class )
    ValueProperty PROP_PACKAGE_PATH = new ValueProperty( TYPE, "PackagePath" ); //$NON-NLS-1$

    Value<String> getPackagePath();

    void setPackagePath( String value );

    // *** Auto-Namespace-Tables ***

    @DefaultValue( text = "true" )
    @Label( standard = "&Auto namespace tables" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "@auto-namespace-tables" )
    ValueProperty PROP_AUTO_NAMESPACE_TABLES = new ValueProperty( TYPE, "AutoNamespaceTables" ); //$NON-NLS-1$

    Value<Boolean> isAutoNamespaceTables();

    void setAutoNamespaceTables( String value );

    void setAutoNamespaceTables( Boolean value );

    // *** Author ***

    @XmlBinding( path = "author" )
    @Label( standard = "&Author" )
    ValueProperty PROP_AUTHOR = new ValueProperty( TYPE, "Author" ); //$NON-NLS-1$

    Value<String> getAuthor();

    void setAuthor( String value );

    // *** namespace ***

    @XmlBinding( path = "namespace" )
    @Label( standard = "&Namespace" )
    @Service( impl = NamespaceValidationService.class )
    ValueProperty PROP_NAMESPACE = new ValueProperty( TYPE, "Namespace" ); //$NON-NLS-1$

    Value<String> getNamespace();

    void setNamespace( String value );

    // *** Entities ***

    @Type( base = Entity.class )
    @Label( standard = "Entities" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "entity", type = Entity.class ) )
    ListProperty PROP_ENTITIES = new ListProperty( TYPE, "Entities" ); //$NON-NLS-1$

    ElementList<Entity> getEntities();

    // *** Exceptions ***

    @Type( base = Exception.class )
    @Label( standard = "exceptions" )
    @XmlListBinding( path = "exceptions", mappings = @XmlListBinding.Mapping( element = "exception", type = Exception.class ) )
    ListProperty PROP_EXCEPTIONS = new ListProperty( TYPE, "Exceptions" ); //$NON-NLS-1$

    ElementList<Exception> getExceptions();

    @Type( base = ServiceBuilderImport.class )
    @Label( standard = "service builder imports" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "service-builder-import", type = ServiceBuilderImport.class ) )
    ListProperty PROP_SERVICE_BUILDER_IMPORTS = new ListProperty( TYPE, "ServiceBuilderImports" ); //$NON-NLS-1$

    ElementList<ServiceBuilderImport> getServiceBuilderImports();

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @CustomXmlValueBinding( impl = ShowRelationshipLabelsBinding.class )
    ValueProperty PROP_SHOW_RELATIONSHIP_LABELS = new ValueProperty( TYPE, "ShowRelationshipLabels" ); //$NON-NLS-1$

    Value<Boolean> getShowRelationshipLabels();

    void setShowRelationshipLabels( String value );

    void setShowRelationshipLabels( Boolean value );

    @Type( base = Relationship.class )
    @CustomXmlListBinding( impl = RelationshipsBindingImpl.class )
    ListProperty PROP_RELATIONSHIPS = new ListProperty( TYPE, "Relationships" ); //$NON-NLS-1$

    ElementList<Relationship> getRelationships();

}
