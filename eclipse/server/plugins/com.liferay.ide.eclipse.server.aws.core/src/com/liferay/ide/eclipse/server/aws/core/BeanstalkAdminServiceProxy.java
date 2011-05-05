package com.liferay.ide.eclipse.server.aws.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.runtime.IProgressMonitor;

public class BeanstalkAdminServiceProxy implements IBeanstalkAdminService {

	IBeanstalkServer server = null;
	public BeanstalkAdminServiceProxy(IBeanstalkServer server) {
		this.server = server;
	}

	public String getHost() {
		return server.getHost();
	}

	public String getServerState() {
		return "STARTED";
	}

	public boolean isAlive() {
		return true;
	}

	public boolean isAppInstalled(String appName) {
		return false;
	}

	public boolean isAppStarted(String appName) {
		return true;
	}

	public Vector listApplications() {
		return null;
	}

	public void setOptions(Map options) {

	}

	public Object startApplication(File scriptFile, String appName) {
		return null;
	}

	public Object startApplication(String appName) {
		return null;
	}

	public Object stopApplication(String appName) {
		return null;
	}

	public Object uninstallApplication(File scriptFile, String appName, Object monitor) {
		return null;
	}

	public Object updateApplication(String appName, String pathToContents, Object monitor) {

		return null;
	}

	public Map getDebugOptions() {
		return new HashMap();
	}

	public Object installApplication(String env, String absolutePath, String appName, IProgressMonitor submon) {
		try {
			HttpClient client = new HttpClient();
			client.getState().setCredentials(
				new AuthScope(null, 80, null), new UsernamePasswordCredentials("admin", "admin"));

			String undeploy = "http://" + env + ".elasticbeanstalk.com/manager/html/undeploy?path=/" + appName;

			GetMethod undeployGet = new GetMethod(undeploy);
			int status = client.executeMethod(undeployGet);
			if (status != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + undeployGet.getStatusLine());
			}

			File f = new File(absolutePath);

			PostMethod filePost = new PostMethod("http://liferay-test-01.elasticbeanstalk.com/manager/html/upload");
			Part[] parts = { new FilePart("deployWar", f) };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			status = client.executeMethod(filePost);

			if (status != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + filePost.getStatusLine());
			}

			String responseString = filePost.getResponseBodyAsString();
			System.out.println("Response : \n\n" + responseString);
			filePost.releaseConnection();

		}
		catch (Exception e) {
			return e.getMessage();
		}

		return null;
	}

}
