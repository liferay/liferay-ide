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
package org.jboss.ide.eclipse.freemarker.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class StringUtil {

	/**
	 * Return the contents of the Stream as a String.
	 * Note:  If the InputStream represents a null String, the Java implementation will try to read from the stream for a certain amount of time
	 * before timing out.
	 * @param is the InputStream to transform into a String
	 * @return the String representation of the Stream
	 */
	public static String getStringFromStream (InputStream is)
		throws IOException
	{
		try {
			InputStreamReader reader = new InputStreamReader(is);
			char[] buffer = new char[1024];
			StringWriter writer = new StringWriter();
			int bytes_read;
			while ((bytes_read = reader.read(buffer)) != -1)
			{
				writer.write(buffer, 0, bytes_read);
			}
			return (writer.toString());
		}
		finally {
			if (null != is) is.close();
		}
	}

}