package com.liferay.ide.eclipse.service.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


@GenerateImpl
@Image(path = "images/finder_column_16x16.png")
public interface IFinderColumn extends IModelElement {

    ModelElementType TYPE = new ModelElementType( IFinderColumn.class );
    
	// *** Name ***
    
    @XmlBinding(path = "@name")
	@Label(standard = "&name")
    @Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

    Value<String> getName();

	void setName(String value);

	// *** CaseSensitive ***

	@Type(base = Boolean.class)
	@Label(standard = "&case sensitive")
	@XmlBinding(path = "@case-sensitive")
	ValueProperty PROP_CASE_SENSITIVE = new ValueProperty(TYPE, "CaseSensitive");

	Value<Boolean> isCaseSensitive();

	void setCaseSensitive(String value);

	void setCaseSensitive(Boolean value);

	// ** Comparator ***

	@Label(standard = "comparator")
	@XmlBinding(path = "@comparator")
	@PossibleValues(values = { "=", "!=", "<", "<=", ">", ">=", "LIKE" }, invalidValueMessage = "{0} is not a valid comparator.")
	ValueProperty PROP_COMPARATOR = new ValueProperty(TYPE, "Comparator");

	Value<String> getComparator();

	void setComparator(String value);

	// ** Arrayable Operator ***

	@Label(standard = "arrayable operator")
	@XmlBinding(path = "@arrayable-operator")
	@PossibleValues(values = { "AND", "OR" }, invalidValueMessage = "{0} is not a valid arryable operator.")
	ValueProperty PROP_ARRAYABLE_OPERATOR = new ValueProperty(TYPE, "ArrayableOperator");

	Value<String> getArrayableOperator();

	void setArrayableOperator(String value);

}
