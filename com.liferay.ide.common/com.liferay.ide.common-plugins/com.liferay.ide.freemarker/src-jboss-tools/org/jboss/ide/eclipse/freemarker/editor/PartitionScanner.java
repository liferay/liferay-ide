/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.editor;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.editor.rules.DirectiveRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.DirectiveRuleEnd;
import org.jboss.ide.eclipse.freemarker.editor.rules.GenericDirectiveRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.GenericDirectiveRuleEnd;
import org.jboss.ide.eclipse.freemarker.editor.rules.InterpolationRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.MacroInstanceRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.MacroInstanceRuleEnd;
import org.jboss.ide.eclipse.freemarker.editor.rules.XmlRule;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class PartitionScanner extends RuleBasedPartitionScanner {

	public final static String FTL_COMMENT = "__ftl_comment"; //$NON-NLS-1$
	public final static String FTL_INCLUDE = "__ftl_include"; //$NON-NLS-1$
	public final static String FTL_IMPORT = "__ftl_import"; //$NON-NLS-1$
	public final static String FTL_ASSIGN = "__ftl_assign"; //$NON-NLS-1$
	public final static String FTL_ASSIGN_END = "__ftl_assign_end"; //$NON-NLS-1$
	public final static String FTL_LOCAL = "__ftl_local"; //$NON-NLS-1$
	public final static String FTL_LOCAL_END = "__ftl_local_end"; //$NON-NLS-1$
	public final static String FTL_GLOBAL = "__ftl_global"; //$NON-NLS-1$
	public final static String FTL_GLOBAL_END = "__ftl_global_end"; //$NON-NLS-1$
	public final static String FTL_BREAK = "__ftl_break"; //$NON-NLS-1$
	public final static String FTL_NESTED = "__ftl_nested"; //$NON-NLS-1$
	public final static String FTL_RETURN = "__ftl_return"; //$NON-NLS-1$
	public final static String FTL_STOP = "__ftl_stop"; //$NON-NLS-1$
	public final static String FTL_FTL_DIRECTIVE = "__ftl_ftl_directive"; //$NON-NLS-1$
	public final static String FTL_FUNCTION_DIRECTIVE_START = "__ftl_function_directive_start"; //$NON-NLS-1$
	public final static String FTL_FUNCTION_DIRECTIVE_END = "__ftl_function_directive_end"; //$NON-NLS-1$
	public final static String FTL_LIST_DIRECTIVE_START = "__ftl_list_directive_start"; //$NON-NLS-1$
	public final static String FTL_LIST_DIRECTIVE_END = "__ftl_list_directive_end"; //$NON-NLS-1$
	public final static String FTL_MACRO_DIRECTIVE_START = "__ftl_macro_directive_start"; //$NON-NLS-1$
	public final static String FTL_MACRO_DIRECTIVE_END = "__ftl_macro_directive_end"; //$NON-NLS-1$
	public final static String FTL_MACRO_INSTANCE_START = "__ftl_macro_instance_start"; //$NON-NLS-1$
	public final static String FTL_MACRO_INSTANCE_END = "__ftl_macro_instance_end"; //$NON-NLS-1$
	public final static String FTL_SWITCH_DIRECTIVE_START = "__ftl_switch_directive_start"; //$NON-NLS-1$
	public final static String FTL_SWITCH_DIRECTIVE_END = "__ftl_switch_directive_end"; //$NON-NLS-1$
	public final static String FTL_CASE_DIRECTIVE_START = "__ftl_case_directive_start"; //$NON-NLS-1$
	public final static String FTL_CASE_DEFAULT_START = "__ftl_case_default_start"; //$NON-NLS-1$
	public final static String FTL_IF_DIRECTIVE_START = "__ftl_if_directive_start"; //$NON-NLS-1$
	public final static String FTL_IF_DIRECTIVE_END = "__ftl_if_directive_end"; //$NON-NLS-1$
	public final static String FTL_IF_ELSE_DIRECTIVE = "__ftl_if_else_directive"; //$NON-NLS-1$
	public final static String FTL_ELSE_IF_DIRECTIVE = "__ftl_else_if_directive"; //$NON-NLS-1$
	public final static String FTL_INTERPOLATION = "__ftl_interpolation"; //$NON-NLS-1$
	public final static String FTL_DIRECTIVE = "__ftl_directive"; //$NON-NLS-1$
	public final static String FTL_DIRECTIVE_END = "__ftl_directive_end"; //$NON-NLS-1$
    public final static String XML_TAG = "__xml_tag"; //$NON-NLS-1$
    public final static String XML_COMMENT = "__xml_comment"; //$NON-NLS-1$
    public final static String STRING = "__string"; //$NON-NLS-1$
   
    public final static String[] DIRECTIVES = {
    	FTL_INCLUDE, FTL_IMPORT, FTL_ASSIGN, FTL_ASSIGN_END, FTL_LOCAL, FTL_LOCAL_END, FTL_GLOBAL,
    	FTL_GLOBAL_END, FTL_BREAK, FTL_NESTED, FTL_RETURN, FTL_STOP, FTL_LIST_DIRECTIVE_START,
    	FTL_LIST_DIRECTIVE_END, FTL_IF_DIRECTIVE_START, FTL_ELSE_IF_DIRECTIVE, FTL_IF_ELSE_DIRECTIVE,
    	FTL_IF_DIRECTIVE_END, FTL_SWITCH_DIRECTIVE_START, FTL_SWITCH_DIRECTIVE_END, FTL_CASE_DIRECTIVE_START,
    	FTL_CASE_DEFAULT_START, FTL_MACRO_DIRECTIVE_START, FTL_MACRO_DIRECTIVE_END, FTL_MACRO_INSTANCE_START,
    	FTL_MACRO_INSTANCE_END, FTL_FTL_DIRECTIVE, FTL_FUNCTION_DIRECTIVE_START, FTL_FUNCTION_DIRECTIVE_END};

    /**
     * The array of partitions used.
     */
    public static String[] PARTITIONS = {
        IDocument.DEFAULT_CONTENT_TYPE,
        FTL_COMMENT,
        XML_TAG,
        XML_COMMENT,
        FTL_INTERPOLATION,
        STRING,
        FTL_DIRECTIVE,
        FTL_DIRECTIVE_END,
    };
   
    static {
    	String[] pSub = new String[PARTITIONS.length + DIRECTIVES.length];
    	int i=0;
    	for (int j=0; j<DIRECTIVES.length; j++) {
    		pSub[i++] = DIRECTIVES[j];
    	}
    	for (int j=0; j<PARTITIONS.length; j++) {
    		pSub[i++] = PARTITIONS[j];
    	}
    	PARTITIONS = pSub;
    }

    /**
     * Creates a new partition scanner.
     */
	public PartitionScanner() {
		List rules = new ArrayList();

		IToken ftlComment = new Token(FTL_COMMENT);

        rules.add(new MultiLineRule("<!--", "-->", new Token(XML_COMMENT))); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new MultiLineRule("<#--", "-->", ftlComment)); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new MultiLineRule("[#--", "--]", ftlComment)); //$NON-NLS-1$ //$NON-NLS-2$

        rules.add(new DirectiveRule("ftl", new Token(FTL_FTL_DIRECTIVE))); //$NON-NLS-1$
        rules.add(new DirectiveRule("if", new Token(FTL_IF_DIRECTIVE_START))); //$NON-NLS-1$
        rules.add(new DirectiveRule("elseif", new Token(FTL_ELSE_IF_DIRECTIVE))); //$NON-NLS-1$
        rules.add(new DirectiveRule("else", new Token(FTL_IF_ELSE_DIRECTIVE), true)); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("if", new Token(FTL_IF_DIRECTIVE_END))); //$NON-NLS-1$

        rules.add(new DirectiveRule("function", new Token(FTL_FUNCTION_DIRECTIVE_START))); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("function", new Token(FTL_FUNCTION_DIRECTIVE_END))); //$NON-NLS-1$

        rules.add(new DirectiveRule("list", new Token(FTL_LIST_DIRECTIVE_START))); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("list", new Token(FTL_LIST_DIRECTIVE_END))); //$NON-NLS-1$

        rules.add(new DirectiveRule("macro", new Token(FTL_MACRO_DIRECTIVE_START))); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("macro", new Token(FTL_MACRO_DIRECTIVE_END))); //$NON-NLS-1$
        rules.add(new MacroInstanceRule(new Token(FTL_MACRO_INSTANCE_START)));
        rules.add(new MacroInstanceRuleEnd(new Token(FTL_MACRO_INSTANCE_END)));

        rules.add(new DirectiveRule("switch", new Token(FTL_SWITCH_DIRECTIVE_START))); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("switch", new Token(FTL_SWITCH_DIRECTIVE_END))); //$NON-NLS-1$
        rules.add(new DirectiveRule("case", new Token(FTL_CASE_DIRECTIVE_START))); //$NON-NLS-1$
        rules.add(new DirectiveRule("default", new Token(FTL_CASE_DEFAULT_START))); //$NON-NLS-1$
        
        rules.add(new DirectiveRule("assign", new Token(FTL_ASSIGN))); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("assign", new Token(FTL_ASSIGN_END))); //$NON-NLS-1$
        rules.add(new DirectiveRule("local", new Token(FTL_LOCAL))); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("local", new Token(FTL_LOCAL_END))); //$NON-NLS-1$
        rules.add(new DirectiveRule("global", new Token(FTL_GLOBAL))); //$NON-NLS-1$
        rules.add(new DirectiveRuleEnd("global", new Token(FTL_GLOBAL_END))); //$NON-NLS-1$

        rules.add(new DirectiveRule("include", new Token(FTL_INCLUDE))); //$NON-NLS-1$
        rules.add(new DirectiveRule("import", new Token(FTL_IMPORT))); //$NON-NLS-1$
        rules.add(new DirectiveRule("break", new Token(FTL_BREAK))); //$NON-NLS-1$
        rules.add(new DirectiveRule("stop", new Token(FTL_STOP))); //$NON-NLS-1$
        rules.add(new DirectiveRule("nested", new Token(FTL_NESTED))); //$NON-NLS-1$
        rules.add(new DirectiveRule("return", new Token(FTL_RETURN))); //$NON-NLS-1$
        
        rules.add(new GenericDirectiveRule(new Token(FTL_DIRECTIVE)));
        rules.add(new GenericDirectiveRuleEnd(new Token(FTL_DIRECTIVE_END)));

        rules.add(new InterpolationRule('$', new Token(FTL_INTERPOLATION)));
        rules.add(new InterpolationRule('#', new Token(FTL_INTERPOLATION)));

        rules.add(new XmlRule(new Token(XML_TAG)));
        
		IPredicateRule[] result= new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}