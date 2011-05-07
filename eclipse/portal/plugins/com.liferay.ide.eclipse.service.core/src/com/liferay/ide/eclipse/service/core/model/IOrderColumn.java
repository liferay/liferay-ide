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
@Image(path = "images/column_16x16.gif")
public interface IOrderColumn extends IModelElement {

    ModelElementType TYPE = new ModelElementType( IOrderColumn.class );
    
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

	// *** Order By ***

	@Label(standard = "order by")
	@XmlBinding(path = "@order-by")
	@PossibleValues(values = { "asc", "desc" }, invalidValueMessage = "{0} is not valid.")
	ValueProperty PROP_ORDER_BY = new ValueProperty(TYPE, "OrderBy");

	Value<String> getOrderBy();

	void setOrderBy(String value);

}
