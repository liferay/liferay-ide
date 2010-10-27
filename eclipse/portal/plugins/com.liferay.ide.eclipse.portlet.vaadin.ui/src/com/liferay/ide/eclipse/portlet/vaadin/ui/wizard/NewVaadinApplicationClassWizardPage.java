package com.liferay.ide.eclipse.portlet.vaadin.ui.wizard;

import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.portlet.ui.wizard.NewPortletClassWizardPage;
import com.liferay.ide.eclipse.portlet.vaadin.core.operation.INewVaadinPortletClassDataModelProperties;
import com.liferay.ide.eclipse.portlet.vaadin.ui.VaadinUI;
import com.liferay.ide.eclipse.ui.dialog.FilteredTypesSelectionDialogEx;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara - borrows from superclass by Greg Amerson
 */
@SuppressWarnings("restriction")
public class NewVaadinApplicationClassWizardPage extends NewPortletClassWizardPage
	implements INewVaadinPortletClassDataModelProperties {

	// superclass has unused fields that are named similarly, don't depend on
	// them
	protected Button portletClassButton;
	protected Label portletClassLabel;
	protected Combo portletClassCombo;

	public NewVaadinApplicationClassWizardPage(IDataModel model,
			String pageName, String pageDesc, String pageTitle, boolean fragment) {
		super(model, pageName, pageDesc, pageTitle, fragment);
	}

	protected void createApplicationClassnameGroup(Composite parent) {
		// class name
		classLabel = new Label(parent, SWT.LEFT);
		classLabel.setText("Application class:");
		classLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		classText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		classText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		synchHelper.synchText(classText,
				INewJavaClassDataModelProperties.CLASS_NAME, null);

		new Label(parent, SWT.LEFT);
	}

	protected void createCustomPortletClassGroup(Composite parent) {
		createApplicationClassnameGroup(parent);

		createPackageGroup(parent);

		createSuperclassGroup(parent);

		createPortletClassGroup(parent);
	}

	protected void createPortletClassGroup(Composite parent) {
		// portlet class
		portletClassLabel = new Label(parent, SWT.LEFT);
		portletClassLabel.setText("Portlet class:");
		portletClassLabel.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));

		portletClassCombo = new Combo(parent, SWT.DROP_DOWN);
		portletClassCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		synchHelper.synchCombo(portletClassCombo, PORTLET_CLASS, null);

		if (this.fragment) {
			SWTUtil.createLabel(parent, "", 1);
		} else {
			portletClassButton = new Button(parent, SWT.PUSH);
			portletClassButton.setText(J2EEUIMessages.BROWSE_BUTTON_LABEL);
			portletClassButton.setLayoutData(new GridData(
					GridData.HORIZONTAL_ALIGN_FILL));
			portletClassButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					// Do nothing
				}

				public void widgetSelected(SelectionEvent e) {
					// handlePortletClassButtonPressed();
					handlePortletClassButtonSelected(portletClassCombo);
				}
			});
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite composite = SWTUtil.createTopComposite(parent, 3);

		if (!this.fragment) {
			createProjectNameGroup(composite);
			createFolderGroup(composite);
			addSeperator(composite, 3);
		}

		createCustomPortletClassGroup(composite);

		// handleCustomButtonSelected();

		classText.setFocus();

		setShellImage();

		return composite;
	}

	protected String[] getValidationPropertyNames() {
		List<String> validationPropertyNames = new ArrayList<String>();

		if (this.fragment) {
			return new String[] {
					IArtifactEditOperationDataModelProperties.COMPONENT_NAME,
					INewJavaClassDataModelProperties.JAVA_PACKAGE,
					INewJavaClassDataModelProperties.CLASS_NAME,
					INewJavaClassDataModelProperties.SUPERCLASS, PORTLET_CLASS };
		} else {
			Collections.addAll(validationPropertyNames,
					super.getValidationPropertyNames());
		}

		return validationPropertyNames.toArray(new String[0]);
	}

	// superclass selection - different base class than in new portlet wizard
	protected void handleClassButtonSelected(Control control) {
		handleClassButtonSelected(control, QUALIFIED_VAADIN_APPLICATION,
				J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_TITLE,
				J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_DESC);
	}

	protected void handlePortletClassButtonSelected(Control control) {
		handleClassButtonSelected(control, "javax.portlet.GenericPortlet",
				"Portlet Class Selection", "Choose a portlet class:");
	}

	// generalized version of the superclass method for class selection
	protected void handleClassButtonSelected(Control control, String baseClass,
			String title, String message) {
		getControl().setCursor(
				new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT));
		try {

			IPackageFragmentRoot packRoot = (IPackageFragmentRoot) model
					.getProperty(INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT);

			if (packRoot == null) {
				return;
			}

			// this eliminates the non-exported classpath entries
			// final IJavaSearchScope scope =
			// TypeSearchEngine.createJavaSearchScopeForAProject(packRoot.getJavaProject(),
			// true, true);

			IJavaSearchScope scope = null;

			try {
				IType type = packRoot.getJavaProject().findType(baseClass);
				if (type == null) {
					// not in project
					return;
				}
				scope = BasicSearchEngine.createHierarchyScope(type);
			} catch (JavaModelException e) {
				PortletUIPlugin.logError(e);
				return;
			}

			// This includes all entries on the classpath. This behavior is
			// identical
			// to the Super Class Browse Button on the Create new Java Class
			// Wizard
			// final IJavaSearchScope scope =
			// SearchEngine.createJavaSearchScope(new
			// IJavaElement[] {root.getJavaProject()} );
			FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialogEx(
					getShell(), false, getWizard().getContainer(), scope,
					IJavaSearchConstants.CLASS);
			dialog.setTitle(title);
			dialog.setMessage(message);

			if (dialog.open() == Window.OK) {
				IType type = (IType) dialog.getFirstResult();

				String classFullPath = J2EEUIMessages.EMPTY_STRING;

				if (type != null) {
					classFullPath = type.getFullyQualifiedName();
				}

				if (control instanceof Text) {
					((Text) control).setText(classFullPath);
				} else if (control instanceof Combo) {
					((Combo) control).setText(classFullPath);
				}

				return;
			}
		} finally {
			getControl().setCursor(null);
		}
	}

	protected void setShellImage() {
		URL url = VaadinUI.getDefault().getBundle().getEntry("/icons/e16/vaadinportlet.png");

		Image shellImage = ImageDescriptor.createFromURL(url).createImage();

		getShell().setImage(shellImage);
	}

}
