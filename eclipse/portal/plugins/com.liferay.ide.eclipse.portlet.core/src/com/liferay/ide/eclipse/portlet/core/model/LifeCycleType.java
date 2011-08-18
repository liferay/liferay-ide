/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author kamesh.sampath
 */
public enum LifeCycleType {

	@Label( standard = "action" )
	ACTION_PHASE, @Label( standard = "event" )
	EVENT_PHASE, @Label( standard = "render" )
	RENDER_PHASE, @Label( standard = "resource" )
	RESOURCE_PHASE
}
