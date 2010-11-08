package models.database.importers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

public class Element {
	private String name;
	private Attributes attrs;
	private StringBuilder text;
	private Map<String, List<Element>> subelements;
	private Element parent;

	public Element(String name_) {
		name = name_;
		subelements = new HashMap<String, List<Element>>();
		text = new StringBuilder();
	}

	public Element(String name_, Element parent_) {
		this(name_);
		parent = parent_;
	}

	public String toString() {
		return "E[" + name + "](\"" + (text == null ? "" : text.toString())
				+ "\"" +
					(subelements == null ? "" : "," + subelements.toString())
				+ ")";
	}

	public List<Element> get(String tag) {
		return subelements.get(tag);
	}

	public String getText() {
		return text.toString();
	}

	public String getText(String tag) {
		return get(tag).get(0).getText();
	}

	public void addText(String str) {
		text.append(str);
	}

	public void addText(char[] str, int start, int finish) {
		text.append(str, start, finish);
	}

	public void addAt(String tag, Element subelt) {
		if (!subelements.containsKey(tag)) {
			subelements.put(tag, new LinkedList());
		}
		subelements.get(tag).add(subelt);
		subelt.parent = this;
	}

	public Element getParent() {
		return parent;
	}

	public boolean has(String string) {
		return subelements.containsKey(string);
	}
}