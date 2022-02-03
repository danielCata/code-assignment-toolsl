package livi.monitor;

import io.vertx.core.AbstractVerticle;
import livi.monitor.client.WebClientVerticle;
import livi.monitor.repository.ServiceRepository;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    System.out.println("Service app starting...");
    new AppServer().run(vertx);
    deployVerticle(ServiceRepository.class.getName());
    deployVerticle(WebClientVerticle.class.getName());
  }

  protected void deployVerticle(String className) {
    vertx.deployVerticle(className, res -> {
      if (res.succeeded()) {
        System.out.printf("Deployed %s verticle \n", className);
      } else {
        System.out.printf("Error deploying %s verticle:%s \n", className, res.cause());
      }
    });
  }
}
