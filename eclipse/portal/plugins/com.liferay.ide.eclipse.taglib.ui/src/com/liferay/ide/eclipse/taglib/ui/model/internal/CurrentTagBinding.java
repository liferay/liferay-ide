
package com.liferay.ide.eclipse.taglib.ui.model.internal;

import com.liferay.ide.eclipse.taglib.ui.model.ITag;

import org.eclipse.sapphire.modeling.ElementBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Resource;


public class CurrentTagBinding extends ElementBindingImpl {

	@Override
	public Resource read() {
		return null;
	}

	@Override
	public ModelElementType type(Resource resource) {
		return ITag.TYPE;
	}

}
