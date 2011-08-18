/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ReferenceService;

import com.liferay.ide.eclipse.portlet.core.model.IEventDefinition;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh.sampath
 */
public class EventDefinitionReferenceService extends ReferenceService {

	private static final String QUERY_BY_NAME = "name";

	private static final String QUERY_BY_QNAME = "qname";

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
	 * @see org.eclipse.sapphire.modeling.ReferenceService#resolve(java.lang.String)
	 */
	@Override
	public Object resolve( String reference ) {
		final IPortletApp config = nearest( IPortletApp.class );

		if ( config != null ) {
			for ( IEventDefinition eventDefinition : config.getEventDefinitions() ) {

				if ( QUERY_BY_NAME.equals( this.params[0] ) ) {
					if ( equal( eventDefinition.getName().getContent(), reference ) ) {
						return eventDefinition;
					}
				}
				else if ( QUERY_BY_QNAME.equals( this.params[0] ) ) {
					if ( equal( getQName( eventDefinition ), reference ) ) {
						return eventDefinition;
					}
				}

			}
		}

		return null;
	}

	/**
	 * @param eventDefinition
	 * @return
	 */
	private String getQName( IEventDefinition eventDefinition ) {
		QName qName = null;
		String nsURI = eventDefinition.getNamespaceURI().getContent();
		String localPart = eventDefinition.getLocalPart().getContent();
		qName = new QName( nsURI, localPart );
		return qName.toString();
	}
}
