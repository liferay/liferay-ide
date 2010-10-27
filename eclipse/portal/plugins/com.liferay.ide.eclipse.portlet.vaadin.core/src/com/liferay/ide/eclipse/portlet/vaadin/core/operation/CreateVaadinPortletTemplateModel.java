package com.liferay.ide.eclipse.portlet.vaadin.core.operation;

import java.util.Collection;

import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara
 */
@SuppressWarnings("restriction")
public class CreateVaadinPortletTemplateModel extends CreateWebClassTemplateModel {

	protected boolean generateGenericInclude = false;

	public CreateVaadinPortletTemplateModel(IDataModel dataModel) {
		super(dataModel);
	}

	public String getClassName() {
		return dataModel.getStringProperty(INewJavaClassDataModelProperties.CLASS_NAME);
	}

	@Override
	public Collection<String> getImports() {
		Collection<String> collection = super.getImports();

		collection.add("com.vaadin.Application");
		collection.add("com.vaadin.ui.Label");
		collection.add("com.vaadin.ui.Window");

		return collection;
	}

}
