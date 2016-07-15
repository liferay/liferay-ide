
package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.parser.FindDependenciesVisitor;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Lovett Li
 */
public class GradleParseTests
{

    private static final File outputfile = new File( "generated/test/testbuild.gradle" );

    @Before
    public void setUp() throws IOException
    {
        if( outputfile.exists() )
        {
            assertTrue( outputfile.delete() );
        }

        outputfile.getParentFile().mkdirs();

        assertTrue( outputfile.createNewFile() );
    }

    @Test
    public void addDependenceSkipComment() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        FindDependenciesVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 27, dependenceLineNum );

        Files.write( outputfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File expectedOutputFile = new File( "projects/testParseOutput/testParse.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( expectedOutputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( outputfile ) ) );
    }

    @Test
    public void addDependenceIntoEmptyBlock() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse2.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        FindDependenciesVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 24, dependenceLineNum );

        Files.write( outputfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File expectedOutputFile = new File( "projects/testParseOutput/testParse2.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( expectedOutputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( outputfile ) ) );
    }

    @Test
    public void addDependenceWithoutDendendenceBlock() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse3.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        FindDependenciesVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( -1, dependenceLineNum );

        Files.write( outputfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File expectedOutputFile = new File( "projects/testParseOutput/testParse3.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( expectedOutputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( outputfile ) ) );
    }

    @Test
    public void addDependenceInSameLine() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse4.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        FindDependenciesVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 23, dependenceLineNum );

        Files.write( outputfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse4.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( outputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( outputfile ) ) );
    }

    @Test
    public void addDependenceInClosureLine() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse5.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        FindDependenciesVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 24, dependenceLineNum );

        Files.write( outputfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse5.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( outputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( outputfile ) ) );
    }

    @Test
    public void getAllDependencies() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testDependencies.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        List<GradleDependency> allDependence = gradleScriptASTParser.getAllDependencies();

        assertEquals( 3, allDependence.size() );
    }

    @Test
    public void getAllDependenciesShortFormat() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testDependenciesShortFormat.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        List<GradleDependency> allDependence = gradleScriptASTParser.getAllDependencies();

        assertEquals( 3, allDependence.size() );
    }

    @Test
    public void getAllDependenciesShortFormatAndLongFormat() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testDependenciesShortFormatAndLongFormat.gradle" );

        GradleDependencyUpdater gradleScriptASTParser = new GradleDependencyUpdater( inputFile );

        List<GradleDependency> allDependence = gradleScriptASTParser.getAllDependencies();

        assertEquals( 3, allDependence.size() );
    }

}
