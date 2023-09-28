package com.intellij.devtools.utils;

import com.intellij.devtools.component.table.MockMetadata;
import com.intellij.devtools.exec.HttpMethod;
import com.intellij.devtools.component.table.InvocationsModel;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServerUtils {

  private static final Map<String, MockMetadata> serverMetaMap = new HashMap<>();
  private static final Map<Integer, Server> serverMap = new HashMap<>();

  private ServerUtils() {
  }

  public static MockMetadata startServer(InvocationsModel invocationsModel, String path, String port, HttpMethod httpMethod,
      String responseCode, String responseHeaders, String responseBody) {
    int portValue = Integer.parseInt(port);
    int statusValue = Integer.parseInt(responseCode);
    MockMetadata mockMetadata = new MockMetadata(path, portValue, httpMethod, responseCode, responseHeaders, responseBody);

    serverMetaMap.put(mockMetadata.id, mockMetadata);
    serverMap.computeIfAbsent(portValue, Server::new);

    Server server = serverMap.get(portValue);
    server.createContext(
        mockMetadata.id, path, new HttpRequestHandler(invocationsModel, httpMethod, statusValue, responseHeaders,
            responseBody));
    return mockMetadata;
  }

  public static void stopServer(String id) {
    MockMetadata mockMetadata = serverMetaMap.get(id);
    Server server = serverMap.get(mockMetadata.port);
    server.removeContext(id);
    if(server.isServerEmpty()) {
      server.shutdown();
      serverMap.remove(mockMetadata.port);
      serverMetaMap.remove(id);
    }
  }

  static class Server {

    private final HttpServer httpServer;
    private final Map<String, HttpContext> contextMap = new HashMap<>();

    public Server(int port) {
      try {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        start();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public void createContext(String id, String path, HttpHandler httpHandler) {
      contextMap.put(id, httpServer.createContext(path, httpHandler));
    }

    private void start() {
      new Thread(httpServer::start).start();
    }

    public void shutdown() {
      new Thread(() -> httpServer.stop(1000)).start();
    }

    public void removeContext(String id) {
      httpServer.removeContext(contextMap.get(id));
      contextMap.remove(id);
    }

    public boolean isServerEmpty() {
      return contextMap.isEmpty();
    }
  }
}
