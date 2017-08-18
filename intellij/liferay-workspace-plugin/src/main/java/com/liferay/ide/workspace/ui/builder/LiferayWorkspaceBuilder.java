package com.liferay.ide.workspace.ui.builder;

import com.intellij.ide.util.projectWizard.EmptyModuleBuilder;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.IconLoader;
import com.liferay.ide.workspace.ui.UI;
import com.liferay.ide.workspace.ui.util.BladeCLI;

import javax.swing.*;
import java.io.File;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceBuilder extends ModuleBuilder {

    @Override
    public void setupRootModel(ModifiableRootModel model) throws ConfigurationException {
        final Project project = model.getProject();

        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + project.getBasePath() + "\"" );
        sb.append( " " );
        sb.append( "init " );
        sb.append( "-f" );

        BladeCLI.execute( sb.toString() );
    }

    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public String getPresentableName() {
        return UI.LIFERAY_WORKSPACE;
    }

    @Override
    public String getParentGroup() {
        return UI.LIFERAY;
    }

    @Override
    public String getDescription() {
        return UI.LIFERAY_WORKSPACE;
    }

}