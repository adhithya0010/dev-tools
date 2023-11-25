package com.intellij.devtools.utils;

import com.intellij.devtools.component.table.InvocationsModel;
import com.intellij.devtools.exec.HttpRequestConfig;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServerUtils {

  private static final Map<String, HttpRequestConfig> requestConfigMap = new HashMap<>();
  private static final Map<Integer, Server> serverMap = new HashMap<>();

  private ServerUtils() {}

  public static void startServer(
      InvocationsModel invocationsModel, HttpRequestConfig httpRequestConfig) {
    requestConfigMap.put(httpRequestConfig.getId(), httpRequestConfig);
    serverMap.computeIfAbsent(httpRequestConfig.getPort(), Server::new);

    Server server = serverMap.get(httpRequestConfig.getPort());
    server.createContext(
        httpRequestConfig.getId(),
        httpRequestConfig.getPath(),
        new HttpRequestHandler(invocationsModel, httpRequestConfig));
  }

  public static void stopServer(String id) {
    HttpRequestConfig httpRequestConfig = requestConfigMap.get(id);
    Server server = serverMap.get(httpRequestConfig.getPort());
    server.removeContext(id);
    if (server.isServerEmpty()) {
      server.shutdown();
      serverMap.remove(httpRequestConfig.getPort());
      requestConfigMap.remove(id);
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
