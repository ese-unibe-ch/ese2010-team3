package models.database.HotDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import models.Tag;
import models.database.ITagDatabase;

public class HotTagDatabase implements ITagDatabase {
	private Map<String,Tag> tags = new HashMap<String,Tag>();

	private static final String tagRegex = "^[^A-Z\\s]{1,32}$";

	public Collection<Tag> all() {
		return tags.values();
	}

	public Tag get(String name) {
		Tag tag = tags.get(name);
		if (tag == null && name.matches(tagRegex)) {
			tag = new Tag(name);
			tags.put(name,tag);
		}
		return tag;
	}

	public void remove(Tag tag) {
		tags.remove(tag.getName());
	}

	public void clear() {
		tags.clear();
	}
}
