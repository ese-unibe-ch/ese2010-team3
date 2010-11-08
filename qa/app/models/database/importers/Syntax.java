package models.database.importers;

import java.util.HashMap;
import java.util.Map;

public class Syntax {
	private String name;
	private Syntax parent;
	private Map<String, Syntax> subsyntax;
	private Action callback;

	private Syntax(String name, Syntax parent) {
		this.name = name;
		this.parent = parent;
		subsyntax = new HashMap<String, Syntax>();
	}

	public String getName() {
		return name;
	}

	public Syntax get(String tag) {
		return subsyntax.get(tag);
	}

	public String toString() {
		return "S(" + name + ")";
	}

	public Syntax(String name) {
		this(name, null);
	}

	public Syntax by(String tag) {
		Syntax sub = new Syntax(tag, this);
		subsyntax.put(tag, sub);
		return sub;
	}

	public Syntax read(String tag) {
		Syntax sub = new Syntax(tag, this);
		subsyntax.put(tag, sub);
		return this;
	}

	public Syntax end() {
		return parent;
	}

	public ElementParser done() {
		return new ElementParser(this);
	}

	public Syntax getParent() {
		return parent;
	}

	public Syntax call(Action action) {
		callback = action;
		return this;
	}

	public boolean hasCallback() {
		return callback != null;
	}

	public void callback(Element elt) {
		if (hasCallback()) {
			callback.call(elt);
		}
	}
}
