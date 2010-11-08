package models.database.importers;

import org.xml.sax.Attributes;

public class ElementParser {

	private Syntax syntax;
	private Element elt;

	public ElementParser(Syntax syntax_) {
		syntax = syntax_;
		elt = new Element();
	}

	public void start(String tag, Attributes atts) {
		Element subelt = new Element();
		elt.addAt(tag, subelt);
		elt = subelt;
		syntax = syntax.get(tag);
	}

	public void start(String tag) {
		start(tag, null);
	}

	public void end() {
		syntax.callback(elt);
		elt = elt.getParent();
		syntax = syntax.getParent();
	}

	public void text(char[] txt, int start, int end) {
		elt.addText(txt, start, end);
	}

	public void text(String txt) {
		elt.addText(txt);
	}

	public Element getElement() {
		return elt;
	}

	public String toString() {
		return "P" + syntax.toString();
	}
}
