package com.liferay.ide.sdk.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ivyde.eclipse.cp.AdvancedSetup;
import org.apache.ivyde.eclipse.cp.ClasspathSetup;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainerConfiguration;
import org.apache.ivyde.eclipse.cp.MappingSetup;
import org.apache.ivyde.eclipse.cp.RetrieveSetup;
import org.apache.ivyde.eclipse.cp.SettingsSetup;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;


public class IvyUtil
{
    private static final String UTF8_ERROR = "The UTF-8 encoding support is required" //$NON-NLS-1$
                    + " is decode the path of the container."; //$NON-NLS-1$

    public static IPath getPath(IvyClasspathContainerConfiguration conf) {
        StringBuffer path = new StringBuffer();
        path.append('?');
        IJavaProject javaProject = conf.getJavaProject();
        try {
            /*
             * Implementation note about why the project is serialized in the path. This is related
             * to https://issues.apache.org/jira/browse/IVYDE-237
             *
             * For some reason, when we add a project to the source path of a launch configuration,
             * any IvyDE container involved of that project lose its reference to its project. Then
             * when the JDT call the IvyDERuntimeClasspathEntryResolver to resolve the source of
             * that container, the IRuntimeClasspathEntry doesn't reference a Java project. In most
             * case, an IvyDE classpath container reference an ivy.xml relatively to the project. So
             * in that context, the classpath cannot be resolved without a reference to the project
             * in the path of the container.
             *
             * Another reason for having the project in the path of the container, is to make the
             * path unique. Again the source path in a launch configuration would consider two
             * containers with exactly the configurations the same, even if the
             * IRuntimeClasspathEntry reference different projects.
             *
             * To reproduce the issue, some test project is available and configured accordingly.
             * See in the test folder of the IvyDE project, check out the project 'jetty' and
             * 'jetty-webapp'.
             */
            path.append("project="); //$NON-NLS-1$
            if (javaProject != null) {
                path.append(URLEncoder.encode(javaProject.getElementName(), "UTF-8")); //$NON-NLS-1$
            }
            path.append("&ivyXmlPath="); //$NON-NLS-1$
            path.append(URLEncoder.encode(conf.getIvyXmlPath(), "UTF-8")); //$NON-NLS-1$
            append(path, "confs", conf.getConfs()); //$NON-NLS-1$
            if (conf.isSettingsProjectSpecific()) {
                SettingsSetup setup = conf.getIvySettingsSetup();
                append(path, "ivySettingsPath", setup.getRawIvySettingsPath()); //$NON-NLS-1$
                append(path, "loadSettingsOnDemand", setup.isLoadSettingsOnDemand()); //$NON-NLS-1$
                append(path, "ivyUserDir", setup.getRawIvyUserDir()); //$NON-NLS-1$
                append(path, "propertyFiles", setup.getRawPropertyFiles()); //$NON-NLS-1$
            }
            if (conf.isClassthProjectSpecific()) {
                ClasspathSetup setup = conf.getClasspathSetup();
                append(path, "acceptedTypes", setup.getAcceptedTypes()); //$NON-NLS-1$
                append(path, "alphaOrder", setup.isAlphaOrder()); //$NON-NLS-1$
                append(path, "resolveInWorkspace", setup.isResolveInWorkspace()); //$NON-NLS-1$
                append(path, "readOSGiMetadata", setup.isReadOSGiMetadata()); //$NON-NLS-1$
                append(path, "retrievedClasspath", setup.isRetrievedClasspath()); //$NON-NLS-1$
                if (setup.isRetrievedClasspath()) {
                    RetrieveSetup retrieveSetup = setup.getRetrieveSetup();
                    append(path, "retrievedClasspathPattern", retrieveSetup.getRetrievePattern()); //$NON-NLS-1$
                    append(path, "retrievedClasspathSync", retrieveSetup.isRetrieveSync()); //$NON-NLS-1$
                    append(path, "retrievedClasspathTypes", retrieveSetup.getRetrieveTypes()); //$NON-NLS-1$
                }
            }
            if (conf.isMappingProjectSpecific()) {
                MappingSetup setup = conf.getMappingSetup();
                append(path, "sourceTypes", setup.getSourceTypes()); //$NON-NLS-1$
                append(path, "javadocTypes", setup.getJavadocTypes()); //$NON-NLS-1$
                append(path, "sourceSuffixes", setup.getSourceSuffixes()); //$NON-NLS-1$
                append(path, "javadocSuffixes", setup.getJavadocSuffixes()); //$NON-NLS-1$
                append(path, "mapIfOnlyOneSource", setup.isMapIfOnlyOneSource()); //$NON-NLS-1$
                append(path, "mapIfOnlyOneJavadoc", setup.isMapIfOnlyOneJavadoc()); //$NON-NLS-1$
            }
            if (conf.isAdvancedProjectSpecific()) {
                AdvancedSetup setup = conf.getAdvancedSetup();
                append(path, "resolveBeforeLaunch", setup.isResolveBeforeLaunch()); //$NON-NLS-1$
                append(path, "useExtendedResolveId", setup.isUseExtendedResolveId()); //$NON-NLS-1$
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_ERROR, e);
        }
        return new Path(IvyClasspathContainer.ID).append(path.toString());
    }

    private static void append(StringBuffer path, String name, List values)
            throws UnsupportedEncodingException {
        append(path, name, concat(values));
    }

    private static void append(StringBuffer path, String name, String value)
                    throws UnsupportedEncodingException {
        path.append('&');
        path.append(name);
        path.append('=');
        path.append(URLEncoder.encode(value, "UTF-8")); //$NON-NLS-1$
    }

    public static String concat(Collection/* <String> */list) {
        if (list == null) {
            return ""; //$NON-NLS-1$
        }
        StringBuffer b = new StringBuffer();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append(","); //$NON-NLS-1$
            }
        }
        return b.toString();
    }

    private static void append(StringBuffer path, String name, boolean value)
                    throws UnsupportedEncodingException {
        append(path, name, Boolean.toString(value));
    }

}
