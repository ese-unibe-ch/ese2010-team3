package models.database.importers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Element {
	private final String name;
	private final StringBuilder text;
	private final Map<String, List<Element>> subelements;
	private Element parent;

	public Element(String name_) {
		this.name = name_;
		this.subelements = new HashMap<String, List<Element>>();
		this.text = new StringBuilder();
	}

	public Element(String name_, Element parent_) {
		this(name_);
		this.parent = parent_;
	}

	@Override
	public String toString() {
		return "E["
				+ this.name
				+ "](\""
				+ (this.text == null ? "" : this.text.toString())
				+ "\""
				+
					(this.subelements == null ? "" : ","
							+ this.subelements.toString())
				+ ")";
	}

	public List<Element> get(String tag) {
		return this.subelements.get(tag);
	}

	public String getText() {
		return this.text.toString();
	}

	public String getText(String tag) {
		return get(tag).get(0).getText();
	}

	public void addText(String str) {
		this.text.append(str);
	}

	public void addText(char[] str, int start, int finish) {
		this.text.append(str, start, finish);
	}

	public void addAt(String tag, Element subelt) {
		if (!this.subelements.containsKey(tag)) {
			this.subelements.put(tag, new LinkedList());
		}
		this.subelements.get(tag).add(subelt);
		subelt.parent = this;
	}

	public Element getParent() {
		return this.parent;
	}

	public boolean has(String string) {
		return this.subelements.containsKey(string);
	}
}