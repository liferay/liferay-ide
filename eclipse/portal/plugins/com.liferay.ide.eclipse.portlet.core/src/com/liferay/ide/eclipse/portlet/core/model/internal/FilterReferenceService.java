/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import org.eclipse.sapphire.modeling.ReferenceService;

import com.liferay.ide.eclipse.portlet.core.model.IFilter;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh.sampath
 */

public final class FilterReferenceService extends ReferenceService {

	@Override
	public Object resolve( final String reference ) {
		final IPortletApp portletApp = nearest( IPortletApp.class );

		if ( portletApp != null ) {
			for ( IFilter iFilter : portletApp.getFilters() ) {
				if ( equal( iFilter.getName().getContent(), reference ) ) {
					return iFilter;
				}
			}
		}

		return null;
	}

}
