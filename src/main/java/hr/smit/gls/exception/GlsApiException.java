package hr.smit.gls.exception;

/** Thrown when a call to the MyGLS API fails at the transport level. */
public class GlsApiException extends RuntimeException {

    public GlsApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlsApiException(String message) {
        super(message);
    }
}
