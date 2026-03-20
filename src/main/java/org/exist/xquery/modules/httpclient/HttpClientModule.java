/*
 * eXist-db Open Source Native XML Database
 * Copyright (C) 2001 The eXist-db Authors
 *
 * info@exist-db.org
 * http://www.exist-db.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.exist.xquery.modules.httpclient;

import org.exist.dom.QName;
import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;

import java.util.List;
import java.util.Map;

import static org.exist.xquery.FunctionDSL.functionDefs;

/**
 * Native EXPath HTTP Client Module for eXist-db.
 *
 * <p>Implements the EXPath HTTP Client 1.0 specification using Java's
 * built-in {@code java.net.http.HttpClient}, with zero external dependencies.</p>
 *
 * <p>Key improvement over the previous implementation: text responses
 * (JSON, plain text) are correctly returned as {@code xs:string} instead of
 * {@code xs:base64Binary}.</p>
 *
 * @see <a href="http://expath.org/spec/http-client">EXPath HTTP Client 1.0</a>
 */
public class HttpClientModule extends AbstractInternalModule {

    public static final String NAMESPACE_URI = "http://expath.org/ns/http-client";
    public static final String PREFIX = "http";
    public static final String RELEASE = "1.0.0";

    public static final String ERROR_NS = "http://expath.org/ns/error";

    public static final FunctionDef[] functions = functionDefs(
            functionDefs(SendRequestFunction.class,
                    SendRequestFunction.FS_SEND_REQUEST)
    );

    public HttpClientModule(final Map<String, List<?>> parameters) {
        super(functions, parameters, true);
    }

    @Override
    public String getNamespaceURI() {
        return NAMESPACE_URI;
    }

    @Override
    public String getDefaultPrefix() {
        return PREFIX;
    }

    @Override
    public String getDescription() {
        return "EXPath HTTP Client Module — sends HTTP requests and returns responses";
    }

    @Override
    public String getReleaseVersion() {
        return RELEASE;
    }

    static QName qname(final String localPart) {
        return new QName(localPart, NAMESPACE_URI, PREFIX);
    }
}
