# EXPath HTTP Client Module for eXist-db

A **drop-in replacement** for eXist-db's bundled EXPath HTTP Client. Same namespace (`http://expath.org/ns/http-client`), same `http:send-request()` function, same `http:` prefix — but built on Java's `java.net.http.HttpClient` with zero external dependencies.

**One intentional behavior change:** text and JSON responses now return `xs:string` instead of `xs:base64Binary`. This is a bug fix per the [EXPath spec](http://expath.org/spec/http-client). Code using `util:binary-to-string()` still works (the function accepts strings), but the workaround is no longer needed.

## Compatibility

- **eXist-db 6.x:** The bundled module's `conf.xml` registration takes precedence over XAR-installed modules. The XAR works once the bundled registration is removed from `conf.xml` (planned for 7.0).
- **eXist-db 7.0+:** The XAR takes over after the bundled module is removed from `conf.xml`.

## Install

From a GitHub release:

```bash
xst package install https://github.com/joewiz/exist-http-client/releases/download/v0.9.0-SNAPSHOT/exist-http-client-0.9.0-SNAPSHOT.xar
```

From a local build:

```bash
mvn package -DskipTests
xst package install target/exist-http-client-0.9.0-SNAPSHOT.xar
```

## Function

The module provides a single function with three arities:

```xquery
http:send-request($request as element(http:request)?) as item()+

http:send-request($request as element(http:request)?,
                  $href as xs:string?) as item()+

http:send-request($request as element(http:request)?,
                  $href as xs:string?,
                  $bodies as item()*) as item()+
```

**Module namespace:** `http://expath.org/ns/http-client`

### Basic GET

```xquery
import module namespace http = "http://expath.org/ns/http-client";

let $response := http:send-request(
    <http:request method="GET" href="https://httpbin.org/get"/>
)
return string($response[1]/@status)
```

### JSON API

With the new module, JSON responses are `xs:string` — `parse-json()` works directly:

```xquery
import module namespace http = "http://expath.org/ns/http-client";

let $response := http:send-request(
    <http:request method="GET" href="https://httpbin.org/json"/>
)
return parse-json($response[2])?slideshow?title
```

> **Note:** On eXist 6.x with the bundled module still active, JSON responses are `xs:base64Binary`. Use `parse-json(util:binary-to-string($response[2]))` for code that must work with both modules.

### POST with Body

```xquery
import module namespace http = "http://expath.org/ns/http-client";

http:send-request(
    <http:request method="POST" href="https://httpbin.org/post">
        <http:header name="Accept" value="application/json"/>
        <http:body media-type="text/plain">Hello from XQuery</http:body>
    </http:request>
)
```

### POST with XML Body

```xquery
import module namespace http = "http://expath.org/ns/http-client";

http:send-request(
    <http:request method="POST" href="https://httpbin.org/post">
        <http:body media-type="application/xml">
            <order><item id="1">Widget</item></order>
        </http:body>
    </http:request>
)
```

## Features

- **HTTP methods:** GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH
- **Content-Type classification:** XML → `document-node()`, JSON/text → `xs:string`, binary → `xs:base64Binary`
- **Request headers** and **body content** (text, JSON, XML, form data)
- **HTTP Basic authentication** (`username`, `password`, `auth-method` attributes)
- **Redirect following** (configurable via `follow-redirect`)
- **Timeouts** with EXPath HC006 error
- **gzip** response decompression
- **Multipart** response handling
- **`status-only`** and **`override-media-type`** attributes
- **EXPath error codes** HC001–HC006

## Content-Type Handling

| Content-Type | Return Type |
|---|---|
| `text/xml`, `application/xml`, `*+xml` | `document-node()` (parsed XML) |
| `text/html`, `application/xhtml+xml` | `document-node()` (parsed HTML) |
| `text/*` (plain, css, csv) | `xs:string` |
| `application/json`, `*+json` | `xs:string` |
| `application/javascript` | `xs:string` |
| Everything else | `xs:base64Binary` |

## What changed from the bundled module

This is a drop-in replacement. The namespace, prefix, and function signature are identical.

| | Bundled (`extensions/expath`) | This package (`exist-http-client`) |
|---|---|---|
| **Namespace** | `http://expath.org/ns/http-client` | `http://expath.org/ns/http-client` |
| **Prefix** | `http:` | `http:` |
| **Function** | `http:send-request()` (3 arities) | `http:send-request()` (3 arities) |
| **Dependencies** | `http-client-java` + `tools-java` (Apache HttpClient) | Zero — `java.net.http.HttpClient` |
| **JSON responses** | `xs:base64Binary` | `xs:string` (bug fix) |
| **Text responses** | `xs:string` for `text/*`, `xs:base64Binary` for `application/json` | `xs:string` for all text types |

### Migration

Import statements require **no changes**:

```xquery
import module namespace http = "http://expath.org/ns/http-client";
```

All three arities of `http:send-request` are compatible. The only behavioral change is that JSON responses (and other text-like types) are now returned as `xs:string` instead of `xs:base64Binary`. Code using `util:binary-to-string()` still works — the function accepts strings.

## Build

```bash
JAVA_HOME=/path/to/java-21 mvn clean package -DskipTests
```

Run integration tests (requires exist-core 7.0.0-SNAPSHOT in your local Maven repo):

```bash
mvn test -Pintegration-tests
```

## License

[GNU Lesser General Public License v2.1](https://opensource.org/licenses/LGPL-2.1)
