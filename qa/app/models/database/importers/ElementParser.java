package models.database.importers;

import org.xml.sax.Attributes;

public class ElementParser {

	private Syntax syntax;
	private Element elt;

	public ElementParser(Syntax syntax_) {
		this.syntax = syntax_;
		this.elt = new Element("base");
	}

	public void start(String tag, Attributes atts) throws SemanticError {
		Element subelt = new Element(tag);
		this.elt.addAt(tag, subelt);
		this.elt = subelt;
		this.syntax = this.syntax.get(tag);
	}

	public void start(String tag) throws SemanticError {
		start(tag, null);
	}

	public void end() {
		this.syntax.callback(this.elt);
		this.elt = this.elt.getParent();
		this.syntax = this.syntax.getParent();
	}

	public void text(String txt) {
		this.elt.addText(txt);
	}

	public Element getElement() {
		return this.elt;
	}

	public String toString() {
		return "P" + this.syntax.toString();
	}
}
