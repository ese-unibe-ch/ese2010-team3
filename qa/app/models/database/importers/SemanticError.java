package models.database.importers;

public class SemanticError extends Exception {
	private final String mesg;

	public SemanticError() {
		this.mesg = "";
	}

	public SemanticError(String string) {
		this.mesg = string;
	}

	public String getMessage() {
		return this.mesg;
	}
}
