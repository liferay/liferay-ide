
package com.liferay.ide.eclipse.hook.ui.actions.internal;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;

import com.liferay.ide.eclipse.sdk.ISDKConstants;

public class FileResourceCondtion extends SapphirePropertyEditorCondition {

	public FileResourceCondtion() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireCondition#initCondition(org.eclipse.sapphire.ui.ISapphirePart,
	 * java.lang.String)
	 */
	@Override
	protected void initCondition( ISapphirePart part, String parameter ) {
		// TODO Auto-generated method stub
		super.initCondition( part, parameter );
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.sapphire.ui.SapphirePropertyEditorCondition#evaluate(org.eclipse.sapphire.ui.SapphirePropertyEditor)
	 */
	@Override
	protected boolean evaluate( SapphirePropertyEditor part ) {
		final IModelElement element = part.getModelElement();
		final ModelProperty property = part.getProperty();
		if ( property instanceof ValueProperty && Path.class.isAssignableFrom( property.getTypeClass() ) ) {
			final Value<Path> path = element.read( (ValueProperty) property );
			if ( path != null && path != null && path.getText().length() > 0 ) {
				IProject project = element.adapt( IProject.class );
				File file =
					project.getLocation().append( ISDKConstants.DEFAULT_WEBCONTENT_FOLDER ).append( path.getText() ).toFile();
				return !file.exists();
			}
		}
		return false;
	}
}
