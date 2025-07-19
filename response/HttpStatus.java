package response;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal server.Server Error");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code,String reasonPhrase)
    {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }
    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : values()) {
            if (status.code == code) return status;
        }
        throw  new IllegalArgumentException("Invalid status code!");
    }

}
