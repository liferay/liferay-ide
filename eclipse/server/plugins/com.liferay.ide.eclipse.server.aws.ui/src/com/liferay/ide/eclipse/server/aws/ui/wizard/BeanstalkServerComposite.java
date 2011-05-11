/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.aws.ui.wizard;

import com.amazonaws.eclipse.core.AwsToolkitCore;
import com.amazonaws.eclipse.elasticbeanstalk.Region;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.aws.core.AWSCorePlugin;
import com.liferay.ide.eclipse.server.aws.core.BeanstalkServer;
import com.liferay.ide.eclipse.server.aws.core.IBeanstalkServer;
import com.liferay.ide.eclipse.server.aws.core.IBeanstalkServerWorkingCopy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Greg Amerson
 */
public class BeanstalkServerComposite extends Composite
	implements ModifyListener, SelectionListener, PropertyChangeListener {

	protected Button detectButton;
	protected boolean disableValidation;
	protected BeanstalkServerWizardFragment fragment;
	protected boolean ignoreModifyEvents;
	protected IServerWorkingCopy serverWC;
	protected Text textHostname;
	protected Text textSdkname;
	protected Button checkboxCreateSDK;
	protected Button checkboxCreateSCM;

	protected IBeanstalkServerWorkingCopy websphereServerWC;
	protected IWizardHandle wizard;
	private BeanstalkInstanceSelectionTable instanceTable;
	private Combo comboSdkversion;
	private String lastSDKName;
	private String lastRemoteName;
	private String lastHost;

	public BeanstalkServerComposite(Composite parent, BeanstalkServerWizardFragment fragment, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.fragment = fragment;
		this.wizard = wizard;

		createControl();
	}

	public void modifyText(ModifyEvent e) {
		Object src = e.getSource();

		if (src == null || ignoreModifyEvents) {
			return;
		}

		if (src.equals(textHostname)) {
			this.serverWC.setHost(textHostname.getText());
			this.serverWC.setName(this.serverWC.getServerType().getName() + " at " + textHostname.getText());
		}
		else if (src.equals(textSdkname)) {
			this.lastSDKName = textSdkname.getText();
		}

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (IBeanstalkServer.ATTR_HOSTNAME.equals(evt.getPropertyName()) ||
			IBeanstalkServer.ATTR_USERNAME.equals(evt.getPropertyName()) ||
			IBeanstalkServer.ATTR_PASSWORD.equals(evt.getPropertyName())) {

			AWSCorePlugin.updateConnectionSettings((IBeanstalkServer) serverWC.loadAdapter(IBeanstalkServer.class, null));
		}
	}

	public void setServer(IServerWorkingCopy newServer) {
		if (newServer == null) {
			serverWC = null;
			websphereServerWC = null;
		}
		else {
			serverWC = newServer;
			websphereServerWC =
				(IBeanstalkServerWorkingCopy) serverWC.loadAdapter(IBeanstalkServerWorkingCopy.class, null);

			serverWC.addPropertyChangeListener(this);
		}

		disableValidation = true;
		initialize();
		disableValidation = false;
		validate();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		Object src = e.getSource();

		if (src == null) {
			return;
		}



	}

	protected void createControl() {
		setLayout(new GridLayout(2, false));

		disableValidation = true;
		Label instances = new Label(this, SWT.NONE);
		instances.setText("Instances:");
		instances.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		instanceTable = new BeanstalkInstanceSelectionTable(this, false);
		GridData instanceLayoutData = new GridData(GridData.FILL_BOTH);
		instanceTable.setLayoutData(instanceLayoutData);
		instanceLayoutData.heightHint = 100;
		instanceTable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Instance instance = instanceTable.getSelectedInstance();

				if (instance != null) {
					String hostname = instance.getPublicDnsName();

					if (!CoreUtil.isNullOrEmpty(hostname)) {
						textHostname.setText(hostname);
						lastHost = hostname;
						validate();
					}
				}
			}
		});
		// instanceTable.addSelectionListener(this);

		Label environments = new Label(this, SWT.NONE);
		environments.setText("Environments:");
		environments.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		AWSElasticBeanstalk elasticBeanstalkClient =
			AwsToolkitCore.getClientFactory().getElasticBeanstalkClientByEndpoint(Region.DEFAULT.getEndpoint());
		final List<EnvironmentDescription> envs = elasticBeanstalkClient.describeEnvironments().getEnvironments();

		final Combo comboEnvs = new Combo(this, SWT.READ_ONLY);
		List<String> names = new ArrayList<String>();
		for (EnvironmentDescription desc : envs) {
			names.add(desc.getEnvironmentName());
		}
		comboEnvs.setItems(names.toArray(new String[0]));
		comboEnvs.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				textSdkname.setText(envs.get(comboEnvs.getSelectionIndex()).getEnvironmentName() + "-sdk");
				lastRemoteName = envs.get(comboEnvs.getSelectionIndex()).getEnvironmentName();
				validate();
			}
		});

		Label labelHostname = new Label(this, SWT.NONE);
		labelHostname.setText("Hostname:");

		textHostname = new Text(this, SWT.BORDER);
		textHostname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textHostname.addModifyListener(this);

		new Label(this, SWT.NONE).setText("SDK Name");

		textSdkname = new Text(this, SWT.BORDER);
		textSdkname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textSdkname.addModifyListener(this);

		new Label(this, SWT.NONE).setText("SDK Version");

		comboSdkversion = new Combo(this, SWT.READ_ONLY);
		comboSdkversion.setItems(new String[] { "6.0.6", "6.0.5" });
		comboSdkversion.select(0);

		new Label(this, SWT.NONE);

		checkboxCreateSCM = new Button(this, SWT.CHECK);
		checkboxCreateSCM.setText("Create local Git repository");
		checkboxCreateSCM.addSelectionListener(this);

		Composite validateComposite = new Composite(this, SWT.NONE);
		validateComposite.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true));
		validateComposite.setLayout(new GridLayout(1, false));

		// Button validateButton = new Button(validateComposite, SWT.PUSH);
		// validateButton.setText("Validate connection");
		// validateButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
		// validateButton.addSelectionListener(new SelectionAdapter() {
		//
		// public void widgetSelected(SelectionEvent e) {
		// validate();
		// }
		// });
		disableValidation = false;

		refreshData();

		validate();
	}

	protected BeanstalkServer getBeanstalkServer() {
		if (serverWC != null) {
			return (BeanstalkServer) serverWC.loadAdapter(BeanstalkServer.class, null);
		}
		else {
			return null;
		}
	}

	protected void initialize() {
		if (this.serverWC != null && this.websphereServerWC != null) {
			// ignoreModifyEvents = true;
			this.textHostname.setText("");
			// ignoreModifyEvents = false;
		}
	}

	protected void validate() {
		if (disableValidation) {
			return;
		}

		if (serverWC == null) {
			wizard.setMessage("", IMessageProvider.ERROR);
			return;
		}

		try {
			IRunnableWithProgress validateRunnable = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

					final IStatus updateStatus = validateServer(monitor);

					BeanstalkServerComposite.this.getDisplay().syncExec(new Runnable() {

						public void run() {
							if (updateStatus == null || updateStatus.isOK()) {
								wizard.setMessage(null, IMessageProvider.NONE);
							}
							else if (updateStatus.getSeverity() == IStatus.WARNING ||
								updateStatus.getSeverity() == IStatus.ERROR) {
								wizard.setMessage(updateStatus.getMessage(), IMessageProvider.WARNING);
							}

							wizard.update();

						}
					});
				}
			};

			wizard.run(true, true, validateRunnable);
			wizard.update();

		}
		catch (final Exception e) {
			BeanstalkServerComposite.this.getDisplay().syncExec(new Runnable() {

				public void run() {
					wizard.setMessage(e.getMessage(), IMessageProvider.WARNING);
					wizard.update();
				}
			});
		}

	}

	protected IStatus validateServer(IProgressMonitor monitor) {
		String host = serverWC.getHost();

		IStatus status = null;

		if (CoreUtil.isNullOrEmpty(host)) {
			status = AWSCorePlugin.createErrorStatus("Must specify hostname");
		}

		if (status == null) {
			status = websphereServerWC.validate(monitor);
		}


		if (status.getSeverity() == IStatus.ERROR) {
			fragment.lastServerStatus =
				new Status(IStatus.WARNING, status.getPlugin(), status.getMessage(), status.getException());
		}
		else {
			fragment.lastServerStatus = status;
		}

		return status;
	}



	public void refreshData() {
		if (instanceTable != null) {
			instanceTable.refreshInstances();
		}
	}

	public String getSDKName() {
		return this.lastSDKName;
	}

	public String getRemoteName() {
		return this.lastRemoteName;
	}

	public String getHost() {
		return this.lastHost;
	}

}
