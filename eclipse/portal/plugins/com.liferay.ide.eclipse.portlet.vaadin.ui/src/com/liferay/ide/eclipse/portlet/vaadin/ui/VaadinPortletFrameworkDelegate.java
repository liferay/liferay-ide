package com.liferay.ide.eclipse.portlet.vaadin.ui;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.portlet.vaadin.core.VaadinPortletFrameworkWizardProvider;
import com.liferay.ide.eclipse.portlet.vaadin.ui.wizard.NewVaadinPortletWizard;
import com.liferay.ide.eclipse.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.eclipse.project.ui.wizard.IPluginWizardFragment;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.osgi.framework.Version;


public class VaadinPortletFrameworkDelegate extends AbstractPortletFrameworkDelegate {

	protected IPluginWizardFragment wizardFragment;

	private final static Version v61 = new Version( "6.1" );

	public VaadinPortletFrameworkDelegate() {
		super();
	}

	public Composite createNewProjectOptionsComposite( Composite parent ) {
		return null;
	}

	@Override
	public IPluginWizardFragment getWizardFragment() {
		if ( wizardFragment == null ) {
			wizardFragment = new NewVaadinPortletWizard();
			wizardFragment.setFragment( true );
		}

		return wizardFragment;
	}

	public void updateFragmentEnabled( IDataModel dataModel ) {
		String frameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );
        
		IPortletFrameworkWizardProvider framework = ProjectCorePlugin.getPortletFramework( frameworkId );

		if ( framework instanceof VaadinPortletFrameworkWizardProvider ) {
			String sdkName = dataModel.getStringProperty( IPluginProjectDataModelProperties.LIFERAY_SDK_NAME );

			SDK sdk = SDKManager.getInstance().getSDK( sdkName );

			if ( sdk != null ) {
				Version sdkVersion = new Version( sdk.getVersion() );

				if( CoreUtil.compareVersions( v61, sdkVersion ) > 0 )
				{
					// user has selected sdk 6.0.x so use vaadin portlet wizard
					setFragmentEnabled( true );
				}
				else {
					setFragmentEnabled( false );
				}
			}
		}

	}
}
