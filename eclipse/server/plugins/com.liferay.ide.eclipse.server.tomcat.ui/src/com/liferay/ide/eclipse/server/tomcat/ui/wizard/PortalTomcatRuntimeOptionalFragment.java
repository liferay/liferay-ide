package com.liferay.ide.eclipse.server.tomcat.ui.wizard;

import com.liferay.ide.eclipse.server.tomcat.ui.PortalTomcatUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatRuntimeWizardFragment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;


@SuppressWarnings("restriction")
public class PortalTomcatRuntimeOptionalFragment extends TomcatRuntimeWizardFragment {

	public PortalTomcatRuntimeOptionalFragment() {
		super();
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new PortalTomcatRuntimeOptionalComposite(parent, wizard);

		wizard.setImageDescriptor(ImageDescriptor.createFromURL(PortalTomcatUIPlugin.getDefault().getBundle().getEntry(
			"/icons/wizban/server_wiz.png")));

		return comp;
	}

}
