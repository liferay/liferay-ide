package com.liferay.ide.eclipse.server.aws.core;

import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.eclipse.server.util.JavaUtil;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.wst.server.core.model.RuntimeDelegate;


public class BeanstalkRuntime extends RuntimeDelegate
	implements IBeanstalkRuntime, ILiferayRuntime, IBeanstalkRuntimeWorkingCopy {

	public static final int INVALID_STUB_CODE = 325;

	public static final String PREF_DEFAULT_RUNTIME_LOCATION_PREFIX = "default.runtime.location.";

	public static final String RUNTIME_TYPE_ID = "com.liferay.studio.eclipse.server.ee.Beanstalk.runtime.6";

	protected static final String PROP_LIFERAY_RUNTIME_STUB_LOCATION = "liferay-runtime-stub-location";

	protected static final String PROP_LIFERAY_RUNTIME_STUB_TYPE_ID = "liferay-runtime-stub-type-id";

	protected static final String PROP_VM_INSTALL_ID = "vm-install-id";

	protected static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";

	public BeanstalkRuntime() {
		super();
	}

	public IVMInstall findBundledJRE(boolean addVM) {
		IBeanstalkStub BeanstalkStub = getBeanstalkStub();

		if (BeanstalkStub != null) {
			// IPath bundledJREPath = BeanstalkStub.findBundledJREPath(getRuntime().getLocation());
			IPath bundledJREPath = null;

			if (bundledJREPath == null) {
				return null;
			}

			// make sure we don't have an existing JRE that has the same path
			for (IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes()) {
				for (IVMInstall vmInstall : vmInstallType.getVMInstalls()) {
					if (vmInstall.getInstallLocation().equals(bundledJREPath.toFile())) {
						return vmInstall;
					}
				}
			}

			if (addVM) {
				IVMInstallType installType = JavaRuntime.getVMInstallType(StandardVMType.ID_STANDARD_VM_TYPE);
				VMStandin newVM = new VMStandin(installType, JavaUtil.createUniqueId(installType));
				newVM.setInstallLocation(bundledJREPath.toFile());
				newVM.setName("Beanstalk JDK");

				// make sure the new VM name isn't the same as existing name
				boolean existingVMWithSameName = ServerUtil.isExistingVMName(newVM.getName());

				int num = 1;

				while (existingVMWithSameName) {
					newVM.setName("Beanstalk JDK (" + (num++) + ")");
					existingVMWithSameName = ServerUtil.isExistingVMName(newVM.getName());
				}

				return newVM.convertToRealVM();
			}

		}

		return null;
	}

	public IPath[] getAllUserClasspathLibraries() {
		return LiferayTomcatUtil.getAllUserClasspathLibraries(getRuntimeStubLocation(), getPortalDir());
	}

	public IPath getAppServerDir() {
		return getRuntimeStubLocation();
	}

	public String getAppServerType() {
		return "tomcat";
	}

	public IPath getDeployDir() {
		return getAppServerDir().append("/webapps");
	}

	public IPath getLibGlobalDir() {
		return getAppServerDir().append("/lib/ext");
	}

	public IPath getPortalDir() {
		return LiferayTomcatUtil.getPortalDir(getRuntimeStubLocation());
	}

	public String getPortalVersion() {
		try {
			return LiferayTomcatUtil.getVersion(getRuntimeStubLocation(), getPortalDir());
		}
		catch (IOException e) {
			return "";
		}
	}

	public Properties getPortletCategories() {
		return LiferayTomcatUtil.getCategories(getRuntimeStubLocation(), getPortalDir());
	}

	public IPath getRuntimeLocation() {
		return getRuntimeStubLocation();
	}

	public IPath getRuntimeStubLocation() {
		return new Path(getAttribute(PROP_LIFERAY_RUNTIME_STUB_LOCATION, ""));
	}

	public String getRuntimeStubTypeId() {
		return getAttribute(PROP_LIFERAY_RUNTIME_STUB_TYPE_ID, "");
	}

	public String[] getSupportedHookProperties() {
		try {
			return LiferayTomcatUtil.getSupportedHookProperties(getRuntimeStubLocation(), getPortalDir());
		}
		catch (IOException e) {
			return new String[0];
		}
	}

	public IVMInstall getVMInstall() {
		if (getVMInstallTypeId() == null) {
			return JavaRuntime.getDefaultVMInstall();
		}

		try {
			IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());
			IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
			int size = vmInstalls.length;

			String id = getVMInstallId();

			for (int i = 0; i < size; i++) {
				if (id.equals(vmInstalls[i].getId())) {
					return vmInstalls[i];
				}
			}
		}
		catch (Exception e) {
			// ignore
		}

		return null;
	}

	public IBeanstalkStub getBeanstalkStub() {
		IBeanstalkStub stub = new BeanstalkStub();
		stub.setRuntimeLocation(getRuntime().getLocation());
		return stub;
	}

	public boolean isUsingDefaultJRE() {
		return getVMInstallTypeId() == null;
	}

	public void setDefaults(IProgressMonitor monitor) {
		// IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();
		//
		// getRuntimeWorkingCopy().setLocation(
		// new Path(BeanstalkCore.getPreferences().get(PREF_DEFAULT_RUNTIME_LOCATION_PREFIX + type.getId(), "")));
	}

	public void setRuntimeStubLocation(IPath path) {
		setAttribute(PROP_LIFERAY_RUNTIME_STUB_LOCATION, path.toPortableString());
	}

	public void setRuntimeStubTypeId(String typeId) {
		setAttribute(PROP_LIFERAY_RUNTIME_STUB_TYPE_ID, typeId);
	}

	public void setVMInstall(IVMInstall vmInstall) {
		if (vmInstall == null) {
			setVMInstall(null, null);
		}
		else {
			setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
		}
	}

	@Override
	public IStatus validate() {
		// IStatus status = super.validate();
		//
		// if (!status.isOK()) {
		// return status;
		// }
		IStatus status = Status.OK_STATUS;

		IBeanstalkStub stub = getBeanstalkStub();

		if (stub == null) {
			return AWSCorePlugin.createErrorStatus("No Beanstalk stub defined for runtime type.");
		}

		IVMInstall vmInstall = getVMInstall();

		if (vmInstall == null) {
			return AWSCorePlugin.createErrorStatus("Invalid JDK.  Edit the runtime and change the JDK location.");
		}

		// check the runtime stub
		IStatus runtimeStubStatus = LiferayTomcatUtil.validateRuntimeStubLocation(getRuntimeStubLocation());

		if (/* status.isOK() && */!runtimeStubStatus.isOK()) {
			// code == 1 means bad stub
			status =
				new Status(
					IStatus.ERROR, AWSCorePlugin.PLUGIN_ID, INVALID_STUB_CODE, "Liferay runtime directory is invalid.",
					null);
		}

		return status;
	}

	protected String getVMInstallId() {
		return getAttribute(PROP_VM_INSTALL_ID, (String) null);
	}

	protected String getVMInstallTypeId() {
		return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String) null);
	}

	protected void setVMInstall(String typeId, String id) {
		if (typeId == null) {
			setAttribute(PROP_VM_INSTALL_TYPE_ID, (String) null);
		}
		else {
			setAttribute(PROP_VM_INSTALL_TYPE_ID, typeId);
		}

		if (id == null) {
			setAttribute(PROP_VM_INSTALL_ID, (String) null);
		}
		else {
			setAttribute(PROP_VM_INSTALL_ID, id);
		}
	}

}
