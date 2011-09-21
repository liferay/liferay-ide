
package com.liferay.ide.eclipse.portlet.ui.editor.internal;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;

public class ExpandSectionOutlineNodeHandler extends SapphireActionHandler {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {
		final MasterDetailsContentNode mContentNode = getPart().nearest( MasterDetailsContentNode.class );
		if ( mContentNode.isExpanded() ) {
			mContentNode.setExpanded( false );
		}
		else {
			mContentNode.setExpanded( true );
		}
		return null;
	}

	protected boolean computeEnabledState() {
		return true;
	}

}
