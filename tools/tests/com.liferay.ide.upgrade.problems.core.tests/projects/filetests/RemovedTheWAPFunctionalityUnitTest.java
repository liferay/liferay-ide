package blade.migrate.liferay70;
public class RemovedTheWAPFunctionalityUnitTest {
	public static void main(String[] args) 
	{
		try
		{
			LayoutLocalServiceUtil.updateLookAndFeel();
			LayoutRevisionLocalServiceUtil.addLayoutRevision();
			LayoutRevisionLocalServiceUtil.updateLayoutRevision();
			LayoutRevisionServiceUtil.addLayoutRevision();
			LayoutServiceUtil.updateLookAndFeel();
			LayoutSetLocalServiceUtil.updateLookAndFeel();
			LayoutSetServiceUtil.updateLookAndFeel();
			ThemeLocalServiceUtil.getColorScheme();
			ThemeLocalServiceUtil.getControlPanelThemes();
			ThemeLocalServiceUtil.getPageThemes();
			ThemeLocalServiceUtil.getTheme();
		}
		catch (Exception e)
		{
		}
}