/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.websocket.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class DefaultServerEndpointConfigurator
        extends ServerEndpointConfig.Configurator {

    @Override
    public String getNegotiatedSubprotocol(List<String> supported,
            List<String> requested) {

        for (String request : requested) {
            if (supported.contains(request)) {
                return request;
            }
        }
        return "";
    }


    @Override
    public List<Extension> getNegotiatedExtensions(List<Extension> installed,
            List<Extension> requested) {

        List<Extension> result = new ArrayList<>();
        for (Extension request : requested) {
            if (installed.contains(request)) {
                result.add(request);
            }
        }
        return result;
    }


    @Override
    public boolean checkOrigin(String originHeaderValue) {
        return true;
    }

    @Override
    public boolean matchesURI(String path, URI requestUri,
            Map<String, String> templateExpansion) {

        String requestPath = requestUri.getPath();

        if (path.indexOf('{') == -1) {
            // Simple case - not a template
            return requestPath.equals(path);
        }

        String servletPath = WsServerContainer.getServletPath(path);
        if (!requestPath.startsWith(servletPath)) {
            return false;
        }

        Map<String,String> params;
        try {
            params = WsServerContainer.getServerContainer().getPathParameters(
                    servletPath, requestPath.substring(servletPath.length()));
        } catch (IllegalArgumentException iae) {
            return false;
        }

        if (params.size() == 0) {
            return false;
        }

        templateExpansion.putAll(params);

        return true;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec,
            HandshakeRequest request, HandshakeResponse response) {
        // NO-OP
    }

}