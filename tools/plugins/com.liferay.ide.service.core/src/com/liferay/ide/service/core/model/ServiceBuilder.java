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

package com.liferay.ide.service.core.model;

import com.liferay.ide.service.core.model.internal.NamespaceValidationService;
import com.liferay.ide.service.core.model.internal.PackagePathValidationService;
import com.liferay.ide.service.core.model.internal.RelationshipsBindingImpl;
import com.liferay.ide.service.core.model.internal.ShowRelationshipLabelsBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.VersionCompatibilityTarget;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@VersionCompatibilityTarget(version = "${ Version }", versioned = "Service Builder")
public interface ServiceBuilder extends Element {

	public ElementType TYPE = new ElementType(ServiceBuilder.class);

	public Value<String> getAuthor();

	// *** Package-path ***

	public ElementList<Entity> getEntities();

	public ElementList<Exception> getExceptions();

	public Value<String> getNamespace();

	// *** Auto-Namespace-Tables ***

	public Value<String> getPackagePath();

	public ElementList<Relationship> getRelationships();

	public ElementList<ServiceBuilderImport> getServiceBuilderImports();

	public Value<Boolean> getShowRelationshipLabels();

	// *** Author ***

	public Value<Boolean> isAutoNamespaceTables();

	public void setAuthor(String value);

	public void setAutoNamespaceTables(Boolean value);

	// *** namespace ***

	public void setAutoNamespaceTables(String value);

	public void setNamespace(String value);

	public void setPackagePath(String value);

	// *** Entities ***

	public void setShowRelationshipLabels(Boolean value);

	public void setShowRelationshipLabels(String value);

	// *** Exceptions ***

	@Label(standard = "&Author")
	@XmlBinding(path = "author")
	public ValueProperty PROP_AUTHOR = new ValueProperty(TYPE, "Author");

	@DefaultValue(text = "true")
	@Label(standard = "&Auto namespace tables")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@auto-namespace-tables")
	public ValueProperty PROP_AUTO_NAMESPACE_TABLES = new ValueProperty(TYPE, "AutoNamespaceTables");

	@Label(standard = "Entities")
	@Type(base = Entity.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "entity", type = Entity.class))
	public ListProperty PROP_ENTITIES = new ListProperty(TYPE, "Entities");

	@Label(standard = "exceptions")
	@Type(base = Exception.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "exception", type = Exception.class), path = "exceptions"
	)
	public ListProperty PROP_EXCEPTIONS = new ListProperty(TYPE, "Exceptions");

	@Label(standard = "&Namespace")
	@Service(impl = NamespaceValidationService.class)
	@XmlBinding(path = "namespace")
	public ValueProperty PROP_NAMESPACE = new ValueProperty(TYPE, "Namespace");

	@Label(standard = "&Package path")
	@Service(impl = PackagePathValidationService.class)
	@XmlBinding(path = "@package-path")
	public ValueProperty PROP_PACKAGE_PATH = new ValueProperty(TYPE, "PackagePath");

	@CustomXmlListBinding(impl = RelationshipsBindingImpl.class)
	@Type(base = Relationship.class)
	public ListProperty PROP_RELATIONSHIPS = new ListProperty(TYPE, "Relationships");

	@Label(standard = "service builder imports")
	@Type(base = ServiceBuilderImport.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "service-builder-import", type = ServiceBuilderImport.class)
	)
	public ListProperty PROP_SERVICE_BUILDER_IMPORTS = new ListProperty(TYPE, "ServiceBuilderImports");

	@CustomXmlValueBinding(impl = ShowRelationshipLabelsBinding.class)
	@DefaultValue(text = "true")
	@Type(base = Boolean.class)
	public ValueProperty PROP_SHOW_RELATIONSHIP_LABELS = new ValueProperty(TYPE, "ShowRelationshipLabels");

}