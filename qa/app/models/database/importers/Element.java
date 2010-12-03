package models.database.importers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Reproduces the structure of a XML document:
 * <ul>
 * <li>Each <code>Element</code> has a number of subelements.
 * <li>An <code>Element</code> might contain text.
 * <li>An <code>Element</code> has a title.
 * <li>An <code>Element</code> might have Attributes.
 * <li>Each <code>Element</code> has exactly one parent.
 * </ul>
 * 
 * @author aaron
 * 
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
	 * @return
	 */
	public String getText(String tag) {
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

	public Element getParent() {
		return this.parent;
	}

	public String getArg(String string) {
		return this.attrs.get(string);
	}
}