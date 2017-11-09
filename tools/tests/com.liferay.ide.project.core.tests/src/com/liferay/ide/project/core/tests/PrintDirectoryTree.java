/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.project.core.tests;

import java.io.File;

public class PrintDirectoryTree {

	public static String print(File folder) {
	    StringBuilder sb = new StringBuilder();
	    print(folder, sb);
	    return sb.toString();
	}

	public static void print(File folder, StringBuilder sb) {
		if (!folder.isDirectory()) {
	        throw new IllegalArgumentException("folder is not a directory");
	    }
	    int indent = 0;
	    printDirectoryTree(folder, indent, sb);
	}

	private static void printDirectoryTree(File folder, int indent, StringBuilder sb) {
	    sb.append(getIndentString(indent));
	    sb.append("+--");
	    sb.append(folder.getName());
	    sb.append("/");
	    sb.append("\n");

	    for (File file : folder.listFiles()) {
	        if (file.isDirectory()) {
	            printDirectoryTree(file, indent + 1, sb);
	        } else {
	            printFile(file, indent + 1, sb);
	        }
	    }

	}

	private static void printFile(File file, int indent, StringBuilder sb) {
	    sb.append(getIndentString(indent));
	    sb.append("+--");
	    sb.append(file.getName());
	    sb.append("\n");
	}

	private static String getIndentString(int indent) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < indent; i++) {
	        sb.append("|  ");
	    }
	    return sb.toString();
	}
}