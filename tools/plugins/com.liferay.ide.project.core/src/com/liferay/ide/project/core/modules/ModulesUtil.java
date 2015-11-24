package com.liferay.ide.project.core.modules;

import com.liferay.blade.api.ProjectTemplate;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Gregory Amerson
 */
public class ModulesUtil
{
    public static ProjectTemplate[] getProjectTemplates()
    {
        final ServiceTracker<ProjectTemplate, ProjectTemplate> tracker = new ServiceTracker<>(
            FrameworkUtil.getBundle( ModulesUtil.class ).getBundleContext(), ProjectTemplate.class, null );

        tracker.open();

        final ProjectTemplate[] templates = tracker.getServices( new ProjectTemplate[0] );

        return templates != null ? templates : new ProjectTemplate[0];
    }

}
