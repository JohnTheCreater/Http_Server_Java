package content;

public enum ContentType
{
    JSON("application/json"),
    TEXT("text/plain; charset=utf-8"),
    HTML("text/html; charset=utf-8"),
    XML("application/xml; charset=utf-8"),
    CSV("text/csv; charset=utf-8"),
    JS("application/javascript"),
    CSS("text/css"),

    PDF("application/pdf"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS("application/vnd.ms-excel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPT("application/vnd.ms-powerpoint"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    PNG("image/png"),
    JPG("image/jpeg"),
    JPEG("image/jpeg"),
    GIF("image/gif"),
    BMP("image/bmp"),
    SVG("image/svg+xml"),
    ICO("image/x-icon"),
    WEBP("image/webp"),

    MP3("audio/mpeg"),
    WAV("audio/wav"),
    OGG("audio/ogg"),
    MP4("video/mp4"),
    AVI("video/x-msvideo"),
    WEBM("video/webm"),
    MOV("video/quicktime"),

    ZIP("application/zip"),
    TAR("application/x-tar"),
    GZ("application/gzip"),
    SEVEN_Z("application/x-7z-compressed"),
    OCTET_STREAM("application/octet-stream"),

    EMPTY("");

    private final String value;
    ContentType(String value)
    {
        this.value = value;
    }
    public String value()
    {
        return this.value;
    }
    public String toString()
    {
        return this.value;
    }

    public static ContentType fromMimeType(String mimeType) {

        if (mimeType == null || mimeType.isBlank()) {
            return ContentType.OCTET_STREAM;
        }

        for (ContentType ct : values()) {
            if (ct.value().equalsIgnoreCase(mimeType)) {
                return ct;
            }
        }
        return ContentType.OCTET_STREAM;
    }
}