package com.liferay.ide.eclipse.service.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


@GenerateImpl
public interface IOrder extends IModelElement {

    ModelElementType TYPE = new ModelElementType( IOrder.class );
    
	// *** By ***

	@Label(standard = "by")
	@XmlBinding(path = "@by")
	@PossibleValues(values = { "asc", "desc" }, invalidValueMessage = "{0} is not valid.")
	ValueProperty PROP_BY = new ValueProperty(TYPE, "By");

	Value<String> getBy();

	void setBy(String value);

	// *** Order Columns ***
    
	@Type(base = IOrderColumn.class)
	@Label(standard = "order columns")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "order-column", type = IOrderColumn.class))
	@CountConstraint(min = 1)
	ListProperty PROP_ORDER_COLUMNS = new ListProperty(TYPE, "OrderColumns");

	ModelElementList<IOrderColumn> getOrderColumns();

}
