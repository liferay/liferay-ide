package com.liferay.ide.workspace.ui.bundle;

import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LiferayBundleConfigurationType extends ConfigurationTypeBase implements ConfigurationType {
    @NotNull
    public static LiferayBundleConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(LiferayBundleConfigurationType.class);
    }

    public LiferayBundleConfigurationType() {
        super("JarApplication", ExecutionBundle.message("jar.application.configuration.name"),
                ExecutionBundle.message("jar.application.configuration.description"), AllIcons.FileTypes.Archive);
        addFactory(new ConfigurationFactoryEx(this) {
            @Override
            public void onNewConfigurationCreated(@NotNull RunConfiguration configuration) {
                LiferayBundleConfiguration jarApplicationConfiguration = (LiferayBundleConfiguration)configuration;
                if (StringUtil.isEmpty(jarApplicationConfiguration.getWorkingDirectory())) {
                    String baseDir = FileUtil.toSystemIndependentName(StringUtil.notNullize(configuration.getProject().getBasePath()));
                    jarApplicationConfiguration.setWorkingDirectory(baseDir);
                }
            }

            public RunConfiguration createTemplateConfiguration(Project project) {
                return new LiferayBundleConfiguration(project, this, "");
            }
        });
    }


    @Override
    public String getDisplayName() {
        return "Liferay Bundle";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Run Liferay Bundle";
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/liferay.png");
    }

    @NotNull
    @Override
    public String getId() {
        return "LiferayBundleConfiguration";
    }


}
