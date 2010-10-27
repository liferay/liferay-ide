package com.liferay.ide.eclipse.portlet.vaadin.ui.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.eclipse.portlet.ui.wizard.NewLiferayPortletWizardPage;
import com.liferay.ide.eclipse.portlet.ui.wizard.NewPortletOptionsWizardPage;
import com.liferay.ide.eclipse.portlet.ui.wizard.NewPortletWizard;
import com.liferay.ide.eclipse.portlet.vaadin.core.operation.NewVaadinPortletClassDataModelProvider;

/**
 * @author Henri Sara
 */
@SuppressWarnings("restriction")
public class NewVaadinPortletWizard extends NewPortletWizard {

	public NewVaadinPortletWizard() {
		this(null);
	}

	public NewVaadinPortletWizard(IDataModel model) {
		super(model);
	}

	// @Override
	// public String getFragmentPluginFacetId() {
	// return
	// IVaadinPortletPluginFacetConstants.LIFERAY_VAADIN_PORTLET_PLUGIN_FACET_ID;
	// }

	@Override
	public String getTitle() {
		return "New Liferay Vaadin Portlet";
	}

	@Override
	protected void doAddPages() {
		addPage(new NewVaadinApplicationClassWizardPage(getDataModel(),
				"pageOne", "Create a portlet application class.",
				getDefaultPageTitle(), fragment));
		addPage(new NewPortletOptionsWizardPage(getDataModel(), "pageTwo",
				"Specify portlet deployment descriptor details.",
				getDefaultPageTitle(), fragment));
		addPage(new NewLiferayPortletWizardPage(getDataModel(), "pageThree",
				"Specify Liferay portlet deployment descriptor details.",
				getDefaultPageTitle(), fragment));
	}

	@Override
	protected String getDefaultPageTitle() {
		return "Create Liferay Vaadin Portlet";
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		// for now, no need for own template store and context type
		TemplateStore templateStore = PortletUIPlugin.getDefault()
				.getTemplateStore();

		TemplateContextType contextType = PortletUIPlugin.getDefault()
				.getTemplateContextRegistry()
				.getContextType(PortletTemplateContextTypeIds.NEW);

		return new NewVaadinPortletClassDataModelProvider(templateStore,
				contextType, fragment);
	}

	@Override
	protected ImageDescriptor getImage() {
		return PortletUIPlugin.imageDescriptorFromPlugin(
				PortletUIPlugin.PLUGIN_ID, "/icons/vaadin-icon-32.png");
	}

}
