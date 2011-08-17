/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.DerivedValueService;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;

import com.liferay.ide.eclipse.portlet.core.model.IQNamed;

/**
 * @author kamesh.sampath
 */
public class QNameDerivedValueService extends DerivedValueService {

	String[] params;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ModelPropertyService#init(org.eclipse.sapphire.modeling.IModelElement,
	 * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
	 */
	@Override
	public void init( IModelElement element, ModelProperty property, String[] params ) {
		super.init( element, property, params );
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.DerivedValueService#getDerivedValue()
	 */
	@Override
	public String getDerivedValue() {
		IModelElement imodelElement = element();
		QName qName = null;
		if ( imodelElement instanceof IQNamed ) {
			IQNamed qnamedElement = (IQNamed) imodelElement;
			String namespaceURI = qnamedElement.getNamespaceURI().getContent();
			String localPart = qnamedElement.getLocalPart().getContent();
			qName = new QName( namespaceURI, localPart );
		}
		// System.out.println( "QNameDerivedValueService.getDerivedValue()" + qName );
		return qName != null ? qName.toString() : "";
	}
}
