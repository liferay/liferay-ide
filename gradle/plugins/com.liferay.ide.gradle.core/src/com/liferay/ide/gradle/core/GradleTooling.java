package com.liferay.ide.gradle.core;

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 */
public class GradleTooling {

	public static <T> T getModel(
		Class<T> modelClass, File cacheDir, File projectDir ) throws Exception {

		T retval = null;

		final GradleConnector connector = GradleConnector.newConnector();
		connector.forProjectDirectory(projectDir);

		ProjectConnection connection = null;

		try {
			connection = connector.connect();

			ModelBuilder<T> modelBuilder =
				(ModelBuilder<T>) connection.model(modelClass);

			String modelLocation = Platform.getBundle( "com.liferay.blade.gradle.model" ).getLocation();

            File modelBundle = new File( modelLocation.replaceAll( "reference:", "" ).replaceAll( "file:", "" ) );

			String pluginLocation = Platform.getBundle( "com.liferay.blade.gradle.plugin" ).getLocation();

			File pluginBundle = new File( pluginLocation.replaceAll( "reference:", "" ).replaceAll( "file:", "" ) );

			final String initScriptTemplate =
                CoreUtil.readStreamToString( GradleTooling.class.getResourceAsStream( "init.gradle" ) );

			String initScriptContents =
				initScriptTemplate.replaceFirst(
				    "%model%", modelBundle.toString()).replaceFirst( "%plugin%", pluginBundle.toString() );

			File scriptFile = Files.createTempFile(
				"ide", "init.gradle").toFile();

			FileUtil.writeFileFromStream( scriptFile, new ByteArrayInputStream( initScriptContents.getBytes() ) );

			modelBuilder.withArguments("--init-script", scriptFile.getAbsolutePath());

			retval = modelBuilder.get();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		return retval;
	}

	public static Set<File> getOutputFiles(File cacheDir, File buildDir)
		throws Exception {

		final CustomModel model =
			getModel(CustomModel.class, cacheDir, buildDir);

		return model.getOutputFiles();
	}

}
