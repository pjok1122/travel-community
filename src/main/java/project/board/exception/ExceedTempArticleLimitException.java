package project.board.exception;

public class ExceedTempArticleLimitException extends RuntimeException {

	private static final long serialVersionUID = -6752546529216415624L;

	public ExceedTempArticleLimitException() {
		super();
	}

	public ExceedTempArticleLimitException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExceedTempArticleLimitException(String message) {
		super(message);
	}

	public ExceedTempArticleLimitException(Throwable cause) {
		super(cause);
	}

	
}
