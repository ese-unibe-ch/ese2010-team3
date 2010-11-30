package models.database.importers;

import java.util.Map;

public class ElementParser {

	private Syntax syntax;
	private Element elt;

	public ElementParser(Syntax syntax_) {
		this.syntax = syntax_;
		this.elt = new Element("base");
	}

	public void start(String tag, Map<String, String> attributes)
			throws SemanticError {
		Element subelt = new Element(tag, attributes);
		this.elt.addAt(tag, subelt);
		this.elt = subelt;
		this.syntax = this.syntax.get(tag);
	}

	public void start(String tag) throws SemanticError {
		start(tag, null);
	}

	public void end() throws SemanticError {
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

	@Override
	public String toString() {
		return "P" + this.syntax.toString();
	}
}
