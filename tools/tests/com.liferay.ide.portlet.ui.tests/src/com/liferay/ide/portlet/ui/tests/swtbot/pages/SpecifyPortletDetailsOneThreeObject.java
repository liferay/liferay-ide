
package com.liferay.ide.portlet.ui.tests.swtbot.pages;

import com.liferay.ide.project.ui.tests.swtbot.CreateLiferayPortletWizard;
import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TablePageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

public class SpecifyPortletDetailsOneThreeObject<T extends SWTBot> extends WizardPageObject<T>
    implements CreateLiferayPortletWizard, ProjectWizard
{

    public CheckBoxPageObject<SWTBot> abstractModify;
    public CheckBoxPageObject<SWTBot> constrFromSuperClass;
    public CheckBoxPageObject<SWTBot> destory;
    public CheckBoxPageObject<SWTBot> doAbout;
    public CheckBoxPageObject<SWTBot> doConfig;
    public CheckBoxPageObject<SWTBot> doEdit;

    public CheckBoxPageObject<SWTBot> doEditDefaults;
    public CheckBoxPageObject<SWTBot> doEditGuest;

    public CheckBoxPageObject<SWTBot> doHelp;

    public CheckBoxPageObject<SWTBot> doPreview;

    public CheckBoxPageObject<SWTBot> doPrint;

    public CheckBoxPageObject<SWTBot> doView;

    public CheckBoxPageObject<SWTBot> finalModify;

    public CheckBoxPageObject<SWTBot> inherAbstractMethods;

    public CheckBoxPageObject<SWTBot> init;

    public TablePageObject<SWTBot> interfaces;

    public CheckBoxPageObject<SWTBot> processAction;
    public CheckBoxPageObject<SWTBot> publicModify;
    public CheckBoxPageObject<SWTBot> serveResource;

    public SpecifyPortletDetailsOneThreeObject( T bot )
    {
        this( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, INDEX_VALIDATION_PORTLET_MESSAGE3 );
    }

    public SpecifyPortletDetailsOneThreeObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText, finishButtonText, backButtonText, nextButtonText, validationMessageIndex );
        publicModify = new CheckBoxPageObject<SWTBot>( bot, LABEL_PUBLIC );
        abstractModify = new CheckBoxPageObject<SWTBot>( bot, LABEL_ABSTRACT );
        finalModify = new CheckBoxPageObject<SWTBot>( bot, LABEL_FINAL );
        interfaces = new TablePageObject<SWTBot>( bot, 0 );
        constrFromSuperClass = new CheckBoxPageObject<SWTBot>( bot, LABEL_CONSTRUCTORS_FROM_SUPERCLASS );
        inherAbstractMethods = new CheckBoxPageObject<SWTBot>( bot, LABEL_INHERITED_ABSTRACT_METHODS );

        init = new CheckBoxPageObject<SWTBot>( bot, LABEL_INIT );
        destory = new CheckBoxPageObject<SWTBot>( bot, LABEL_DESTORY );
        doView = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_VIEW );
        doEdit = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_EDIT );
        doHelp = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_HELP );
        doAbout = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_ABOUT );
        doConfig = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_CONFIG );
        doEditDefaults = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_EDIT_DEFAULTS );
        doEditGuest = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_EDIT_GUEST );
        doPreview = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_PREEVIEW );
        doPrint = new CheckBoxPageObject<SWTBot>( bot, LABEL_DO_PRINT );
        processAction = new CheckBoxPageObject<SWTBot>( bot, LABEL_PROCESS_ACTION );
        serveResource = new CheckBoxPageObject<SWTBot>( bot, LABEL_SERVE_RESOURCE );
    }

    public CheckBoxPageObject<SWTBot> getAbstractModify()
    {
        return abstractModify;
    }

    public CheckBoxPageObject<SWTBot> getConstrFromSuperClass()
    {
        return constrFromSuperClass;
    }

    public CheckBoxPageObject<SWTBot> getDestory()
    {
        return destory;
    }

    public CheckBoxPageObject<SWTBot> getDoAbout()
    {
        return doAbout;
    }

    public CheckBoxPageObject<SWTBot> getDoConfig()
    {
        return doConfig;
    }

    public CheckBoxPageObject<SWTBot> getDoEdit()
    {
        return doEdit;
    }

    public CheckBoxPageObject<SWTBot> getDoEditDefaults()
    {
        return doEditDefaults;
    }

    public CheckBoxPageObject<SWTBot> getDoEditGuest()
    {
        return doEditGuest;
    }

    public CheckBoxPageObject<SWTBot> getDoHelp()
    {
        return doHelp;
    }

    public CheckBoxPageObject<SWTBot> getDoPreview()
    {
        return doPreview;
    }

    public CheckBoxPageObject<SWTBot> getDoPrint()
    {
        return doPrint;
    }

    public CheckBoxPageObject<SWTBot> getDoView()
    {
        return doView;
    }

    public CheckBoxPageObject<SWTBot> getFinalModify()
    {
        return finalModify;
    }

    public CheckBoxPageObject<SWTBot> getInherAbstractMethods()
    {
        return inherAbstractMethods;
    }

    public CheckBoxPageObject<SWTBot> getInit()
    {
        return init;
    }

    public TablePageObject<SWTBot> getInterfaces()
    {
        return interfaces;
    }

    public CheckBoxPageObject<SWTBot> getProcessAction()
    {
        return processAction;
    }

    public CheckBoxPageObject<SWTBot> getPublicModify()
    {
        return publicModify;
    }

    public CheckBoxPageObject<SWTBot> getServeResource()
    {
        return serveResource;
    }

}
