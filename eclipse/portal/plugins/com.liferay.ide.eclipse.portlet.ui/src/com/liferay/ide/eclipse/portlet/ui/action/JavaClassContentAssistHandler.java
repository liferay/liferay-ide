/**
 * 
 */

package com.liferay.ide.eclipse.portlet.ui.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.swt.widgets.Text;

import com.liferay.ide.eclipse.portlet.ui.util.PortletUIHelper;

/**
 * @author kamesh.sampath
 */
public class JavaClassContentAssistHandler extends SapphireActionHandler {

	final int scope = 5;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {

		ISapphirePart sapphirePart = getAction().getPart();

		IProject project = PortletUIHelper.getProject( sapphirePart );

		System.out.println( "JavaClassContentAssistHandler.run() - IProject " + project );

		if ( sapphirePart instanceof SapphirePropertyEditor ) {

			SapphirePropertyEditor propertyEditor = (SapphirePropertyEditor) sapphirePart;

			ModelProperty property = propertyEditor.getProperty();

			Text text = SapphirePropertyEditor.findControlForProperty( context.getComposite(), property, Text.class );
			PortletUIHelper.addTypeFieldAssistToText( propertyEditor, text, project, scope );
		}
		return null;
	}
}
