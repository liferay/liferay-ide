
package com.liferay.ide.eclipse.portlet.core.descriptor;

import com.liferay.ide.eclipse.core.util.NodeUtil;
import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class PortletDescriptorValidator extends AbstractValidator {

	public static final String PORTLET_CLASS_ELEMENT = "portlet-class";

	public static final String RESOURCE_BUNDLE_ELEMENT = "resource-bundle";

	public static final String MARKER_TYPE = "com.liferay.ide.eclipse.portlet.core.portletDescriptorValidationMarker";

	public static final String PREFERENCE_NODE_QUALIFIER = PortletCore.getDefault().getBundle().getSymbolicName();

	public static final String VALIDATION_PORTLET_CLASS_NOT_FOUND = "validation-portlet-class-not-found";

	public static final String VALIDATION_RESOURCE_BUNDLE_NOT_FOUND = "validation-resource-bundle-not-found";

	public static final String MESSAGE_RESOURCE_BUNDLE_NOT_FOUND =
		"The resource bundle {0} was not found on the Java Build Path";

	public static final String MESSAGE_PORTLET_CLASS_NOT_FOUND =
		"The portlet class {0} was not found on the Java Build Path";

	protected IPreferencesService fPreferencesService = Platform.getPreferencesService();

	public PortletDescriptorValidator() {
		super();
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE) {
			return null;
		}

		ValidationResult result = new ValidationResult();

		IFile portletXml = (IFile) resource;

		if (portletXml.isAccessible() && ProjectUtil.isPortletProject(resource.getProject())) {
			final IJavaProject javaProject = JavaCore.create(portletXml.getProject());

			if (javaProject.exists()) {
				IScopeContext[] scopes = new IScopeContext[] {
					new InstanceScope(), new DefaultScope()
				};

				// ProjectScope projectScope = new
				// ProjectScope(portletXml.getProject());

				// boolean useProjectSettings =
				// projectScope.getNode(PREFERENCE_NODE_QUALIFIER).getBoolean(
				// JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS,
				// false);
				//				
				// if (useProjectSettings) {
				// scopes = new IScopeContext[] {
				// projectScope, new InstanceScope(), new DefaultScope()
				// };
				// }

				try {
					Map<String, Object>[] problems = detectProblems(javaProject, portletXml, scopes);

					for (int i = 0; i < problems.length; i++) {
						ValidatorMessage message =
							ValidatorMessage.create(problems[i].get(IMarker.MESSAGE).toString(), resource);
						message.setType(MARKER_TYPE);
						message.setAttributes(problems[i]);
						result.add(message);
					}

					// if (problems.length > 0) {
					// result.setDependsOn(new IResource[] {
					// portletXml
					// });
					// }
				}
				catch (Exception e) {
					PortletCore.logError(e);
				}
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object>[] detectProblems(
		IJavaProject javaProject, IFile portletXml, IScopeContext[] preferenceScopes)
		throws CoreException {

		List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

		IStructuredModel m = null;

		try {
			m = StructuredModelManager.getModelManager().getModelForRead(portletXml);

			if (m != null && m instanceof IDOMModel) {
				IDOMDocument document = ((IDOMModel) m).getDocument();

				checkPortletClassElements(document, javaProject, preferenceScopes, problems);

				checkResourceBundleElements(document, javaProject, preferenceScopes, problems);
			}
		}
		catch (IOException e) {

		}
		finally {
			if (m != null) {
				m.releaseFromRead();
			}
		}

		Map<String, Object>[] retval = new Map[problems.size()];

		return (Map<String, Object>[]) problems.toArray(retval);
	}
	
	protected void checkResourceBundleElements(
		IDOMDocument document, IJavaProject javaProject, IScopeContext[] preferenceScopes,
		List<Map<String, Object>> problems) {

		NodeList resourceBundles = document.getElementsByTagName(RESOURCE_BUNDLE_ELEMENT);

		for (int i = 0; i < resourceBundles.getLength(); i++) {
			Node item = resourceBundles.item(i);

			Map<String, Object> problem =
				checkClassResource(
					javaProject, item, preferenceScopes, VALIDATION_RESOURCE_BUNDLE_NOT_FOUND,
					MESSAGE_RESOURCE_BUNDLE_NOT_FOUND);

			if (problem != null) {
				problems.add(problem);
			}
		}
	}

	protected void checkPortletClassElements(
		IDOMDocument document, IJavaProject javaProject, IScopeContext[] preferenceScopes,
		List<Map<String, Object>> problems) {

		NodeList classes = document.getElementsByTagName(PORTLET_CLASS_ELEMENT);

		for (int i = 0; i < classes.getLength(); i++) {
			Node item = classes.item(i);

			Map<String, Object> problem =
				checkClass(
					javaProject, item, preferenceScopes, VALIDATION_PORTLET_CLASS_NOT_FOUND,
					MESSAGE_PORTLET_CLASS_NOT_FOUND);

			if (problem != null) {
				problems.add(problem);
			}
		}
	}

	protected Integer getMessageSeverity(IScopeContext[] preferenceScopes, String key) {
		int sev =
			fPreferencesService.getInt(PREFERENCE_NODE_QUALIFIER, key, IMessage.NORMAL_SEVERITY, preferenceScopes);

		switch (sev) {
		case ValidationMessage.ERROR:
			return new Integer(IMarker.SEVERITY_ERROR);

		case ValidationMessage.WARNING:
			return new Integer(IMarker.SEVERITY_WARNING);

		case ValidationMessage.INFORMATION:
			return new Integer(IMarker.SEVERITY_INFO);

		case ValidationMessage.IGNORE:
			return null;
		}

		return new Integer(IMarker.SEVERITY_WARNING);
	}

	protected Map<String, Object> checkClassResource(
		IJavaProject javaProject, Node classResourceSpecifier, IScopeContext[] preferenceScopes, String preferenceKey,
		String errorMessage) {

		String classResource = NodeUtil.getTextContent(classResourceSpecifier);

		if (classResource != null && classResource.length() > 2) {

		}

		return null;
	}

	protected Map<String, Object> checkClass(
		IJavaProject javaProject, Node classSpecifier, IScopeContext[] preferenceScopes, String preferenceKey,
		String errorMessage) {

		String className = NodeUtil.getTextContent(classSpecifier);

		if (className != null && className.length() > 2) {
			IType type = null;

			try {
				type = javaProject.findType(className);
			}
			catch (JavaModelException e) {
				return null;
			}

			if (type == null || !type.exists()) {
				Object severity = getMessageSeverity(preferenceScopes, preferenceKey);

				if (severity == null) {
					return null;
				}

				IDOMNode classElement = (IDOMNode) classSpecifier;

				Map<String, Object> markerValues = new HashMap<String, Object>();

				markerValues.put(IMarker.SEVERITY, severity);

				int start = classElement.getStartOffset();

				if (classElement.getStartStructuredDocumentRegion() != null &&
					classElement.getEndStructuredDocumentRegion() != null) {

					start = classElement.getStartStructuredDocumentRegion().getEndOffset();
				}

				int end = classElement.getEndOffset();

				if (classElement.getStartStructuredDocumentRegion() != null &&
					classElement.getEndStructuredDocumentRegion() != null) {

					end = classElement.getEndStructuredDocumentRegion().getStartOffset();
				}

				int line = classElement.getStructuredDocument().getLineOfOffset(start);

				markerValues.put(IMarker.CHAR_START, new Integer(start));
				markerValues.put(IMarker.CHAR_END, new Integer(end));
				markerValues.put(IMarker.LINE_NUMBER, new Integer(line + 1));
				markerValues.put(IMarker.MESSAGE, MessageFormat.format(errorMessage, new Object[] {
					className
				}));

				return markerValues;
			}
		}

		return null;
	}

	@Override
	public boolean shouldClearMarkers(ValidationEvent event) {
		return true;
	}

}
