package engine;

public class Letter {
	String sender;
	int code;
	String focus;
	
	public Letter(String sender, int code, String focus) {
		this.sender = sender;
		this.code = code;
		this.focus = focus;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}
	
	
	
	/* Code
	 * 1 - Pedir ajuda do suporte
	 * 2 - Pedir ajuda para matar o oponente com pouco HP
	 * */
}
