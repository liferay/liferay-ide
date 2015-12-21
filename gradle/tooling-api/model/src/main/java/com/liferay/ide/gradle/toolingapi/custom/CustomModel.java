package com.liferay.ide.gradle.toolingapi.custom;

import java.io.File;
import java.util.Set;

/**
 * @author Gregory Amerson
 */
public interface CustomModel {

    boolean hasPlugin(String className);
    Set<File> getOutputFiles();
    Set<String> getPluginClassNames();
}
