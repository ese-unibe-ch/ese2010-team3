package models.database.HotDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import models.Tag;
import models.database.ITagDatabase;

public class HotTagDatabase implements ITagDatabase {
	Map<String,Tag> tags = new HashMap<String,Tag>();

	public Collection<Tag> all() {
		return tags.values();
	}

	public Tag get(String name) {
		return tags.get(name);
	}

	public void add(Tag tag) {
		tags.put(tag.getName(), tag);
	}

	public void remove(Tag tag) {
		tags.remove(tag.getName());
	}

	public void clear() {
		tags.clear();
	}
}
