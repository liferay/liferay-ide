/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.PossibleValuesService;

import com.liferay.ide.eclipse.portlet.core.model.IEventDefinition;
import com.liferay.ide.eclipse.portlet.core.model.IEventDefinitionRef;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;
import com.liferay.ide.eclipse.portlet.core.model.IPublicRenderParameter;
import com.liferay.ide.eclipse.portlet.core.model.ISupportedPublicRenderParameter;

/**
 * @author kamesh.sampath
 */
public class QNamesPossibleValuesService extends PossibleValuesService {

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
	 * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
	 */
	@Override
	protected void fillPossibleValues( SortedSet<String> values ) {
		IModelElement imodelElement = element();
		values.add( params[0] );
		IPortletApp portletApp = element().nearest( IPortletApp.class );
		if ( imodelElement instanceof IEventDefinitionRef ) {
			ModelElementList<IEventDefinition> eventDefs = portletApp.getEventDefinitions();
			for ( IEventDefinition eventDefinition : eventDefs ) {
				values.add( getQName(
					eventDefinition.getNamespaceURI().getContent(), eventDefinition.getLocalPart().getContent() ) );
			}
		}
		else if ( imodelElement instanceof ISupportedPublicRenderParameter ) {
			ModelElementList<IPublicRenderParameter> publicRenderParameters = portletApp.getPublicRenderParameters();
			for ( IPublicRenderParameter publicRenderParam : publicRenderParameters ) {
				values.add( getQName(
					publicRenderParam.getNamespaceURI().getContent(), publicRenderParam.getLocalPart().getContent() ) );
			}
		}

	}

	/**
	 * @param nsURI
	 * @param localPart
	 * @return
	 */
	private String getQName( String nsURI, String localPart ) {
		QName qName = null;

		qName = new QName( nsURI, localPart );
		return qName.toString();
	}
}
