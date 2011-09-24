/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.util;

/**
 * @author kamesh
 */
public interface PortletAppModelConstants {

	public static final String COLON = ":";
	public static final String DEFAULT_QNAME_PREFIX = "qns";
	public static final String LOCAL_PART_DEFAULT_VALUE = "LOCAL_PART";
	public static final String NAMESPACE_URI_DEFAULT_VALUE = "NAMESPACE_URI";
	public static final String DEFAULT_DERIVED_QNAME_VALUE = "{NAMESPACE_URI}LOCAL_PART";
	public static final String XMLNS_NS_URI = "http://www.w3.org/2001/XMLSchema";
	public static final String DEFAULT_QNAME_NS_DECL = "xmlns" + COLON + DEFAULT_QNAME_PREFIX;
	public static final String NS_DECL = "xmlns:%s";
}
