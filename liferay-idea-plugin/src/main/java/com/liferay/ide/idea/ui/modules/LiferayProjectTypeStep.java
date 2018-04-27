/**
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
 */

package com.liferay.ide.idea.ui.modules;

import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.ide.projectWizard.ProjectCategory;
import com.intellij.ide.projectWizard.ProjectCategoryUsagesCollector;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.frameworkSupport.FrameworkRole;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportUtil;
import com.intellij.ide.util.newProjectWizard.AddSupportForFrameworksPanel;
import com.intellij.ide.util.newProjectWizard.FrameworkSupportNode;
import com.intellij.ide.util.newProjectWizard.FrameworkSupportNodeBase;
import com.intellij.ide.util.newProjectWizard.StepSequence;
import com.intellij.ide.util.newProjectWizard.TemplatesGroup;
import com.intellij.ide.util.newProjectWizard.WizardDelegate;
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
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
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
import com.intellij.util.containers.ConcurrentMultiMap;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.Convertor;
import com.intellij.util.containers.FactoryMap;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.ui.UIUtil;

import com.liferay.ide.idea.util.CoreUtil;

import gnu.trove.THashMap;

import java.awt.CardLayout;
import java.awt.Dimension;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Terry Jia
 */
public class LiferayProjectTypeStep extends ModuleWizardStep implements SettingsStep, Disposable {

	public static final Function<FrameworkSupportNode, String> NODE_STRING_FUNCTION = FrameworkSupportNodeBase::getId;

	public static final Convertor<FrameworkSupportInModuleProvider, String> PROVIDER_STRING_CONVERTOR = o -> o.getId();

	public static MultiMap<TemplatesGroup, ProjectTemplate> getTemplatesMap(WizardContext context) {
		MultiMap<TemplatesGroup, ProjectTemplate> groups = new MultiMap<>();

		for (ProjectTemplatesFactory factory : ProjectTemplatesFactory.EP_NAME.getExtensions()) {
			Stream<String> stream = Stream.of(factory.getGroups());

			stream.map(
				group -> new AbstractMap.SimpleEntry<>(group, factory.createTemplates(group, context))
			).filter(
				pair -> !CoreUtil.isNullOrEmpty(pair.getValue())
			).forEach(
				pair -> {
					String group = pair.getKey();

					Icon icon = factory.getGroupIcon(group);

					String parentGroup = factory.getParentGroup(group);

					TemplatesGroup templatesGroup = new TemplatesGroup(
						group, null, icon, factory.getGroupWeight(group), parentGroup, group, null);

					groups.putValues(templatesGroup, Arrays.asList(pair.getValue()));
				}
			);
		}

		return groups;
	}

	public LiferayProjectTypeStep(
		WizardContext context, NewLiferayModuleWizard wizard, ModulesProvider modulesProvider) {

		_context = context;
		_wizard = wizard;

		_templatesMap = new ConcurrentMultiMap<>();
		List<TemplatesGroup> groups = _fillTemplatesMap();

		if (_log.isDebugEnabled()) {
			_log.debug("groups=" + groups);
		}

		_projectTypeList.setModel(new CollectionListModel<>(groups));
		_projectTypeList.setSelectionModel(new SingleSelectionModel());
		_projectTypeList.addListSelectionListener(
			new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent event) {
					_updateSelection();
				}

			});

		_projectTypeList.setCellRenderer(
			new GroupedItemsListRenderer<TemplatesGroup>(
				new ListItemDescriptorAdapter<TemplatesGroup>() {

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
						return false;
					}

				}) {

				@Override
				protected JComponent createItemComponent() {
					JComponent component = super.createItemComponent();
					myTextLabel.setBorder(IdeBorderFactory.createEmptyBorder(3));

					return component;
				}

			});

		new ListSpeedSearch<TemplatesGroup>(_projectTypeList) {

			@Override
			protected String getElementText(Object element) {
				return ((TemplatesGroup)element).getName();
			}

		};

		_modulesProvider = modulesProvider;

		Project project = context.getProject();

		LibrariesContainer container = LibrariesContainerFactory.createContainer(context, modulesProvider);

		FrameworkSupportModelBase model = new FrameworkSupportModelBase(project, null, container) {

			@NotNull
			@Override
			public String getBaseDirectoryForLibrariesPath() {
				ModuleBuilder builder = _getSelectedBuilder();

				return StringUtil.notNullize(builder.getContentEntryPath());
			}

			@Override
			public ModuleBuilder getModuleBuilder() {
				return _getSelectedBuilder();
			}

		};

		_frameworksPanel = new AddSupportForFrameworksPanel(Collections.emptyList(), model, true, _headerPanel);

		Disposer.register(this, _frameworksPanel);

		_frameworksPanelPlaceholder.add(_frameworksPanel.getMainPanel());

		_frameworksLabel.setLabelFor(_frameworksPanel.getFrameworksTree());

		_frameworksLabel.setBorder(IdeBorderFactory.createEmptyBorder(3));

		_configurationUpdater = new ModuleBuilder.ModuleConfigurationUpdater() {

			@Override
			public void update(@NotNull Module module, @NotNull ModifiableRootModel rootModel) {
				if (_isFrameworksMode()) {
					_frameworksPanel.addSupport(module, rootModel);
				}
			}

		};

		ListSelectionModel listSelectionModel = _projectTypeList.getSelectionModel();

		listSelectionModel.addListSelectionListener(
			new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent event) {
					projectTypeChanged();
				}

			});

		_templatesList.addListSelectionListener(
			new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent event) {
					_updateSelection();
				}

			});

		Set<TemplatesGroup> keys = _templatesMap.keySet();

		Stream<TemplatesGroup> stream = keys.stream();

		stream.map(
			templatesGroup -> templatesGroup.getModuleBuilder()
		).filter(
			builder -> builder instanceof LiferayModuleBuilder
		).forEach(
			builder -> _wizard.getSequence().addStepsForBuilder(builder, context, modulesProvider)
		);

		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		String groupId = propertiesComponent.getValue(_PROJECT_WIZARD_GROUP);

		if (groupId != null) {
			TemplatesGroup group = ContainerUtil.find(groups, group1 -> groupId.equals(group1.getId()));

			if (group != null) {
				_projectTypeList.setSelectedValue(group, true);
			}
		}

		if (_projectTypeList.getSelectedValue() == null) {
			_projectTypeList.setSelectedIndex(0);
		}

		_templatesList.restoreSelection();
	}

	@Override
	public void addExpertField(@NotNull String label, @NotNull JComponent field) {
	}

	@Override
	public void addExpertPanel(@NotNull JComponent panel) {
	}

	@Override
	public void addSettingsComponent(@NotNull JComponent component) {
	}

	@Override
	public void addSettingsField(@NotNull String label, @NotNull JComponent field) {
		LiferayProjectSettingsStep.addField(label, field, _headerPanel);
	}

	@Override
	public void dispose() {
		_lastSelectedGroup = null;
		_settingsStep = null;
		_templatesMap.clear();
		_builders.clear();
		_customSteps.clear();
	}

	@Override
	public void disposeUIResources() {
		Disposer.dispose(this);
	}

	@Override
	public JComponent getComponent() {
		return _mainPanel;
	}

	@Override
	public WizardContext getContext() {
		return _context;
	}

	@Override
	public String getHelpId() {
		if ((_getCustomStep() != null) && (_getCustomStep().getHelpId() != null)) {
			return _getCustomStep().getHelpId();
		}

		if (_context.isCreatingNewProject()) {
			return "Project_Category_and_Options";
		}

		return "Module_Category_and_Options";
	}

	@Override
	public JTextField getModuleNameField() {
		return null;
	}

	@Override
	public JComponent getPreferredFocusedComponent() {
		return _projectTypeList;
	}

	@Nullable
	public ProjectTemplate getSelectedTemplate() {
		if (_currentCard == _TEMPLATES_CARD) {
			return _templatesList.getSelectedTemplate();
		}

		return null;
	}

	public void onWizardFinished() throws CommitStepException {
		if (_isFrameworksMode()) {
			boolean ok = _frameworksPanel.downloadLibraries(_wizard.getContentComponent());

			if (!ok) {
				throw new CommitStepException(null);
			}
		}

		TemplatesGroup group = _getSelectedGroup();

		if (group != null) {
			ProjectCategoryUsagesCollector.projectTypeUsed(group.getId());
		}
	}

	public void projectTypeChanged() {
		TemplatesGroup group = _getSelectedGroup();

		if ((group == null) || (group == _lastSelectedGroup)) {
			return;
		}

		_lastSelectedGroup = group;

		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		propertiesComponent.setValue(_PROJECT_WIZARD_GROUP, group.getId());

		ModuleBuilder groupModuleBuilder = group.getModuleBuilder();

		_settingsStep = null;
		_headerPanel.removeAll();

		if ((groupModuleBuilder != null) && (groupModuleBuilder.getModuleType() != null)) {
			_settingsStep = groupModuleBuilder.modifyProjectTypeStep(this);
		}

		if ((groupModuleBuilder == null) || groupModuleBuilder.isTemplateBased()) {
			_showTemplates(group);
		}
		else if (!_showCustomOptions(groupModuleBuilder)) {
			List<FrameworkSupportInModuleProvider> providers = FrameworkSupportUtil.getProviders(groupModuleBuilder);

			ProjectCategory category = group.getProjectCategory();

			if (category != null) {
				List<FrameworkSupportInModuleProvider> filtered = ContainerUtil.filter(
					providers, provider -> _matchFramework(category, provider));

				Map<String, FrameworkSupportInModuleProvider> map = ContainerUtil.newMapFromValues(
					providers.iterator(), PROVIDER_STRING_CONVERTOR);

				Stream<FrameworkSupportInModuleProvider> stream = filtered.stream();

				Set<FrameworkSupportInModuleProvider> set = stream.flatMap(
					provider -> provider.getDependenciesFrameworkIds().stream()
				).map(
					depId -> map.get(depId.getFrameworkId())
				).filter(
					dependency -> dependency != null
				).collect(
					Collectors.toSet()
				);

				_frameworksPanel.setProviders(
					new ArrayList<>(set), new HashSet<>(Arrays.asList(category.getAssociatedFrameworkIds())),
					new HashSet<>(Arrays.asList(category.getPreselectedFrameworkIds())));
			}
			else {
				_frameworksPanel.setProviders(providers);
			}

			_getSelectedBuilder().addModuleConfigurationUpdater(_configurationUpdater);

			_showCard(_FRAMEWORKS_CARD);
		}

		_headerPanel.setVisible(_headerPanel.getComponentCount() > 0);

		List<JLabel> labels = UIUtil.findComponentsOfType(_headerPanel, JLabel.class);

		int width = 0;

		for (JLabel label : labels) {
			Dimension labelPreferredSize = label.getPreferredSize();

			int width1 = labelPreferredSize.width;

			width = Math.max(width, width1);
		}

		for (JLabel label : labels) {
			Dimension labelPreferredSize = label.getPreferredSize();

			label.setPreferredSize(new Dimension(width, labelPreferredSize.height));
		}

		_headerPanel.revalidate();
		_headerPanel.repaint();

		_updateSelection();
	}

	@Override
	public void updateDataModel() {
		ModuleBuilder builder = _getSelectedBuilder();

		StepSequence stepSequence = _wizard.getSequence();

		stepSequence.addStepsForBuilder(builder, _context, _modulesProvider);

		ModuleWizardStep step = _getCustomStep();

		if (step != null) {
			step.updateDataModel();
		}

		if (_settingsStep != null) {
			_settingsStep.updateDataModel();
		}
	}

	@Override
	public boolean validate() throws ConfigurationException {
		if ((_settingsStep != null) && !_settingsStep.validate()) {
			return false;
		}

		ModuleWizardStep step = _getCustomStep();

		if ((step != null) && !step.validate()) {
			return false;
		}

		if (_isFrameworksMode() && !_frameworksPanel.validate()) {
			return false;
		}

		return super.validate();
	}

	private static ModuleType _getModuleType(TemplatesGroup group) {
		ModuleBuilder moduleBuilder = group.getModuleBuilder();

		if (moduleBuilder == null) {
			return null;
		}

		return moduleBuilder.getModuleType();
	}

	private static boolean _matchFramework(
		ProjectCategory projectCategory, FrameworkSupportInModuleProvider framework) {

		FrameworkRole[] roles = framework.getRoles();

		if (roles.length == 0) {
			return true;
		}

		return ContainerUtil.intersects(
			Arrays.asList(roles), Arrays.asList(projectCategory.getAcceptableFrameworkRoles()));
	}

	private List<TemplatesGroup> _fillTemplatesMap() {
		_templatesMap.put(new TemplatesGroup(new LiferayModuleBuilder()), new ArrayList<>());
		//_templatesMap.put(new TemplatesGroup(new LiferayModuleFragmentBuilder()), new ArrayList<>());

		List<TemplatesGroup> groups = new ArrayList<>(_templatesMap.keySet());

		MultiMap<ModuleType, TemplatesGroup> moduleTypes = new MultiMap<>();

		for (TemplatesGroup group : groups) {
			ModuleType type = _getModuleType(group);

			moduleTypes.putValue(type, group);
		}

		return groups;
	}

	@Nullable
	private ModuleWizardStep _getCustomStep() {
		return _customSteps.get(_currentCard);
	}

	private ModuleBuilder _getSelectedBuilder() {
		ProjectTemplate template = getSelectedTemplate();

		if (template != null) {
			return _builders.get(template);
		}

		return _getSelectedGroup().getModuleBuilder();
	}

	private TemplatesGroup _getSelectedGroup() {
		return _projectTypeList.getSelectedValue();
	}

	private boolean _isFrameworksMode() {
		if (_FRAMEWORKS_CARD.equals(_currentCard) && _getSelectedBuilder().equals(_context.getProjectBuilder())) {
			return true;
		}

		return false;
	}

	private void _setTemplatesList(
		TemplatesGroup group, Collection<ProjectTemplate> templates, boolean preserveSelection) {

		List<ProjectTemplate> list = new ArrayList<>(templates);

		ModuleBuilder moduleBuilder = group.getModuleBuilder();

		if ((moduleBuilder != null) && !(moduleBuilder instanceof TemplateModuleBuilder)) {
			list.add(0, new BuilderBasedTemplate(moduleBuilder));
		}

		_templatesList.setTemplates(list, preserveSelection);
	}

	private void _showCard(String card) {
		((CardLayout)_optionsPanel.getLayout()).show(_optionsPanel, card);
		_currentCard = card;
	}

	private boolean _showCustomOptions(@NotNull ModuleBuilder builder) {
		String card = builder.getBuilderId();

		if (!_customSteps.containsKey(card)) {
			ModuleWizardStep step = builder.getCustomOptionsStep(_context, this);

			if (step == null) {
				return false;
			}

			step.updateStep();

			_customSteps.put(card, step);
			_optionsPanel.add(step.getComponent(), card);
		}

		_showCard(card);

		return true;
	}

	private void _showTemplates(TemplatesGroup group) {
		_setTemplatesList(group, _templatesMap.get(group), false);

		_showCard(_TEMPLATES_CARD);
	}

	private void _updateSelection() {
		ProjectTemplate template = getSelectedTemplate();

		if (template != null) {
			_context.setProjectTemplate(template);
		}

		ModuleBuilder builder = _getSelectedBuilder();

		_context.setProjectBuilder(builder);

		if (builder != null) {
			StepSequence stepSequence = _wizard.getSequence();

			stepSequence.setType(builder.getBuilderId());
		}

		_wizard.setDelegate(builder instanceof WizardDelegate ? (WizardDelegate)builder : null);
		_wizard.updateWizardButtons();
	}

	private static final String _FRAMEWORKS_CARD = "frameworks card";

	private static final String _PROJECT_WIZARD_GROUP = "project.wizard.group";

	private static final String _TEMPLATES_CARD = "templates card";

	private static final Logger _log = Logger.getInstance(LiferayProjectTypeStep.class);

	private Map<ProjectTemplate, ModuleBuilder> _builders = FactoryMap.createMap(
		key -> (ModuleBuilder)key.createModuleBuilder());
	private ModuleBuilder.ModuleConfigurationUpdater _configurationUpdater;
	private WizardContext _context;
	private String _currentCard;
	private Map<String, ModuleWizardStep> _customSteps = new THashMap<>();
	private JBLabel _frameworksLabel;
	private AddSupportForFrameworksPanel _frameworksPanel;
	private JPanel _frameworksPanelPlaceholder;
	private JPanel _headerPanel;
	private TemplatesGroup _lastSelectedGroup;
	private JPanel _mainPanel;
	private ModulesProvider _modulesProvider;
	private JPanel _optionsPanel;
	private JBList<TemplatesGroup> _projectTypeList;

	@Nullable
	private ModuleWizardStep _settingsStep;

	private LiferayProjectTemplateList _templatesList;
	private MultiMap<TemplatesGroup, ProjectTemplate> _templatesMap;
	private NewLiferayModuleWizard _wizard;

}