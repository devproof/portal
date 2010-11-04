/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.tag.component;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.entity.AbstractTag;
import org.devproof.portal.core.module.tag.service.TagService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Ajax autocompletion field for tags
 *
 * @author Carsten Hufe
 */
public class TagField<T extends AbstractTag<?>> extends AutoCompleteTextField<String> implements IFormModelUpdateListener {
    private static final long serialVersionUID = 1L;
    private TagService<T> tagService;
    private IModel<List<T>> originalTagsModel = null;

    public TagField(String id, IModel<List<T>> tags, TagService<T> tagService) {
        super(id, Model.of(createModelString(tags.getObject())));
        add(CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS));
        originalTagsModel = tags;
        this.tagService = tagService;
    }

    private static <T extends AbstractTag<?>> String createModelString(List<T> tags) {
        StringBuilder concat = new StringBuilder();
        if (tags != null) {
            for (T tag : tags) {
                concat.append(tag.getTagname()).append(TagConstants.TAG_DEFAULT_SEPERATOR);
            }
        }
        return concat.toString();
    }

    @Override
    protected Iterator<String> getChoices(String input) {

        if (Strings.isEmpty(input)) {
            return new ArrayList<String>().iterator();
        }

        String lastWord = getLastWord(input);
        List<String> choices = new ArrayList<String>(10);
        if (isSearchable(input, lastWord)) {
            String leadingTags = getLeadingTags(input, lastWord);
            List<T> matchingCompletionTags = tagService.findTagsStartingWith(lastWord);
            for (T matchingCompletionTag : matchingCompletionTags) {
                choices.add(leadingTags + matchingCompletionTag.getTagname());
            }
        }
        return choices.iterator();

    }

    private String getLeadingTags(String input, String lastWord) {
        return input.substring(0, input.length() - lastWord.length());
    }

    private boolean isSearchable(String input, String lastWord) {
        return lastWord.length() > 1 && input.endsWith(lastWord);
    }

    private String getLastWord(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input, TagConstants.TAG_SEPERATORS, false);
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
        List<T> back = new ArrayList<T>();
        StringTokenizer tokenizer = new StringTokenizer(getValue(), TagConstants.TAG_SEPERATORS, false);
        while (tokenizer.hasMoreTokens()) {
            String tagName = tokenizer.nextToken().trim();
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
