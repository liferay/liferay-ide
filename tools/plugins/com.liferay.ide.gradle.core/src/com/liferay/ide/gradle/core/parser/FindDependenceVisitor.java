package com.liferay.ide.gradle.core.parser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

/**
 * @author Lovett Li
 */
public class FindDependenceVisitor extends CodeVisitorSupport
{

    private int dependenceLineNum = -1;
    private int columnNum = -1;
    private List<GradleDependence> dependences = new ArrayList<>();

    @Override
    public void visitMethodCallExpression( MethodCallExpression call )
    {
        if( !( call.getMethodAsString().equals( "buildscript" ) ) )
        {
            if( call.getMethodAsString().equals( "dependencies" ) )
            {
                if( dependenceLineNum == -1 )
                {
                    dependenceLineNum = call.getLastLineNumber();
                }
            }
            super.visitMethodCallExpression( call );
        }

    }

    @Override
    public void visitClosureExpression( ClosureExpression expression )
    {
        if( dependenceLineNum != -1 && expression.getLineNumber() == expression.getLastLineNumber() )
        {
            columnNum = expression.getLastColumnNumber();
        }
        super.visitClosureExpression( expression );
    }

    @Override
    public void visitMapExpression( MapExpression expression )
    {
        List<MapEntryExpression> mapEntryExpressions = expression.getMapEntryExpressions();
        Map<String, String> dependenceMap = new HashMap<String, String>();

        for( MapEntryExpression mapEntryExpression : mapEntryExpressions )
        {
            String key = mapEntryExpression.getKeyExpression().getText();
            String value = mapEntryExpression.getValueExpression().getText();
            dependenceMap.put( key, value );
        }
        dependences.add( new GradleDependence( dependenceMap ) );

        super.visitMapExpression( expression );
    }

    public int getDependenceLineNum()
    {
        return dependenceLineNum;
    }

    public int getColumnNum()
    {
        return columnNum;
    }

    public List<GradleDependence> getDependences()
    {
        return dependences;
    }

}
