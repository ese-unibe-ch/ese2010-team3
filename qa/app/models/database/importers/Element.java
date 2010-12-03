package models.database.importers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Reproduces a partial structure of an XML document:
 * <ul>
 * <li>Each <code>Element</code> has a number of subelements.
 * <li>An <code>Element</code> might contain text.
 * <li>An <code>Element</code> has a tag name.
 * <li>An <code>Element</code> might have Attributes.
 * <li>Each <code>Element</code> has exactly one parent.
 * </ul>
 * 
 * @author aaron
 */
public class Element {
	private final String name;
	private Map<String, String> attrs;
	private final StringBuilder text;
	private final Map<String, List<Element>> subelements;
	private Element parent;

	public Element(String name, Map<String, String> attributes) {
		this(name);
		this.attrs = attributes;
	}

	public Element(String name) {
		this.name = name;
		this.subelements = new HashMap<String, List<Element>>();
		this.text = new StringBuilder();
	}

	@Override
	public String toString() {
		return "E[" + this.name + "](\"" + this.text.toString() + "\","
				+ this.subelements.toString() + ")";
	}

	/**
	 * A list of all children wit a given name/tag.
	 * 
	 * @param tag
	 *            name of the XML element
	 * @return List of Elements with the given name, in order of occurrence.
	 */
	public List<Element> get(String tag) {
		return this.subelements.get(tag);
	}

	/**
	 * The text that was directly found inside the node.
	 * 
	 * @return accumulated text.
	 */
	public String getText() {
		return this.text.toString();
	}

	/**
	 * Text of the first subelement with the given tag. <br/>
	 * 
	 * Best used when there is one and only one element of the given text
	 * expected.
	 * 
	 * @param tag
	 *            the tag's name
	 * @return the tag's text content (or <code>null</code> if there's no such
	 *         tag)
	 */
	public String getText(String tag) {
		if (!this.subelements.containsKey(tag))
			return null;
		return get(tag).get(0).getText();
	}

	/**
	 * Reads new text into the Element. It will be added to the end of the
	 * text so far.
	 * 
	 * @param str
	 */
	public void addText(String str) {
		this.text.append(str);
	}

	/**
	 * Adds a subelement of the given tag.
	 * 
	 * @param tag
	 * @param subelt
	 */
	public void addAt(String tag, Element subelt) {
		if (!this.subelements.containsKey(tag)) {
			this.subelements.put(tag, new LinkedList());
		}
		this.subelements.get(tag).add(subelt);
		subelt.parent = this;
	}

	/**
	 * Returns this element's parent (if there is any).
	 * 
	 * @return the parent or <code>null</code>
	 */
	public Element getParent() {
		return this.parent;
	}

	/**
	 * Returns a named attribute of this element (if there is any).
	 * 
	 * @param string
	 *            the attribute's name
	 * @return its value or <code>null</code>
	 */
	public String getArg(String name) {
		return this.attrs.get(name);
	}
}
