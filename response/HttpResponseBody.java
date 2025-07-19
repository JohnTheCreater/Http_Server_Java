package response;


import content.ContentType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class HttpResponseBody {
    private ContentType contentType;
    private byte[] rawContent;

    public HttpResponseBody() {
        this.contentType = ContentType.EMPTY;
        this.rawContent = new byte[0];
    }

    public void setRawContent(byte[] rawContent) {
        this.rawContent = rawContent;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public byte[] getRawContent() {
        return this.rawContent;
    }

    public String toString(Charset charset) {
        if (this.contentType != ContentType.TEXT && this.contentType != ContentType.JSON && this.contentType != ContentType.HTML && this.contentType != ContentType.XML) {
            throw new UnsupportedOperationException("Cannot convert the body contentType to String!");
        } else {
            return new String(this.rawContent, charset);
        }
    }

    public InputStream asStream() {
        return new ByteArrayInputStream(this.rawContent);
    }

    public boolean isEmpty() {
        return this.contentType == ContentType.EMPTY || this.rawContent.length == 0;
    }
}
