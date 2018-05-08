/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.maven.core.MavenUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.NewLiferayComponentOpMethods;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.net.URL;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class MavenModuleProjectTests extends AbstractMavenProjectTestCase {

	@Test
	public void testMavenDependencyUpdate() throws Exception {
		String[][] dependency = {{"com.liferay.portal", "com.liferay.portal.kernel", "2.6.0"}};

		Dependency mavenDependency = new Dependency();

		mavenDependency.setGroupId(dependency[0][0]);
		mavenDependency.setArtifactId(dependency[0][1]);
		mavenDependency.setVersion(dependency[0][2]);

		URL wsZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/MavenDependencyTestProject.zip");

		File wsZipFile = new File(FileLocator.toFileURL(wsZipUrl).getFile());

		File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRootLocation().toFile();

		ZipUtil.unzip(wsZipFile, eclipseWorkspaceLocation);

		File mavenDependencyTestProjectFolder = new File(eclipseWorkspaceLocation, "MavenDependencyTestProject");

		MavenUtil.importProject(mavenDependencyTestProjectFolder.getAbsolutePath(), monitor);

		waitForJobsToComplete(monitor);

		IProject mavenDependencyTestProject = CoreUtil.getProject("MavenDependencyTestProject");

		assertNotNull(mavenDependencyTestProject);

		assertTrue(mavenDependencyTestProject.exists());

		IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(
			mavenDependencyTestProject, new NullProgressMonitor());

		assertNotNull(projectFacade);

		MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());

		List<Dependency> existedDependencies = mavenProject.getDependencies();

		assertFalse(_checkDependency(existedDependencies, mavenDependency));

		ILiferayProject liferayMavenDependencyProject = LiferayCore.create(mavenDependencyTestProject);

		IProjectBuilder projectBuilder = liferayMavenDependencyProject.adapt(IProjectBuilder.class);

		projectBuilder.updateProjectDependency(mavenDependencyTestProject, Arrays.asList(dependency));

		waitForJobsToComplete(monitor);

		MavenProject updateMavenProject = projectFacade.getMavenProject(new NullProgressMonitor());

		List<Dependency> updateDependencies = updateMavenProject.getDependencies();

		assertTrue(_checkDependency(updateDependencies, mavenDependency));
	}

	@Test
	public void testNewLiferayComponentBndAndMavenForPortleActionCommandAndRest() throws Exception {
		NewLiferayModuleProjectOp pop = NewLiferayModuleProjectOp.TYPE.instantiate();

		pop.setProjectName("testMavenModuleComponentBnd");
		pop.setProjectTemplateName("portlet");
		pop.setProjectProvider("maven-module");

		Status modulePorjectStatus = NewLiferayModuleProjectOpMethods.execute(
			pop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		assertTrue(modulePorjectStatus.ok());

		IProject modPorject = CoreUtil.getProject(pop.getProjectName().content());

		modPorject.open(new NullProgressMonitor());

		NewLiferayComponentOp cop = NewLiferayComponentOp.TYPE.instantiate();

		cop.setProjectName(pop.getProjectName().content());
		cop.setComponentClassTemplateName("PortletActionCommand");

		NewLiferayComponentOpMethods.execute(cop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		IFile bgd = modPorject.getFile("bnd.bnd");

		String bndcontent = FileUtil.readContents(bgd.getLocation().toFile(), true);

		String bndConfig =
			"-includeresource: \\" + System.getProperty("line.separator") + "\t" +
				"@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class,\\" +
					System.getProperty("line.separator") + "\t@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld" +
						System.getProperty("line.separator");

		assertTrue(bndcontent.contains(bndConfig));

		IFile pomFile = modPorject.getFile(IMavenConstants.POM_FILE_NAME);

		IMaven maven = MavenPlugin.getMaven();

		Model model = maven.readModel(pomFile.getLocation().toFile());

		List<Dependency> dependencies = model.getDependencies();

		boolean hasDependency = false;

		for (Dependency de : dependencies) {
			String managementKey = de.getManagementKey();

			if (managementKey.equals("com.liferay.portal:com.liferay.util.bridges:jar")) {
				hasDependency = true;

				break;
			}
		}

		assertTrue(hasDependency);

		NewLiferayComponentOp copRest = NewLiferayComponentOp.TYPE.instantiate();

		copRest.setProjectName(pop.getProjectName().content());
		copRest.setComponentClassTemplateName("RestService");

		NewLiferayComponentOpMethods.execute(copRest, ProgressMonitorBridge.create(new NullProgressMonitor()));

		bgd = modPorject.getFile("bnd.bnd");

		bndcontent = FileUtil.readContents(bgd.getLocation().toFile(), true);

		assertTrue(bndcontent.contains(bndConfig));

		String restConfig = "Require-Capability: osgi.contract; filter:=\"(&(osgi.contract=JavaJAXRS)(version=2))\"";

		assertTrue(bndcontent.contains(restConfig));

		model = maven.readModel(pomFile.getLocation().toFile());

		dependencies = model.getDependencies();

		hasDependency = false;

		for (Dependency de : dependencies) {
			String managementKey = de.getManagementKey();

			if (managementKey.equals("javax.ws.rs:javax.ws.rs-api:jar")) {
				hasDependency = true;

				break;
			}
		}

		assertTrue(hasDependency);

		NewLiferayComponentOp copAuth = NewLiferayComponentOp.TYPE.instantiate();

		copAuth.setProjectName(pop.getProjectName().content());
		copAuth.setComponentClassTemplateName("Authenticator");

		NewLiferayComponentOpMethods.execute(copAuth, ProgressMonitorBridge.create(new NullProgressMonitor()));

		bgd = modPorject.getFile("bnd.bnd");

		bndcontent = FileUtil.readContents(bgd.getLocation().toFile(), true);

		bndConfig =
			"-includeresource: \\" + System.getProperty("line.separator") + "\t" +
				"@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class,\\" +
					System.getProperty("line.separator") + "\t@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld,\\" +
						System.getProperty("line.separator") + "\t@shiro-core-1.1.0.jar";

		assertTrue(bndcontent.contains(bndConfig));

		model = maven.readModel(pomFile.getLocation().toFile());

		dependencies = model.getDependencies();

		hasDependency = false;

		for (Dependency de : dependencies) {
			String managementKey = de.getManagementKey();

			if (managementKey.equals("org.apache.shiro:shiro-core:jar")) {
				hasDependency = true;

				break;
			}
		}

		assertTrue(hasDependency);
	}

	@Test
	public void testNewLiferayMavenModuleMVCPortletProject() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("foo");
		op.setProjectProvider("maven-module");
		op.setComponentName("Foo");

		IProject project = _create(op);

		assertTrue(project.getFile("src/main/java/foo/portlet/FooPortlet.java").exists());

		_verifyProject(project);
	}

	@Test
	public void testNewLiferayMavenModuleMVCPortletProjectCustomPackage() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("foo-bar");
		op.setProjectProvider("maven-module");
		op.setComponentName("FooBar");
		op.setPackageName("my.custom.pname");

		IProject project = _create(op);

		assertTrue(project.getFile("src/main/java/my/custom/pname/portlet/FooBarPortlet.java").exists());

		_verifyProject(project);
	}

	@Test
	public void testNewLiferayMavenModuleMVCPortletProjectWithDashes() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("foo-bar");
		op.setProjectProvider("maven-module");
		op.setComponentName("FooBar");
		op.setPackageName("foo.bar");

		IProject project = _create(op);

		assertTrue(project.getFile("src/main/java/foo/bar/portlet/FooBarPortlet.java").exists());

		_verifyProject(project);
	}

	@Test
	public void testNewLiferayMavenModuleMVCPortletProjectWithDots() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("foo.bar");
		op.setProjectProvider("maven-module");

		IProject project = _create(op);

		_verifyProject(project);
	}

	@Test
	public void testNewLiferayModuleProjectDefaultLocation() throws Exception {
		URL wsZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/gradle-liferay-workspace.zip");

		File wsZipFile = new File(FileLocator.toFileURL(wsZipUrl).getFile());

		File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRootLocation().toFile();

		ZipUtil.unzip(wsZipFile, eclipseWorkspaceLocation);

		File wsFolder = new File(eclipseWorkspaceLocation, "gradle-liferay-workspace");

		_importExistingProject(wsFolder, new NullProgressMonitor());

		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("my-test-project");

		op.setProjectTemplateName("mvc-portlet");

		op.setProjectProvider("maven-module");

		// don't put maven type project inside liferay-workspace

		Value<org.eclipse.sapphire.modeling.Path> pathValue = op.getLocation();

		org.eclipse.sapphire.modeling.Path path = pathValue.content();

		assertTrue(path.toFile().equals(eclipseWorkspaceLocation));

		op.setProjectProvider("gradle-module");

		op.setProjectTemplateName("theme");

		// put gradle type theme project inside liferay-workspace/wars

		pathValue = op.getLocation();

		path = pathValue.content();

		assertTrue(path.toPortableString().contains("gradle-liferay-workspace/wars"));

		op.setProjectTemplateName("mvc-portlet");

		pathValue = op.getLocation();

		path = pathValue.content();

		// put gradle type project inside liferay-workspace/modules

		assertTrue(path.toPortableString().contains("gradle-liferay-workspace/modules"));

		IProject project = CoreUtil.getProject("gradle-liferay-workspace");

		if ((project != null) && project.isAccessible() && project.exists()) {
			project.delete(true, true, new NullProgressMonitor());
		}

		op.setProjectTemplateName("service-builder");

		// no liferay-workspace

		pathValue = op.getLocation();

		path = pathValue.content();

		assertTrue(path.toFile().equals(eclipseWorkspaceLocation));
	}

	@Test
	public void testNewLiferayModuleProjectNewProperties() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("test-properties-in-portlet");

		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("portlet");
		op.setComponentName("Test");

		PropertyKey pk = op.getPropertyKeys().insert();

		pk.setName("property-test-key");
		pk.setValue("property-test-value");

		Status exStatus = NewLiferayModuleProjectOpMethods.execute(op, ProgressMonitorBridge.create(monitor));

		assertEquals("OK", exStatus.message());

		IProject modProject = CoreUtil.getProject(op.getProjectName().content());

		modProject.open(new NullProgressMonitor());

		SearchFilesVisitor sv = new SearchFilesVisitor();

		List<IFile> searchFiles = sv.searchFiles(modProject, "TestPortlet.java");

		IFile componentClassFile = searchFiles.get(0);

		assertEquals(componentClassFile.exists(), true);

		String actual = CoreUtil.readStreamToString(componentClassFile.getContents());

		assertTrue(actual, actual.contains("\"property-test-key=property-test-value\""));
	}

	@Test
	public void testNewLiferayModuleProjectNoGradleFiles() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("test-servicebuilder-no-gradlefiles");
		op.setProjectTemplateName("service-builder");
		op.setProjectProvider("maven-module");

		Status exStatus = NewLiferayModuleProjectOpMethods.execute(
			op, ProgressMonitorBridge.create(new NullProgressMonitor()));

		assertEquals("OK", exStatus.message());

		IProject parentProject = CoreUtil.getProject(op.getProjectName().content());

		parentProject.open(new NullProgressMonitor());

		IFile gradleFile = parentProject.getFile("build.gradle");
		IFile settingsFile = parentProject.getFile("settings.gradle");

		assertFalse(gradleFile.exists());
		assertFalse(settingsFile.exists());

		IProject apiProject = CoreUtil.getProject(op.getProjectName().content() + "-api");

		apiProject.open(new NullProgressMonitor());

		gradleFile = apiProject.getFile("build.gradle");
		settingsFile = apiProject.getFile("settings.gradle");

		assertFalse(gradleFile.exists());
		assertFalse(settingsFile.exists());

		IProject serviceProject = CoreUtil.getProject(op.getProjectName().content() + "-service");

		serviceProject.open(new NullProgressMonitor());

		gradleFile = serviceProject.getFile("build.gradle");
		settingsFile = serviceProject.getFile("settings.gradle");

		assertFalse(gradleFile.exists());
		assertFalse(settingsFile.exists());
	}

	@Test
	public void testProjectTemplateActivator() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("activator-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("activator");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateApi() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("api-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("api");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateApiWithInvalidPackageName() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("api-test-1");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("api");

		Status status = op.validation();

		assertFalse(status.ok());

		assertTrue(status.message().contains("not a valid Java identifier"));

		op.setPackageName("api.test.one");

		assertTrue(op.validation().message(), op.validation().ok());

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateContentTargetingReport() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("content-targeting-report-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("content-targeting-report");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateContentTargetingRule() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("content-targeting-rule-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("content-targeting-rule");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateContentTargetingTrackingAction() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("content-targeting-tracking-action-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("content-targeting-tracking-action");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateControlMenuEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("control-menu-entry-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("control-menu-entry");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateFormField() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("form-field-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("form-field");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateLayoutTemplate() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("layout-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("layout-template");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		assertTrue(project.getFile("target/layout-test-1.0.0.war").exists());
	}

	@Test
	public void testProjectTemplateMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("mvc-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("mvc-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmAngularPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-angular-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-angular-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateNpmAngularPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-angular-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-angular-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Ignore("Re-enable NPM tests")
	@Test
	public void testProjectTemplateNpmBillboardjsPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-billboardjs-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-billboardjs-portlet");

		_createAndBuild(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmBillboardjsPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-billboardjs-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-billboardjs-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmIsomorphicPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-isomorphic-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-isomorphic-portlet");

		_createAndBuild(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmIsomorphicPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-isomorphic-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-isomorphic-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmJqueryPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-jquery-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-jquery-portlet");

		_createAndBuild(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmJqueryPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-jquery-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-jquery-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmMetaljsPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-metaljs-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-metaljs-portlet");

		_createAndBuild(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmMetaljsPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-metaljs-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-metaljs-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-portlet");

		_createAndBuild(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmReactPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-react-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-react-portlet");

		_createAndBuild(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmReactPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-react-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-react-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmVuejsPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-vuejs-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-vuejs-portlet");

		_createAndBuild(op);
	}

	@Ignore("Re-enable NPM Tests")
	@Test
	public void testProjectTemplateNpmVuejsPortlet71() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("npm-vuejs-portlet-test-v71");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("npm-vuejs-portlet");
		op.setLiferayVersion("7.1");

		_createAndBuild(op);
		_verifyNpmPortletV71(op);
	}

	@Test
	public void testProjectTemplatePanelApp() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("panel-app-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("panel-app");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortletConfigurationIcon() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-configuration-icon-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("portlet-configuration-icon");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortletProvider() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-provider-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("portlet-provider");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplatePortletToolbarContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("portlet-toolbar-contributor-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("portlet-toolbar-contributor");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateRest() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("rest-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("rest");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateService() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("service-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("service");
		op.setServiceName("com.liferay.portal.kernel.events.LifecycleAction");

		IProject project = _create(op);

		IFile serviceFile = project.getFile("src/main/java/service/test/ServiceTest.java");

		assertTrue(serviceFile.exists());

		String contents =
			"package service.test;\nimport com.liferay.portal.kernel.events.ActionException;\n" +
				"import com.liferay.portal.kernel.events.LifecycleAction;\n" +
					"import com.liferay.portal.kernel.events.LifecycleEvent;\n" +
						"import org.osgi.service.component.annotations.Component;\n@Component(\n" +
							"immediate = true, property = {\"key=login.events.pre\"},\n" +
								"service = LifecycleAction.class\n)\n" +
									"public class ServiceTest implements LifecycleAction {\n" +
										"@Override public void processLifecycleEvent" +
											"(LifecycleEvent lifecycleEvent) throws ActionException { }\n}";

		serviceFile.setContents(new ByteArrayInputStream(contents.getBytes()), IResource.FORCE, monitor);

		_verifyProject(project);
	}

	@Test
	public void testProjectTemplateServiceBuilder() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("service-builder-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("service-builder");
		op.setPackageName("com.liferay.test");

		IProject parent = _create(op);

		assertTrue(parent != null && parent.exists());

		IProject api = CoreUtil.getProject("service-builder-test-api");

		assertTrue(api != null && api.exists());

		IProject service = CoreUtil.getProject("service-builder-test-service");

		assertTrue(service != null && service.exists());

		IProjectBuilder builder = LiferayCore.create(IProjectBuilder.class, service);

		builder.buildService(monitor);

		api.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		service.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		IBundleProject apiBundle = LiferayCore.create(IBundleProject.class, api);

		assertNotNull(apiBundle);

		IPath apiOutput = apiBundle.getOutputBundle(true, monitor);

		assertNotNull(apiOutput);

		assertTrue(apiOutput.toFile().exists());

		assertEquals("service-builder-test-api-1.0.0.jar", apiOutput.lastSegment());

		IBundleProject serviceBundle = LiferayCore.create(IBundleProject.class, service);

		IPath serviceOutput = serviceBundle.getOutputBundle(true, monitor);

		assertNotNull(serviceOutput);

		assertTrue(serviceOutput.toFile().exists());

		assertEquals("service-builder-test-service-1.0.0.jar", serviceOutput.lastSegment());
	}

	@Test
	public void testProjectTemplateServiceWrapper() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("service-wrapper-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("service-wrapper");
		op.setServiceName("com.liferay.portal.kernel.service.UserLocalServiceWrapper");
		op.setComponentName("MyServiceWrapper");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateSimulationPanelEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("simulation-panel-entry-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("simulation-panel-entry");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateSoyPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("soy-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("soy-portlet");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateSpringMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("spring-mvc-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("spring-mvc-portlet");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		assertTrue(project.getFile("target/spring-mvc-portlet-test-1.0.0.war").exists());
	}

	@Test
	public void testProjectTemplateTemplateContextContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("template-context-contributor-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("template-context-contributor");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateTheme() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("theme-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("theme");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateThemeContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("theme-contributor-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("theme-contributor");

		_createAndBuild(op);
	}

	@Test
	public void testProjectTemplateWarHook() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("war-hook-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("war-hook");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		assertTrue(project.getFile("target/war-hook-test-1.0.0.war").exists());
	}

	@Test
	public void testProjectTemplateWarMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("war-mvc-portlet-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("war-mvc-portlet");

		IProject project = _createAndBuild(op);

		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		assertTrue(project.getFile("target/war-mvc-portlet-test-1.0.0.war").exists());
	}

	@Test
	public void testThemeProjectComponentConfiguration() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("maven-theme-component-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("theme");

		op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		IProject project = CoreUtil.getProject("maven-theme-component-test");

		assertNotNull(project);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		assertNotNull(bundleProject);
	}

	@Test
	public void testThemeProjectPluginDetection() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("maven-theme-test");
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("theme");

		op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		IProject project = CoreUtil.getProject("maven-theme-test");

		assertNotNull(project);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		assertNotNull(bundleProject);
	}

	private static void _importExistingProject(File dir, IProgressMonitor monitor) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IProjectDescription description = workspace.loadProjectDescription(
			new Path(dir.getAbsolutePath()).append(".project"));

		String name = description.getName();

		IProject project = workspace.getRoot().getProject(name);

		if (project.exists()) {
			return;
		}
		else {
			project.create(description, monitor);
			project.open(IResource.BACKGROUND_REFRESH, monitor);

			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

	private boolean _checkDependency(List<Dependency> existedDependencies, Dependency mavenDependency) {
		for (Dependency existedDependency : existedDependencies) {
			String existedKey = existedDependency.getManagementKey();

			if (existedKey.equals(mavenDependency.getManagementKey())) {
				return true;
			}
		}

		return false;
	}

	private IProject _create(NewLiferayModuleProjectOp op) throws CoreException, InterruptedException {
		Status status = op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		assertNotNull(status);
		assertTrue(status.message(), status.ok());

		waitForJobsToComplete();

		return CoreUtil.getProject(op.getProjectName().content());
	}

	private IProject _createAndBuild(NewLiferayModuleProjectOp op) throws Exception {
		assertTrue(op.validation().message(), op.validation().ok());

		IProject project = _create(op);

		_verifyProject(project);

		return project;
	}

	private void _verifyNpmPortletV71(NewLiferayModuleProjectOp op) {
		IProject project = CoreUtil.getProject(op.getProjectName().content());

		assertNotNull(project);

		IFile viewFile = project.getFile("src/main/resources/META-INF/resources/view.jsp");

		assertTrue(viewFile.exists());

		String viewFileContent = FileUtil.readContents(viewFile.getLocation().toFile());

		assertTrue(viewFileContent.contains("<aui:script require=\"<%= bootstrapRequire %>\">"));
	}

	private void _verifyProject(IProject project) throws Exception {
		assertNotNull(project);
		assertTrue(project.exists());

		assertFalse(project.getFile("build.gradle").exists());

		project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);

		waitForJobsToComplete();

		project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		waitForJobsToComplete();

		assertNoErrors(project);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		assertNotNull(bundleProject);

		IPath outputBundle = bundleProject.getOutputBundle(true, monitor);

		assertNotNull(outputBundle);

		assertTrue(outputBundle.toFile().exists());
	}

}