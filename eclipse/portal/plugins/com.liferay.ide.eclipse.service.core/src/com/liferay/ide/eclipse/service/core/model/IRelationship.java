package com.liferay.ide.eclipse.service.core.model;

import com.liferay.ide.eclipse.service.core.model.internal.EntityRelationshipService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


@GenerateImpl
@Image(path = "images/references_16x16.png")
public interface IRelationship extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IRelationship.class);

	@Reference(target = IEntity.class)
	@Service(impl = EntityRelationshipService.class)
	@Required
	@XmlBinding(path = "name")
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	ReferenceValue<String, IEntity> getName();

	void setName(String value);

	ValueProperty PROP_FOREIGN_KEY_COLUMN_NAME = new ValueProperty( TYPE, "ForeignKeyColumnName" );

    Value<String> getForeignKeyColumnName();

	void setForeignKeyColumnName( String value );

}
