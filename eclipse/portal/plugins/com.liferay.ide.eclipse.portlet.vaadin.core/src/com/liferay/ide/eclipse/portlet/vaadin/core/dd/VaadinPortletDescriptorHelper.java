
package com.liferay.ide.eclipse.portlet.vaadin.core.dd;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.NodeUtil;
import com.liferay.ide.eclipse.portlet.core.IPluginPackageModel;
import com.liferay.ide.eclipse.portlet.core.PluginPropertiesConfiguration;
import com.liferay.ide.eclipse.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.eclipse.portlet.vaadin.core.VaadinCore;
import com.liferay.ide.eclipse.portlet.vaadin.core.operation.INewVaadinPortletClassDataModelProperties;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Helper for editing various portlet configuration XML files, to add Vaadin portlet configuration to them. Also
 * supports adding a dependency to Vaadin in liferay-plugin-package.properties (if necessary).
 * 
 * @author Henri Sara - borrows/modifies some code from superclass by Greg Amerson
 */
@SuppressWarnings({
	"restriction", "unchecked"
})
public class VaadinPortletDescriptorHelper extends PortletDescriptorHelper
	implements INewVaadinPortletClassDataModelProperties {

	public VaadinPortletDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addNewVaadinPortlet(IDataModel model) {
		IStatus status = addNewPortlet(model);

		if (!status.isOK()) {
			return status;
		}

		return addPortalDependency(IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_JARS, "vaadin.jar");
	}

	public IStatus addPortalDependency(String propertyName, String value) {
		if (CoreUtil.isNullOrEmpty(value)) {
			return Status.OK_STATUS;
		}

		PluginPropertiesConfiguration pluginPackageProperties;

		try {
			IVirtualComponent comp = ComponentCore.createComponent(this.project.getProject());

			if (comp == null) {
				IStatus warning = VaadinCore.createWarningStatus("Could not add Vaadin dependency to the project.");
				VaadinCore.getDefault().getLog().log(warning);
				return Status.OK_STATUS;
			}

			IFolder webroot = (IFolder) comp.getRootFolder().getUnderlyingFolder();
			IFile pluginPackageFile =
				webroot.getFile("WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);

			if (!pluginPackageFile.exists()) {
				IStatus warning =
					VaadinCore.createWarningStatus("No " + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE +
						" file in the project, not adding Vaadin dependency.");

				VaadinCore.getDefault().getLog().log(warning);

				return Status.OK_STATUS;
			}

			File osfile = new File(pluginPackageFile.getLocation().toOSString());

			pluginPackageProperties = new PluginPropertiesConfiguration();
			pluginPackageProperties.load(osfile);

			String existingDeps = pluginPackageProperties.getString(propertyName, "");

			String[] existingValues = existingDeps.split(",");

			for (String existingValue : existingValues) {
				if (value.equals(existingValue)) {
					return Status.OK_STATUS;
				}
			}

			String newPortalDeps = null;

			if (CoreUtil.isNullOrEmpty(existingDeps)) {
				newPortalDeps = value;
			}
			else {
				newPortalDeps = existingDeps + "," + value;
			}

			pluginPackageProperties.setProperty(propertyName, newPortalDeps);

			FileWriter output = new FileWriter(osfile);
			try {
				pluginPackageProperties.save(output);
			}
			finally {
				output.close();
			}

			// refresh file
			pluginPackageFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());

		}
		catch (Exception e) {
			VaadinCore.logError(e);
			return VaadinCore.createErrorStatus("Could not add dependency to vaadin.jar in liferay-plugin-package.properties .");
		}

		return Status.OK_STATUS;
	}

	// some superclass behavior modifications: portlet class as a separate
	// property
	public IStatus updatePortletXML(IDOMDocument document, IDataModel model) {
		// <portlet-app> element
		Element docRoot = document.getDocumentElement();

		// new <portlet> element
		Element newPortletElement = document.createElement("portlet");

		appendChildElement(newPortletElement, "portlet-name", model.getStringProperty(PORTLET_NAME));

		appendChildElement(newPortletElement, "display-name", model.getStringProperty(DISPLAY_NAME));

		appendChildElement(newPortletElement, "portlet-class", model.getStringProperty(PORTLET_CLASS));

		// add <init-param> elements as needed
		List<ParamValue> initParams = (List<ParamValue>) model.getProperty(INIT_PARAMS);

		for (ParamValue initParam : initParams) {
			Element newInitParamElement = appendChildElement(newPortletElement, "init-param");

			appendChildElement(newInitParamElement, "name", initParam.getName());

			appendChildElement(newInitParamElement, "value", initParam.getValue());
		}

		// expiration cache
		appendChildElement(newPortletElement, "expiration-cache", "0");

		// supports node
		Element newSupportsElement = appendChildElement(newPortletElement, "supports");

		appendChildElement(newSupportsElement, "mime-type", "text/html");

		// for all support modes need to add into
		for (String portletMode : ALL_PORTLET_MODES) {
			if (model.getBooleanProperty(portletMode)) {
				appendChildElement(
					newSupportsElement, "portlet-mode",
					model.getPropertyDescriptor(portletMode).getPropertyDescription());
			}
		}

		if (model.getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE)) {
			// need to remove .properties off the end of the bundle_file_path
			String bundlePath = model.getStringProperty(CREATE_RESOURCE_BUNDLE_FILE_PATH);
			String bundleValue = bundlePath.replaceAll("\\.properties$", "");
			appendChildElement(newPortletElement, "resource-bundle", bundleValue);
		}

		// add portlet-info
		Element newPortletInfoElement = appendChildElement(newPortletElement, "portlet-info");

		appendChildElement(newPortletInfoElement, "title", model.getStringProperty(TITLE));

		appendChildElement(newPortletInfoElement, "short-title", model.getStringProperty(TITLE));

		appendChildElement(newPortletInfoElement, "keywords", "");

		// security role refs
		for (String roleName : DEFAULT_SECURITY_ROLE_NAMES) {
			appendChildElement(appendChildElement(newPortletElement, "security-role-ref"), "role-name", roleName);
		}

		// check for event-definition elements

		Node refNode = null;

		String[] refElementNames =
			new String[] {
				"custom-portlet-mode", "custom-window-state", "user-attribute", "security-constraint",
				"resource-bundle", "filter", "filter-mapping", "default-namespace", "event-definition",
				"public-render-parameter", "listener", "container-runtime-option"
			};

		for (int i = 0; i < refElementNames.length; i++) {
			refNode = NodeUtil.findFirstChild(docRoot, refElementNames[i]);

			if (refNode != null) {
				break;
			}
		}

		docRoot.insertBefore(newPortletElement, refNode);

		// append a newline text node
		docRoot.appendChild(document.createTextNode(System.getProperty("line.separator")));

		// format the new node added to the model;
		FormatProcessorXML processor = new FormatProcessorXML();

		processor.formatNode(newPortletElement);

		return Status.OK_STATUS;
	}

}
