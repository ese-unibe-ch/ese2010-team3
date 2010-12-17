package models.database.HotDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Tag;
import models.database.ITagDatabase;
import models.helpers.ICleanup;

public class HotTagDatabase implements ITagDatabase, ICleanup<Tag> {
	private final Map<String, Tag> tags = new HashMap<String, Tag>();

	public Collection<Tag> all() {
		return this.tags.values();
	}

	public Tag get(String name) {
		Tag tag = this.tags.get(name);
		if (tag == null) {
			try {
				tag = new Tag(name, this);
				this.tags.put(name, tag);
			} catch (IllegalArgumentException ex) {
				// this tag's name wasn't valid, make sure it conforms to Tag.tagRegex
			}
		}
		return tag;
	}

	public List<String> suggestTagNames(String start) {
		List<String> tagNames = new ArrayList<String>();

		for (Tag tag : this.all()) {
			if (start == null || tag.getName().startsWith(start.toLowerCase())) {
				tagNames.add(tag.getName());
			}
		}
		Collections.sort(tagNames);

		return tagNames;
	}

	public void remove(Tag tag) {
		this.tags.remove(tag.getName());
	}

	public void clear() {
		this.tags.clear();
	}

	@Override
	public void cleanUp(Tag tag) {
		this.remove(tag);
	}
}
