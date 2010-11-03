package models.database;

import java.util.Collection;

import models.Tag;

public interface ITagDatabase {
	public Collection<Tag> all();

	public Tag get(String name);

	public void add(Tag tag);

	public void remove(Tag tag);

	public void clear();
}
