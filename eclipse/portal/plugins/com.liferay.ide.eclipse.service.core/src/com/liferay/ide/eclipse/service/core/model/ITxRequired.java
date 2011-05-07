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
@Image(path = "images/tx_requireds_16x16.gif")
public interface ITxRequired extends IModelElement {

    ModelElementType TYPE = new ModelElementType( ITxRequired.class );
    
	// *** TxRequired ***

	@XmlBinding(path = "")
	@Label(standard = "&tx required")
	ValueProperty PROP_TX_REQUIRED = new ValueProperty(TYPE, "TxRequired");

	Value<String> getTxRequired();

	void setTxRequired(String value);

}
