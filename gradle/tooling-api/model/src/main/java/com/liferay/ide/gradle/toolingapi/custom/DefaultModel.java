package com.liferay.ide.gradle.toolingapi.custom;

import java.io.File;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("serial")
public class DefaultModel implements Serializable {

    private final Set<String> pluginClassNames;
    private final Set<File> outputFiles;

    public DefaultModel(Set<String> pluginClassNames, Set<File> outputFiles) {
        this.pluginClassNames = pluginClassNames;
        this.outputFiles = outputFiles;
    }

    public boolean hasPlugin(String pluginClassName) {
        return pluginClassNames.contains(pluginClassName);
    }

    public Set<File> getOutputFiles() {
        return this.outputFiles;
    }
}
