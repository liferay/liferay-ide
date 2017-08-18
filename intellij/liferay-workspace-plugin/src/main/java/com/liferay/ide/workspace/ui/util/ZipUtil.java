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

package com.liferay.ide.workspace.ui.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Terry Jia
 */
public class ZipUtil {

    public static ZipFile open(final File file) throws IOException {
        try {
            return new ZipFile(file);
        } catch (FileNotFoundException e) {
            final FileNotFoundException fnfe = new FileNotFoundException(file.getAbsolutePath());

            fnfe.initCause(e);

            throw fnfe;
        }
    }

    public static void unzip(final File file, final File destdir) throws IOException {
        unzip(file, null, destdir);
    }

    public static void unzip(final File file, final String entryToStart, final File destdir) throws IOException {
        final ZipFile zip = open(file);

        try {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            final int totalWork = zip.size();

            int c = 0;
            boolean foundStartEntry = entryToStart == null;

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();

                if (!foundStartEntry) {
                    foundStartEntry = entryToStart.equals(entry.getName());
                    continue;
                }

                String entryName = null;

                if (entryToStart == null) {
                    entryName = entry.getName();
                } else {
                    entryName = entry.getName().replaceFirst(entryToStart, ""); //$NON-NLS-1$
                }

                if (entry.isDirectory()) {
                    File emptyDir = new File(destdir, entryName);

                    mkdir(emptyDir);

                    continue;
                }

                final File f = new File(destdir, entryName);
                final File dir = f.getParentFile();

                mkdir(dir);

                InputStream in = null;
                FileOutputStream out = null;

                try {
                    in = zip.getInputStream(entry);
                    out = new FileOutputStream(f);

                    final byte[] bytes = new byte[1024];
                    int count = in.read(bytes);

                    while (count != -1) {
                        out.write(bytes, 0, count);
                        count = in.read(bytes);
                    }

                    out.flush();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                        }
                    }

                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        } finally {
            try {
                zip.close();
            } catch (IOException e) {
            }
        }
    }

    private static void mkdir(File dir) throws IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            final String msg = "Could not create dir: " + dir.getPath();
            throw new IOException(msg);
        }
    }

}
