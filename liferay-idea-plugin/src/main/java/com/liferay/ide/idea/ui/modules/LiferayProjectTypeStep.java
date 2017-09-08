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

package com.liferay.ide.idea.ui.modules;

import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.ide.projectWizard.ProjectCategory;
import com.intellij.ide.projectWizard.ProjectCategoryUsagesCollector;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.frameworkSupport.FrameworkRole;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportUtil;
import com.intellij.ide.util.newProjectWizard.*;
import com.intellij.ide.util.newProjectWizard.impl.FrameworkSupportModelBase;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.ui.popup.ListItemDescriptorAdapter;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import com.intellij.platform.templates.ArchivedProjectTemplate;
import com.intellij.platform.templates.BuilderBasedTemplate;
import com.intellij.platform.templates.TemplateModuleBuilder;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.SingleSelectionModel;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.popup.list.GroupedItemsListRenderer;
import com.intellij.util.Function;
import com.intellij.util.containers.*;
import com.intellij.util.ui.UIUtil;

import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Terry Jia
 */
public class LiferayProjectTypeStep extends ModuleWizardStep implements SettingsStep, Disposable {
    public static final Convertor<FrameworkSupportInModuleProvider, String> PROVIDER_STRING_CONVERTOR =
            o -> o.getId();
    public static final Function<FrameworkSupportNode, String> NODE_STRING_FUNCTION = FrameworkSupportNodeBase::getId;
    private static final Logger LOG = Logger.getInstance(LiferayProjectTypeStep.class);
    private static final String TEMPLATES_CARD = "templates card";
    private static final String FRAMEWORKS_CARD = "frameworks card";
    private static final String PROJECT_WIZARD_GROUP = "project.wizard.group";
    private final WizardContext myContext;
    private final NewLiferayModuleWizard wizard;
    private final ModulesProvider modulesProvider;
    private final AddSupportForFrameworksPanel frameworksPanel;
    private final ModuleBuilder.ModuleConfigurationUpdater configurationUpdater;
    private final Map<ProjectTemplate, ModuleBuilder> builders = FactoryMap.createMap(key -> (ModuleBuilder) key.createModuleBuilder());
    private final Map<String, ModuleWizardStep> customSteps = new THashMap<>();
    private final MultiMap<TemplatesGroup, ProjectTemplate> templatesMap;
    private JPanel mainPanel;
    private JPanel optionsPanel;
    private JBList<TemplatesGroup> projectTypeList;
    private LiferayProjectTemplateList templatesList;
    private JPanel frameworksPanelPlaceholder;
    private JPanel headerPanel;
    private JBLabel frameworksLabel;
    @Nullable
    private ModuleWizardStep settingsStep;
    private String currentCard;
    private TemplatesGroup lastSelectedGroup;

    public LiferayProjectTypeStep(WizardContext context, NewLiferayModuleWizard wizard, ModulesProvider modulesProvider) {
        myContext = context;
        this.wizard = wizard;

        templatesMap = new ConcurrentMultiMap<>();
        final List<TemplatesGroup> groups = fillTemplatesMap(context);
        LOG.debug("groups=" + groups);

        projectTypeList.setModel(new CollectionListModel<>(groups));
        projectTypeList.setSelectionModel(new SingleSelectionModel());
        projectTypeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateSelection();
            }
        });

        projectTypeList.setCellRenderer(new GroupedItemsListRenderer<TemplatesGroup>(new ListItemDescriptorAdapter<TemplatesGroup>() {
            @Nullable
            @Override
            public String getTextFor(TemplatesGroup value) {
                return value.getName();
            }

            @Nullable
            @Override
            public String getTooltipFor(TemplatesGroup value) {
                return value.getDescription();
            }

            @Nullable
            @Override
            public Icon getIconFor(TemplatesGroup value) {
                return value.getIcon();
            }

            @Override
            public boolean hasSeparatorAboveOf(TemplatesGroup value) {
                int index = groups.indexOf(value);
                if (index < 1) return false;
                TemplatesGroup upper = groups.get(index - 1);
                if (upper.getParentGroup() == null && value.getParentGroup() == null) return true;
                return !Comparing.equal(upper.getParentGroup(), value.getParentGroup()) &&
                        !Comparing.equal(upper.getName(), value.getParentGroup());
            }
        }) {
            @Override
            protected JComponent createItemComponent() {
                JComponent component = super.createItemComponent();
                myTextLabel.setBorder(IdeBorderFactory.createEmptyBorder(3));
                return component;
            }
        });

        new ListSpeedSearch(projectTypeList) {
            @Override
            protected String getElementText(Object element) {
                return ((TemplatesGroup) element).getName();
            }
        };

        this.modulesProvider = modulesProvider;

        final Project project = context.getProject();

        final LibrariesContainer container = LibrariesContainerFactory.createContainer(context, modulesProvider);

        FrameworkSupportModelBase model = new FrameworkSupportModelBase(project, null, container) {
            @NotNull
            @Override
            public String getBaseDirectoryForLibrariesPath() {
                ModuleBuilder builder = getSelectedBuilder();
                return StringUtil.notNullize(builder.getContentEntryPath());
            }

            @Override
            public ModuleBuilder getModuleBuilder() {
                return getSelectedBuilder();
            }
        };

        frameworksPanel = new AddSupportForFrameworksPanel(Collections.emptyList(), model, true, headerPanel);

        Disposer.register(this, frameworksPanel);

        frameworksPanelPlaceholder.add(frameworksPanel.getMainPanel());

        frameworksLabel.setLabelFor(frameworksPanel.getFrameworksTree());

        frameworksLabel.setBorder(IdeBorderFactory.createEmptyBorder(3));

        configurationUpdater = new ModuleBuilder.ModuleConfigurationUpdater() {
            @Override
            public void update(@NotNull Module module, @NotNull ModifiableRootModel rootModel) {
                if (isFrameworksMode()) {
                    frameworksPanel.addSupport(module, rootModel);
                }
            }
        };

        projectTypeList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                projectTypeChanged();
            }
        });

        templatesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateSelection();
            }
        });

        for (TemplatesGroup templatesGroup : templatesMap.keySet()) {
            final ModuleBuilder builder = templatesGroup.getModuleBuilder();

            if (builder instanceof LiferayModuleBuilder) {
                if (builder != null) {
                    this.wizard.getSequence().addStepsForBuilder(builder, context, modulesProvider);
                }
            }
        }

        final String groupId = PropertiesComponent.getInstance().getValue(PROJECT_WIZARD_GROUP);

        if (groupId != null) {
            TemplatesGroup group = ContainerUtil.find(groups, group1 -> groupId.equals(group1.getId()));
            if (group != null) {
                projectTypeList.setSelectedValue(group, true);
            }
        }

        if (projectTypeList.getSelectedValue() == null) {
            projectTypeList.setSelectedIndex(0);
        }

        templatesList.restoreSelection();
    }

    private static ModuleType getModuleType(TemplatesGroup group) {
        final ModuleBuilder moduleBuilder = group.getModuleBuilder();

        return moduleBuilder == null ? null : moduleBuilder.getModuleType();
    }

    private static boolean matchFramework(ProjectCategory projectCategory, FrameworkSupportInModuleProvider framework) {
        final FrameworkRole[] roles = framework.getRoles();

        if (roles.length == 0) {
            return true;
        }

        return ContainerUtil.intersects(Arrays.asList(roles), Arrays.asList(projectCategory.getAcceptableFrameworkRoles()));
    }

    public static MultiMap<TemplatesGroup, ProjectTemplate> getTemplatesMap(WizardContext context) {
        final MultiMap<TemplatesGroup, ProjectTemplate> groups = new MultiMap<>();

        for (ProjectTemplatesFactory factory : ProjectTemplatesFactory.EP_NAME.getExtensions()) {
            for (String group : factory.getGroups()) {
                ProjectTemplate[] templates = factory.createTemplates(group, context);
                List<ProjectTemplate> values = Arrays.asList(templates);
                if (!values.isEmpty()) {
                    Icon icon = factory.getGroupIcon(group);
                    String parentGroup = factory.getParentGroup(group);
                    TemplatesGroup templatesGroup = new TemplatesGroup(group, null, icon, factory.getGroupWeight(group), parentGroup, group, null);
                    groups.putValues(templatesGroup, values);
                }
            }
        }

        return groups;
    }

    private boolean isFrameworksMode() {
        return FRAMEWORKS_CARD.equals(currentCard) && getSelectedBuilder().equals(myContext.getProjectBuilder());
    }

    private List<TemplatesGroup> fillTemplatesMap(WizardContext context) {
        templatesMap.put(new TemplatesGroup(new LiferayModuleBuilder()), new ArrayList<>());
        templatesMap.put(new TemplatesGroup(new LiferayModuleFragmentBuilder()), new ArrayList<>());

        final List<TemplatesGroup> groups = new ArrayList<>(templatesMap.keySet());

        final MultiMap<ModuleType, TemplatesGroup> moduleTypes = new MultiMap<>();

        for (final TemplatesGroup group : groups) {
            final ModuleType type = getModuleType(group);

            moduleTypes.putValue(type, group);
        }

        return groups;
    }

    public void projectTypeChanged() {
        final TemplatesGroup group = getSelectedGroup();

        if (group == null || group == lastSelectedGroup) {
            return;
        }

        lastSelectedGroup = group;

        PropertiesComponent.getInstance().setValue(PROJECT_WIZARD_GROUP, group.getId());

        final ModuleBuilder groupModuleBuilder = group.getModuleBuilder();

        settingsStep = null;
        headerPanel.removeAll();

        if (groupModuleBuilder != null && groupModuleBuilder.getModuleType() != null) {
            settingsStep = groupModuleBuilder.modifyProjectTypeStep(this);
        }

        if (groupModuleBuilder == null || groupModuleBuilder.isTemplateBased()) {
            showTemplates(group);
        } else if (!showCustomOptions(groupModuleBuilder)) {
            final List<FrameworkSupportInModuleProvider> providers = FrameworkSupportUtil.getProviders(groupModuleBuilder);

            final ProjectCategory category = group.getProjectCategory();

            if (category != null) {
                final List<FrameworkSupportInModuleProvider> filtered = ContainerUtil.filter(providers, provider -> matchFramework(category, provider));

                final Map<String, FrameworkSupportInModuleProvider> map = ContainerUtil.newMapFromValues(providers.iterator(), PROVIDER_STRING_CONVERTOR);

                final Set<FrameworkSupportInModuleProvider> set = new java.util.HashSet<>(filtered);

                for (FrameworkSupportInModuleProvider provider : filtered) {
                    for (FrameworkSupportInModuleProvider.FrameworkDependency depId : provider.getDependenciesFrameworkIds()) {
                        final FrameworkSupportInModuleProvider dependency = map.get(depId.getFrameworkId());

                        if (dependency != null) {
                            set.add(dependency);
                        }
                    }
                }

                frameworksPanel.setProviders(new ArrayList<>(set),
                        new java.util.HashSet<>(Arrays.asList(category.getAssociatedFrameworkIds())),
                        new java.util.HashSet<>(Arrays.asList(category.getPreselectedFrameworkIds())));
            } else {
                frameworksPanel.setProviders(providers);
            }

            getSelectedBuilder().addModuleConfigurationUpdater(configurationUpdater);

            showCard(FRAMEWORKS_CARD);
        }

        headerPanel.setVisible(headerPanel.getComponentCount() > 0);

        final List<JLabel> labels = UIUtil.findComponentsOfType(headerPanel, JLabel.class);

        int width = 0;

        for (JLabel label : labels) {
            int width1 = label.getPreferredSize().width;

            width = Math.max(width, width1);
        }

        for (JLabel label : labels) {
            label.setPreferredSize(new Dimension(width, label.getPreferredSize().height));
        }

        headerPanel.revalidate();
        headerPanel.repaint();

        updateSelection();
    }

    private void showCard(String card) {
        ((CardLayout) optionsPanel.getLayout()).show(optionsPanel, card);
        currentCard = card;
    }

    private void showTemplates(TemplatesGroup group) {
        setTemplatesList(group, templatesMap.get(group), false);

        showCard(TEMPLATES_CARD);
    }

    private void setTemplatesList(TemplatesGroup group, Collection<ProjectTemplate> templates, boolean preserveSelection) {
        final List<ProjectTemplate> list = new ArrayList<>(templates);

        final ModuleBuilder moduleBuilder = group.getModuleBuilder();

        if (moduleBuilder != null && !(moduleBuilder instanceof TemplateModuleBuilder)) {
            list.add(0, new BuilderBasedTemplate(moduleBuilder));
        }

        templatesList.setTemplates(list, preserveSelection);
    }

    private boolean showCustomOptions(@NotNull ModuleBuilder builder) {
        final String card = builder.getBuilderId();

        if (!customSteps.containsKey(card)) {
            final ModuleWizardStep step = builder.getCustomOptionsStep(myContext, this);

            if (step == null) {
                return false;
            }

            step.updateStep();

            customSteps.put(card, step);
            optionsPanel.add(step.getComponent(), card);
        }

        showCard(card);

        return true;
    }

    @Nullable
    private ModuleWizardStep getCustomStep() {
        return customSteps.get(currentCard);
    }

    private TemplatesGroup getSelectedGroup() {
        return projectTypeList.getSelectedValue();
    }

    @Nullable
    public ProjectTemplate getSelectedTemplate() {
        return currentCard == TEMPLATES_CARD ? templatesList.getSelectedTemplate() : null;
    }

    private ModuleBuilder getSelectedBuilder() {
        final ProjectTemplate template = getSelectedTemplate();

        if (template != null) {
            return builders.get(template);
        }

        return getSelectedGroup().getModuleBuilder();
    }

    public void onWizardFinished() throws CommitStepException {
        if (isFrameworksMode()) {
            boolean ok = frameworksPanel.downloadLibraries(wizard.getContentComponent());
            if (!ok) {
                throw new CommitStepException(null);
            }
        }

        final TemplatesGroup group = getSelectedGroup();

        if (group != null) {
            ProjectCategoryUsagesCollector.projectTypeUsed(group.getId());
        }
    }

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void updateDataModel() {
        final ModuleBuilder builder = getSelectedBuilder();

        wizard.getSequence().addStepsForBuilder(builder, myContext, modulesProvider);

        final ModuleWizardStep step = getCustomStep();

        if (step != null) {
            step.updateDataModel();
        }

        if (settingsStep != null) {
            settingsStep.updateDataModel();
        }
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (settingsStep != null) {
            if (!settingsStep.validate()) return false;
        }

        final ModuleWizardStep step = getCustomStep();

        if (step != null && !step.validate()) {
            return false;
        }

        if (isFrameworksMode() && !frameworksPanel.validate()) {
            return false;
        }

        return super.validate();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return projectTypeList;
    }

    @Override
    public void dispose() {
        lastSelectedGroup = null;
        settingsStep = null;
        templatesMap.clear();
        builders.clear();
        customSteps.clear();
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }

    private void updateSelection() {
        final ProjectTemplate template = getSelectedTemplate();

        if (template != null) {
            myContext.setProjectTemplate(template);
        }

        final ModuleBuilder builder = getSelectedBuilder();

        myContext.setProjectBuilder(builder);

        if (builder != null) {
            wizard.getSequence().setType(builder.getBuilderId());
        }

        wizard.setDelegate(builder instanceof WizardDelegate ? (WizardDelegate) builder : null);
        wizard.updateWizardButtons();
    }

    @Override
    public WizardContext getContext() {
        return myContext;
    }

    @Override
    public void addSettingsField(@NotNull String label, @NotNull JComponent field) {
        LiferayProjectSettingsStep.addField(label, field, headerPanel);
    }

    @Override
    public void addSettingsComponent(@NotNull JComponent component) {
    }

    @Override
    public void addExpertPanel(@NotNull JComponent panel) {

    }

    @Override
    public void addExpertField(@NotNull String label, @NotNull JComponent field) {
    }

    @Override
    public JTextField getModuleNameField() {
        return null;
    }

    @Override
    public String getHelpId() {
        if (getCustomStep() != null && getCustomStep().getHelpId() != null) {
            return getCustomStep().getHelpId();
        }
        return myContext.isCreatingNewProject() ? "Project_Category_and_Options" : "Module_Category_and_Options";
    }

}
