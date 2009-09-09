/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.tag.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.core.module.tag.service.TagService;

/**
 * Ajax autocompletion field for tags
 * 
 * @author Carsten Hufe
 */
public class TagField<T extends BaseTagEntity<?>> extends AutoCompleteTextField<String> implements IFormModelUpdateListener {
	private static final long serialVersionUID = 1L;
	private final TagService<T> tagService;
	private IModel<List<T>> originalTagsModel = null;
	
	private TagField(final String id, final String tags, final TagService<T> tagService) {
		super(id, Model.of(tags));
		add(CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS));
		this.tagService = tagService;
	}
	
	public TagField(final String id, final IModel<List<T>> tags, final TagService<T> tagService) {
		super(id, Model.of(createModelString(tags.getObject())));
		originalTagsModel = tags;
		this.tagService = tagService;
	}

	private static <T extends BaseTagEntity<?>> String createModelString(final List<T> tags) {
		StringBuilder concat = new StringBuilder();
		if (tags != null) {
			for (final T tag : tags) {
				concat.append(tag.getTagname()).append(TagConstants.TAG_DEFAULT_SEPERATOR);
			}
		}
		return concat.toString();
	}

	@Override
	protected Iterator<String> getChoices(final String input) {

		if (Strings.isEmpty(input)) {
			return new ArrayList<String>().iterator();
		}

		String lastWord = getLastWord(input);
		final List<String> choices = new ArrayList<String>(10);
		if (isSearchable(input, lastWord)) {
			final String leadingTags = getLeadingTags(input, lastWord);
			final List<T> matchingCompletionTags = tagService.findTagsStartingWith(lastWord);
			for (final T matchingCompletionTag : matchingCompletionTags) {
				choices.add(leadingTags + matchingCompletionTag.getTagname());
			}
		}
		return choices.iterator();

	}

	private String getLeadingTags(final String input, String lastWord) {
		final String prefix = input.substring(0, input.length() - lastWord.length());
		return prefix;
	}

	private boolean isSearchable(final String input, String lastWord) {
		return lastWord.length() > 1 && input.endsWith(lastWord);
	}

	private String getLastWord(final String input) {
		final StringTokenizer tokenizer = new StringTokenizer(input, TagConstants.TAG_SEPERATORS, false);
		String lastToken = "";
		while (tokenizer.hasMoreTokens()) {
			lastToken = tokenizer.nextToken();
		}
		return lastToken;
	}

	/**
	 * Returns the tags and stores it
	 */
	private List<T> getTagsAndStore() {

		final List<T> back = new ArrayList<T>();
		final StringTokenizer tokenizer = new StringTokenizer(getValue(), TagConstants.TAG_SEPERATORS, false);
		while (tokenizer.hasMoreTokens()) {
			final String tagName = tokenizer.nextToken().trim();
			// save only token with 3 letters or more!
			if (!Strings.isEmpty(tagName) && tagName.length() > 2) {
				T tag = tagService.findByIdAndCreateIfNotExists(tagName);
				back.add(tag);
			}
		}
		return back;
	}

	@Override
	public void updateModel() {
		super.updateModel();
		List<T> tagsAndStore = getTagsAndStore();
		originalTagsModel.setObject(tagsAndStore);
	}
}
