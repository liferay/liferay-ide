package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=DateUtil.compareTo method removed",
		"problem.section=#dateutil-compareto-method-removed",
		"problem.summary=DateUtil.compareTo method removed",
		"problem.tickets=LPS-59192",
		"implName=DateUtilApiChange"
	},
	service = FileMigrator.class
)
/**
 * @author Andy Wu
 */
public class DateUtilApiChange extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findMethodInvocations(null, "DateUtil",
				"compareTo", new String[] { "Date","Date","boolean" });
	}
}
