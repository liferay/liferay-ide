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

package com.liferay.ide.idea.ui.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Terry Jia
 */
public class ServerUtil {

    private static String[] osgiBundleDirs = new String[]{"core", "modules", "portal", "static"};

    public static List<String> getModuleFileListFrom70Server(File runtime) {
        final List<String> bundles = new ArrayList<>();

        try {
            for (String dir : osgiBundleDirs) {
                final File dirFile = new File(new File(runtime, "osgi"), dir);

                if (dirFile.exists()) {
                    final File[] files = dirFile.listFiles(new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String name) {
                            return name.matches(".*\\.jar");
                        }
                    });

                    if (files != null && files.length > 0) {
                        for (final File file : files) {
                            bundles.add(file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final File[] files = getMarketplaceLpkgFiles(runtime);

        for (File file : files) {
            try (JarFile jar = new JarFile(file)) {
                Enumeration<JarEntry> enu = jar.entries();

                while (enu.hasMoreElements()) {
                    JarEntry entry = enu.nextElement();

                    String name = entry.getName();

                    if (name.endsWith(".jar")) {
                        bundles.add(name);
                    }
                }
            } catch (IOException e) {
            }
        }

        return bundles;
    }

    public static File[] getMarketplaceLpkgFiles(File runtime) {
        File marketplace = new File(new File(runtime, "osgi"), "marketplace");

        File[] files = marketplace.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches(".*\\.lpkg");
            }
        });

        return files;
    }

    public static File getModuleFileFrom70Server(File runtime, String hostOsgiBundle, File temp) {
        File moduleOsgiBundle = null;

        for (String dir : osgiBundleDirs) {
            moduleOsgiBundle = new File(new File(new File(runtime, "osgi"), dir), hostOsgiBundle);

            if (moduleOsgiBundle.exists()) {
                FileUtil.copyFile(moduleOsgiBundle, new File(temp, hostOsgiBundle));

                return moduleOsgiBundle;
            }
        }

        final File f = new File(temp, hostOsgiBundle);

        if (f.exists()) {
            return f;
        }

        File[] files = getMarketplaceLpkgFiles(runtime);

        InputStream in = null;

        try {
            boolean found = false;

            for (File file : files) {
                try (JarFile jar = new JarFile(file)) {
                    Enumeration<JarEntry> enu = jar.entries();

                    while (enu.hasMoreElements()) {
                        JarEntry entry = enu.nextElement();

                        String name = entry.getName();

                        if (name.contains(hostOsgiBundle)) {
                            in = jar.getInputStream(entry);
                            found = true;

                            FileUtil.writeFile(f, in);

                            break;
                        }
                    }

                    if (found) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return f;
    }

}
