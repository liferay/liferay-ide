
package com.liferay.ide.eclipse.portlet.vaadin.core.operation;

import com.liferay.ide.eclipse.portlet.core.operation.INewPortletClassDataModelProperties;

import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara
 */
@SuppressWarnings("restriction")
public class NewVaadinApplicationClassOperation extends NewWebClassOperation
	implements INewPortletClassDataModelProperties {

	protected static final String TEMPLATE_DIR = "/templates/"; //$NON-NLS-1$

	protected static final String TEMPLATE_FILE = TEMPLATE_DIR + "application.javajet"; //$NON-NLS-1$

	public NewVaadinApplicationClassOperation(IDataModel dataModel) {
		super(dataModel);
	}

	@Override
	protected CreateWebClassTemplateModel createTemplateModel() {
		return new CreateVaadinPortletTemplateModel(getDataModel());
	}

	@Override
	protected String getTemplateFile() {
		return TEMPLATE_FILE;
	}

	@Override
	protected Object getTemplateImplementation() {
		return VaadinPortletTemplate.create(null);
	}

}
