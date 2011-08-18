/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ReferenceService;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh.sampath
 */

public final class PortletReferenceService extends ReferenceService {

	private static final String QUERY_BY_NAME = "portlet-name";
	private static final String QUERY_BY_DISPLAY_NAME = "display-name";

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

	/**
	 * 
	 */
	@Override
	public Object resolve( final String reference ) {
		final IPortletApp portletApp = nearest( IPortletApp.class );

		if ( portletApp != null ) {
			for ( IPortlet portlet : portletApp.getPortlets() ) {
				if ( params == null || QUERY_BY_NAME.equals( params[0] ) ) {
					if ( equal( portlet.getPortletName().getContent(), reference ) ) {
						return portlet;
					}
				}
				else if ( params == null || QUERY_BY_DISPLAY_NAME.equals( params[0] ) ) {
					if ( equal( portlet.getPortletName().getContent(), reference ) ) {
						return portlet;
					}
				}
			}
		}

		return null;
	}

}
