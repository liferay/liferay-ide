
package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.parser.FindDependenceVisitor;
import com.liferay.ide.gradle.core.parser.GradleDependence;
import com.liferay.ide.gradle.core.parser.GradleDependenceParser;

/**
 * @author Lovett Li
 */
public class GradleParseTests
{

    File testfile = new File( "generated/test/testbuild.gradle" );

    @Before
    public void setUp() throws IOException
    {
        File testdir = new File( "generated/test" );

        if( testdir.exists() )
        {
            FileUtils.deleteDirectory( testdir );
        }

        assertFalse( testdir.exists() );

        testfile.getParentFile().mkdir();

        assertTrue( testfile.createNewFile() );

    }

    @Test
    public void addDependenceSkipComment() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 27, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( outputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( testfile ) ) );
    }

    @Test
    public void addDependenceIntoEmptyBlock() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse2.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 24, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse2.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( outputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( testfile ) ) );
    }

    @Test
    public void addDependenceWithoutDendendenceBlock() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse3.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( -1, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse3.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( outputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( testfile ) ) );
    }

    @Test
    public void addDependenceInSameLine() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse4.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 23, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse4.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( outputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( testfile ) ) );
    }

    @Test
    public void addDependenceInClosureLine() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testParse5.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        FindDependenceVisitor visitor = gradleScriptASTParser.updateDependence(
            "\tcompile group: \"com.liferay\", name:\"com.liferay.bookmarks.api\", version:\"1.0.0\"" );

        int dependenceLineNum = visitor.getDependenceLineNum();

        assertEquals( 24, dependenceLineNum );

        Files.write( testfile.toPath(), gradleScriptASTParser.getScripts(), StandardCharsets.UTF_8 );

        final File outputFile = new File( "projects/testParseOutput/testParse5.gradle" );

        assertEquals(
            CoreUtil.readStreamToString( new FileInputStream( outputFile ) ),
            CoreUtil.readStreamToString( new FileInputStream( testfile ) ) );
    }

    @Test
    public void getAllDependence() throws IOException
    {
        final File inputFile = new File( "projects/testParseInput/testDependence.gradle" );

        GradleDependenceParser gradleScriptASTParser = new GradleDependenceParser( inputFile );

        List<GradleDependence> allDependence = gradleScriptASTParser.getAllDependence();

        assertEquals( 3, allDependence.size() );
    }

}
