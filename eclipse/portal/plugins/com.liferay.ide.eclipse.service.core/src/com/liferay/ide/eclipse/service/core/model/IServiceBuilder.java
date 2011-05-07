package com.liferay.ide.eclipse.service.core.model;

import com.liferay.ide.eclipse.service.core.model.internal.ServiceBuilderRootElementController;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

@GenerateImpl
@CustomXmlRootBinding(value = ServiceBuilderRootElementController.class)
public interface IServiceBuilder extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IServiceBuilder.class);
	
	// *** Package-path ***

	@XmlBinding(path = "@package-path")
	@Label(standard = "&Package path")
	@Required
	ValueProperty PROP_PACKAGE_PATH = new ValueProperty(TYPE, "PackagePath");

	Value<String> getPackagePath();

	void setPackagePath(String value);

	// *** Auto-Namespace-Tables ***

	@Type(base = Boolean.class)
	@Label(standard = "&Auto namespace tables")
	@XmlBinding(path = "@auto-namespace-tables")
	ValueProperty PROP_AUTO_NAMESPACE_TABLES = new ValueProperty(TYPE, "AutoNamespaceTables");

	Value<Boolean> isAutoNamespaceTables();

	void setAutoNamespaceTables(String value);

	void setAutoNamespaceTables(Boolean value);

	// *** Author ***
    
	@XmlBinding(path = "author")
	@Label(standard = "&Author")
	ValueProperty PROP_AUTHOR = new ValueProperty(TYPE, "Author");

	Value<String> getAuthor();

	void setAuthor(String value);

	// *** namespace ***

	@XmlBinding(path = "namespace")
	@Label(standard = "&Namespace")
	@Required
	ValueProperty PROP_NAMESPACE = new ValueProperty(TYPE, "Namespace");

	Value<String> getNamespace();

	void setNamespace(String value);

	// *** Entities ***

	@Type(base = IEntity.class)
	@Label(standard = "Entities")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "entity", type = IEntity.class))
	@CountConstraint(min = 1)
	ListProperty PROP_ENTITIES = new ListProperty(TYPE, "Entities");

	ModelElementList<IEntity> getEntities();

	// *** Exceptions ***

	@Type(base = IException.class)
	@Label(standard = "exceptions")
	@XmlListBinding(path = "exceptions", mappings = @XmlListBinding.Mapping(element = "exception", type = IException.class))
	ListProperty PROP_EXCEPTIONS = new ListProperty(TYPE, "Exceptions");

	ModelElementList<IException> getExceptions();

}
