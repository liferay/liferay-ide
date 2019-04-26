package blade.migrate.liferay70;

public class DDMTemplateLocalServiceUtilTest {

	public static void main(String[] args) {

		DDMTemplateLocalServiceUtil.updateTemplate(
				template.getTemplateId(), template.getClassPK(),
				template.getNameMap(), template.getDescriptionMap(),
				template.getType(), template.getMode(), template.getLanguage(),
				template.getScript(), template.isCacheable(),
				template.isSmallImage(), template.getSmallImageURL(), null,
				ServiceContextTestUtil.getServiceContext());
	}

}
