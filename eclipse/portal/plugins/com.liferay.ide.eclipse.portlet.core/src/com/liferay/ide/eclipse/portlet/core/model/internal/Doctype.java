/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kamesh
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Doctype {

	String rootElementName();

	String publicId();

	String systemId();
}
