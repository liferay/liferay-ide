package com.liferay.ide.gradle.toolingapi.custom;

import java.io.File;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("serial")
public class DefaultModel implements Serializable {

    private final Set<String> _pluginClassNames;
    private final Set<File> _outputFiles;

    public DefaultModel(Set<String> pluginClassNames, Set<File> outputFiles) {
        _pluginClassNames = pluginClassNames;
        _outputFiles = outputFiles;
    }

    public boolean hasPlugin(String pluginClassName) {
        return _pluginClassNames.contains(pluginClassName);
    }

    public Set<File> getOutputFiles() {
        return _outputFiles;
    }

    public Set<String> getPluginClassNames() {
        return _pluginClassNames;
    }
}
