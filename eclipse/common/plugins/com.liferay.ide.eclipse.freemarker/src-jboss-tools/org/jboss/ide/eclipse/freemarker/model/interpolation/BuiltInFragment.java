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
package org.jboss.ide.eclipse.freemarker.model.interpolation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class BuiltInFragment extends AbstractFragment {

	private static final Map STRING_BUILT_INS = new HashMap();
	private static final Map NUMBER_BUILT_INS = new HashMap();
	private static final Map DATE_BUILT_INS = new HashMap();
	private static final Map LIST_BUILT_INS = new HashMap();
	private static final Map MAP_BUILT_INS = new HashMap();
	private static final Map OBJECT_BUILT_INS = new HashMap();
	static {
		addToMap (OBJECT_BUILT_INS,
				new Object[]{
					"is_collection", Boolean.class, null, //$NON-NLS-1$
					"is_macro", Boolean.class, null, //$NON-NLS-1$
					"is_transform", Boolean.class, null, //$NON-NLS-1$
					"string", String.class, null,			 //$NON-NLS-1$
					"namespace", String.class, null, //$NON-NLS-1$
					"default", String.class, new String[]{"value "}, //$NON-NLS-1$ //$NON-NLS-2$
					"is_number", Boolean.class, null, //$NON-NLS-1$
					"is_directive", String.class, null, //$NON-NLS-1$
					"is_boolean", Boolean.class, null, //$NON-NLS-1$
					"is_hash_ex", Boolean.class, null, //$NON-NLS-1$
					"is_enumerable", Boolean.class, null, //$NON-NLS-1$
					"is_date", Boolean.class, null, //$NON-NLS-1$
					"is_node", Boolean.class, null, //$NON-NLS-1$
					"exists", Boolean.class, null, //$NON-NLS-1$
					"is_indexable", Boolean.class, null, //$NON-NLS-1$
					"is_string", Boolean.class, null, //$NON-NLS-1$
					"is_hash", Boolean.class, null, //$NON-NLS-1$
					"is_sequence", Boolean.class, null, //$NON-NLS-1$
					"if_exists", Boolean.class, null, //$NON-NLS-1$
					"has_content", Boolean.class, null, //$NON-NLS-1$
					"is_method", Boolean.class, null, //$NON-NLS-1$
					"round", String.class, null, //$NON-NLS-1$
					"floor", String.class, null, //$NON-NLS-1$
					"ceiling", String.class, null, //$NON-NLS-1$
		});
		addToMap (STRING_BUILT_INS,
				new Object[]{
					"interpret", String.class, null, //$NON-NLS-1$
					"matches", Boolean.class, new String[]{"expression"}, //$NON-NLS-1$ //$NON-NLS-2$
					"html", String.class, null, //$NON-NLS-1$
					"index_of", Number.class, new String[]{"substr"}, //$NON-NLS-1$ //$NON-NLS-2$
					"right_pad", String.class, new String[]{"padAmount"}, //$NON-NLS-1$ //$NON-NLS-2$
					"xml", String.class, null, //$NON-NLS-1$
					"web_safe", String.class, null, //$NON-NLS-1$
					"eval", null, null, //$NON-NLS-1$
					"size", Number.class, null, //$NON-NLS-1$
					"cap_first", String.class, null,					 //$NON-NLS-1$
					"j_string", String.class, null, //$NON-NLS-1$
					"first", String.class, null, //$NON-NLS-1$
					"split", Collection.class, null, //$NON-NLS-1$
					"upper_case", String.class, null, //$NON-NLS-1$
					"last_index_of", String.class, null, //$NON-NLS-1$
					"long", Number.class, null, //$NON-NLS-1$
					"last", String.class, null, //$NON-NLS-1$
					"starts_with", Boolean.class, null, //$NON-NLS-1$
					"capitalize", String.class, null, //$NON-NLS-1$
					"short", String.class, null, //$NON-NLS-1$
					"ends_with", String.class, null, //$NON-NLS-1$
					"chunk", Collection.class, null, //$NON-NLS-1$
					"byte", String.class, null, //$NON-NLS-1$
					"trim", String.class, null, //$NON-NLS-1$
					"c", String.class, null, //$NON-NLS-1$
					"chop_linebreak", String.class, null, //$NON-NLS-1$
					"double", Number.class, null, //$NON-NLS-1$
					"url", String.class, null, //$NON-NLS-1$
					"replace", String.class, null, //$NON-NLS-1$
					"uncap_first", String.class, null, //$NON-NLS-1$
					"contains", Boolean.class, null, //$NON-NLS-1$
					"left_pad", String.class, new String[]{"param"}, //$NON-NLS-1$ //$NON-NLS-2$
					"length", Number.class, null, //$NON-NLS-1$
					"rtf", String.class, null, //$NON-NLS-1$
					"lower_case", String.class, null, //$NON-NLS-1$
					"js_string", String.class, null, //$NON-NLS-1$
					"word_list", String.class, null, //$NON-NLS-1$
		});
		addToMap (NUMBER_BUILT_INS,
				new Object[]{
					"string.currency", Boolean.class, new String[]{"format"}, //$NON-NLS-1$ //$NON-NLS-2$
					"string", String.class, null,			 //$NON-NLS-1$
					"number", Number.class, null, //$NON-NLS-1$
					"new", String.class, null, //$NON-NLS-1$
					"long", Number.class, null, //$NON-NLS-1$
					"short", String.class, null, //$NON-NLS-1$
					"double", Number.class, null, //$NON-NLS-1$
					"int", Number.class, null, //$NON-NLS-1$
		});
		addToMap (DATE_BUILT_INS,
				new Object[]{
					"date", Date.class, null, //$NON-NLS-1$
					"time", Date.class, null, //$NON-NLS-1$
					"datetime", Date.class, null, //$NON-NLS-1$
					"string", String.class, new String[]{"format"}, //$NON-NLS-1$ //$NON-NLS-2$
					"string.short", String.class, null, //$NON-NLS-1$
					"string.medium", String.class, null, //$NON-NLS-1$
					"string.long", String.class, null, //$NON-NLS-1$
		});
		addToMap (LIST_BUILT_INS,
				new Object[]{
					"seq_contains", Boolean.class, new String[]{"value"}, //$NON-NLS-1$ //$NON-NLS-2$
					"reverse", Collection.class, null, //$NON-NLS-1$
					"size", Number.class, null, //$NON-NLS-1$
					"last", Object.class, null, //$NON-NLS-1$
		});
		addToMap (MAP_BUILT_INS,
				new Object[]{
					"keys", Collection.class, null, //$NON-NLS-1$
		});
	}
	
	private static void addToMap (Map map, Object[] arr) {
		int i=0; 
		while (i<arr.length) {
			map.put(
					(String) arr[i++],
					new ParameterSet (
						(Class) arr[i++],
						(String[]) arr[i++]));
		}
	}
	
	public BuiltInFragment(int offset, String content) {
		super(offset, content);
	}

	private String subContent = null;
	public Class getReturnClass (Class parentClass, List fragments, Map context, IResource resource, IProject project) {
		if (null == subContent) {
			subContent = getContent();
			int index = subContent.indexOf("("); //$NON-NLS-1$
			if (index > 0) subContent = subContent.substring(0, index);
		}
		ParameterSet parameterSet = (ParameterSet) STRING_BUILT_INS.get(subContent);
		if (null == parameterSet) parameterSet = (ParameterSet) NUMBER_BUILT_INS.get(subContent);
		if (null == parameterSet) parameterSet = (ParameterSet) DATE_BUILT_INS.get(subContent);
		if (null == parameterSet) parameterSet = (ParameterSet) LIST_BUILT_INS.get(subContent);
		if (null == parameterSet) parameterSet = (ParameterSet) MAP_BUILT_INS.get(subContent);
		if (null == parameterSet) parameterSet = (ParameterSet) OBJECT_BUILT_INS.get(subContent);
		if (null != parameterSet) {
			return parameterSet.getReturnClass();
		}
		else return null;
	}

	public ICompletionProposal[] getCompletionProposals (int subOffset, int offset, Class parentClass,
			List fragments, ISourceViewer sourceViewer, Map context, IResource file, IProject project) {
		if (instanceOf(parentClass, String.class)) {
			return getCompletionProposals(subOffset, offset, STRING_BUILT_INS);
		}
		else if (instanceOf(parentClass, Number.class)) {
			return getCompletionProposals(subOffset, offset, NUMBER_BUILT_INS);
		}
		else if (instanceOf(parentClass, Date.class)) {
			return getCompletionProposals(subOffset, offset, DATE_BUILT_INS);
		}
		else if (instanceOf(parentClass, Collection.class) || instanceOf(parentClass, List.class)) {
			return getCompletionProposals(subOffset, offset, LIST_BUILT_INS);
		}
		else if (instanceOf(parentClass, Map.class)) {
			return getCompletionProposals(subOffset, offset, MAP_BUILT_INS);
		}
		else return getCompletionProposals(subOffset, offset, OBJECT_BUILT_INS);
	}

	private ICompletionProposal[] getCompletionProposals(int subOffset, int offset, Map values) {
		if (offset == 0) return null;
		String prefix = getContent().substring(0, subOffset-1);
		List proposals = new ArrayList();
		for (Iterator i=values.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			String key = (String) entry.getKey();
			ParameterSet params = (ParameterSet) entry.getValue();
			if (key.startsWith(prefix)) {
				proposals.add(getCompletionProposal(key, params, offset, subOffset));
			}
		}
		if (!values.equals(OBJECT_BUILT_INS)) {
			values = OBJECT_BUILT_INS;
			for (Iterator i=values.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = (String) entry.getKey();
				ParameterSet params = (ParameterSet) entry.getValue();
				if (key.startsWith(prefix)) {
					proposals.add(getCompletionProposal(key, params, offset, subOffset));
				}
			}
		}
		return completionProposals(proposals);
	}

	private ICompletionProposal getCompletionProposal (String key, ParameterSet params, int offset, int subOffset) {
		if (null == params.getParameters() || params.getParameters().length == 0) {
			return getCompletionProposal(offset, subOffset-1,
					key, getContent());
		}
		else {
			String replacementString = key + "()"; //$NON-NLS-1$
			return new CompletionProposal (
					replacementString, offset-subOffset+1,
					getContent().length(), replacementString.length()-1);
		}
	}

	private String getReplacementString (String key, ParameterSet values) {
		if (null == values.getParameters()) {
			return key;
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append(key);
			sb.append("("); //$NON-NLS-1$
//			for (int i=0; i<values.getParameters().length; i++) {
//				if (i > 0) sb.append(", ");
//				sb.append(values.getParameters()[i]);
//			}
			sb.append(")"); //$NON-NLS-1$
			return sb.toString();
		}
	}
}