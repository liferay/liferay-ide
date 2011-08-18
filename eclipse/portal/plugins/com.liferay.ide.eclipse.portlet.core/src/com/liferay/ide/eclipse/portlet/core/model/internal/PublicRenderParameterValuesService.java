/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.PossibleValuesService;

import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;
import com.liferay.ide.eclipse.portlet.core.model.IPublicRenderParameter;

/**
 * @author kamesh.sampath
 */
public class PublicRenderParameterValuesService extends PossibleValuesService {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
	 */
	@Override
	protected void fillPossibleValues( SortedSet<String> values ) {
		final IPortletApp portletApp = nearest( IPortletApp.class );
		ModelElementList<IPublicRenderParameter> publicRenderParameters = portletApp.getPublicRenderParameters();
		for ( IPublicRenderParameter renderParameter : publicRenderParameters ) {
			final String name = renderParameter.getName().getContent();
			final String qName = getQName( renderParameter );
			if ( name != null ) {
				values.add( name );
			}
			else if ( qName != null ) {
				values.add( name );
			}
		}
	}

	/**
	 * @param eventDefinition
	 * @return
	 */
	private String getQName( IPublicRenderParameter renderParameter ) {
		QName qName = null;
		String nsURI = renderParameter.getNamespaceURI().getContent();
		String localPart = renderParameter.getLocalPart().getContent();
		qName = new QName( nsURI, localPart );
		return qName.toString();
	}
}
