package models.database.HotDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import models.Tag;
import models.database.ITagDatabase;
import models.helpers.ICleanup;

public class HotTagDatabase implements ITagDatabase, ICleanup<Tag> {
	private final Map<String, Tag> tags = new HashMap<String, Tag>();

	private static final String tagRegex = "^[^A-Z\\s]{1,32}$";

	public Collection<Tag> all() {
		return this.tags.values();
	}

	public Tag get(String name) {
		Tag tag = this.tags.get(name);
		if (tag == null && name.matches(tagRegex)) {
			tag = new Tag(name, this);
			this.tags.put(name, tag);
		}
		return tag;
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
