package com.liferay.ide.eclipse.server.tomcat.ui.wizard;

import com.liferay.ide.eclipse.server.tomcat.ui.LiferayTomcatUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatRuntimeWizardFragment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;


@SuppressWarnings("restriction")
public class LiferayTomcatRuntimeOptionalFragment extends TomcatRuntimeWizardFragment {

	public LiferayTomcatRuntimeOptionalFragment() {
		super();
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new LiferayTomcatRuntimeOptionalComposite(parent, wizard);

		wizard.setImageDescriptor(ImageDescriptor.createFromURL(LiferayTomcatUIPlugin.getDefault().getBundle().getEntry(
			"/icons/wizban/server_wiz.png")));

		return comp;
	}

}
