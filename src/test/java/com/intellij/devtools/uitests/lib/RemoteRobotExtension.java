package com.intellij.devtools.uitests.lib;

import com.intellij.remoterobot.RemoteRobot;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class RemoteRobotExtension implements AfterTestExecutionCallback, ParameterResolver {

  private String url = System.getProperty("remote-robot-url", "http://127.0.0.1:8580");
  private RemoteRobot remoteRobot;

  public RemoteRobotExtension() {
    //    if (System.getProperty("debug-retrofit").equals("enable")) {
    //      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(
    //          HttpLoggingInterceptor.Level.BODY);
    //      OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    //      remoteRobot = new RemoteRobot(url, client);
    //    } else {
    remoteRobot = new RemoteRobot(url);
    //    }
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType().equals(RemoteRobot.class);
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return remoteRobot;
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {}
}
