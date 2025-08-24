# Custom HTTP Server (From Scratch, Using Only Sockets)

A fully functional, **multi-threaded HTTP server** built from scratch without relying on built-in HTTP modules.  
Everything — from request parsing to response formatting — is **hand-crafted** to deeply understand HTTP protocol internals.

---

## 🚀 Features

### 1. **Pure Socket Implementation**
- Built using **raw TCP sockets** — no `HttpServer`, `http` modules, or frameworks.
- Full manual handling of:
    - Request parsing (method, headers, query params, body)
    - Response construction
    - MIME type detection
    - Data serialization/deserialization

### 2. **HTTP Methods Supported**
- `GET`
- `POST`
- `PUT`
- `DELETE`
- `PATCH`
- `OPTIONS`
- `HEAD`

### 3. **Content-Type Handling**
Manually maps file extensions and response types to MIME types.  
Includes support for **most major formats**:

#### 📄 Document Types
- `application/json`
- `text/plain`, `text/html`, `application/xml`, `text/csv`
- `application/javascript`, `text/css`
- `application/pdf`, Word (`.doc`, `.docx`), Excel (`.xls`, `.xlsx`), PowerPoint (`.ppt`, `.pptx`)

#### 🖼 Image Types
- `.png`, `.jpg`, `.jpeg`, `.gif`, `.bmp`, `.svg`, `.ico`, `.webp`

#### 🎵 Audio / 🎥 Video
- `.mp3`, `.wav`, `.ogg`
- `.mp4`, `.avi`, `.webm`, `.mov`

#### 📦 Archives
- `.zip`, `.tar`, `.gz`, `.7z`
- `application/octet-stream` fallback

---

### 4. **Dynamic Response Building**
- **Primitives & Objects → JSON**: Uses a **recursive serializer** that handles deeply nested objects and **prevents infinite reference loops**.
- Default content type:
    - HTML for strings (unless XML format detected)
- Small file serving via `File` class instance.

---

### 5. **Routing System**
- Register routes with a **callback function** (lambda) executed when the route is matched.
- Simple API:
  ```java
  server.get("/hello", (req, res) -> {
      res.send("Hello World");
  });
- Routes stored in a HashMap (new routes overwrite existing paths with the same key).

- Order-independent route storage.
### 6. **Concurrency**

- Multi-threaded request handling with Java **Executor** thread pool.
- Each incoming connection handled in its own worker thread.

### 7. **Connection Handling**

- Currently supports **Connection: close** only.

- Proper cleanup of sockets after response.