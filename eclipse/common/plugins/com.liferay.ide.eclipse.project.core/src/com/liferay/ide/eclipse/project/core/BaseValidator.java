package com.liferay.ide.eclipse.project.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

@SuppressWarnings("restriction")
public abstract class BaseValidator extends AbstractValidator {

	protected IPreferencesService fPreferencesService = Platform.getPreferencesService();

	public BaseValidator() {
		super();
	}

	protected Integer getMessageSeverity(String qualifier, IScopeContext[] preferenceScopes, String key) {
		int sev = fPreferencesService.getInt(qualifier, key, IMessage.NORMAL_SEVERITY, preferenceScopes);
	
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

	protected Map<String, Object> createMarkerValues(
		String qualifier, IScopeContext[] preferenceScopes, String preferenceKey, IDOMNode domNode, String message) {
		Object severity = getMessageSeverity(qualifier, preferenceScopes, preferenceKey);
	
		if (severity == null) {
			return null;
		}
	
		Map<String, Object> markerValues = new HashMap<String, Object>();
	
		markerValues.put(IMarker.SEVERITY, severity);
	
		int start = domNode.getStartOffset();
	
		if (domNode.getStartStructuredDocumentRegion() != null && domNode.getEndStructuredDocumentRegion() != null) {
	
			start = domNode.getStartStructuredDocumentRegion().getEndOffset();
		}
	
		int end = domNode.getEndOffset();
	
		if (domNode.getStartStructuredDocumentRegion() != null && domNode.getEndStructuredDocumentRegion() != null) {
	
			end = domNode.getEndStructuredDocumentRegion().getStartOffset();
		}
	
		int line = domNode.getStructuredDocument().getLineOfOffset(start);
	
		markerValues.put(IMarker.CHAR_START, new Integer(start));
		markerValues.put(IMarker.CHAR_END, new Integer(end));
		markerValues.put(IMarker.LINE_NUMBER, new Integer(line + 1));
		markerValues.put(IMarker.MESSAGE, message);
	
		return markerValues;
	}

	@Override
	public boolean shouldClearMarkers(ValidationEvent event) {
		return true;
	}

}
