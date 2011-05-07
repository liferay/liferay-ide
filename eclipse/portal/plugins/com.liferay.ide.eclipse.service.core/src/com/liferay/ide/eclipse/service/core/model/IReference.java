package com.liferay.ide.eclipse.service.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


@GenerateImpl
@Image(path = "images/references_16x16.png")
public interface IReference extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IReference.class);
    
	// *** Package-path ***

	@XmlBinding(path = "@package-path")
	@Label(standard = "&Package path")
	ValueProperty PROP_PACKAGE_PATH = new ValueProperty(TYPE, "PackagePath");

	Value<String> getPackagePath();

	void setPackagePath(String value);

	// *** Entity ***

	@XmlBinding(path = "@entity")
	@Label(standard = "&entity")
	ValueProperty PROP_ENTITY = new ValueProperty(TYPE, "Entity");

	Value<String> getEntity();

	void setEntity(String value);
}