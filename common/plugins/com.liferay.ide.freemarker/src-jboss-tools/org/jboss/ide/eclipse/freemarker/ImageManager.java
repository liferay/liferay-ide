/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker;

import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class ImageManager {

	public static final String IMG_MACRO = "icons/userdefined_directive_call.gif"; //$NON-NLS-1$
	public static final String IMG_IMPORT = "icons/import.gif"; //$NON-NLS-1$
	public static final String IMG_IMPORT_COLLECTION = "icons/import_collection.gif"; //$NON-NLS-1$
	public static final String IMG_FUNCTION = "icons/function.gif"; //$NON-NLS-1$
	

	public static Image getImage(String filename) {
		return Plugin.getDefault().getImage(filename);
	}
	
}