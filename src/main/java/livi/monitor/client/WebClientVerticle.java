package livi.monitor.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import livi.monitor.model.Service;
import livi.monitor.repository.ServiceRepository;

public class WebClientVerticle extends AbstractVerticle {

  private HttpRequest<JsonObject> request;
  private WebClient client;

  @Override
  public void start() {
    client = WebClient.create(vertx);
    vertx.setPeriodic(3000, id -> verifyServices());
  }

  private void verifyServices() {
    for (Service service: ServiceRepository.getServiceCache().values()) {
      client.headAbs(service.getUrl())
        .send()
        .onSuccess(response -> {
          if ("FAIL".equals(service.getStatus())) {
            service.setStatus("OK");
            vertx.eventBus().request("service.service-update-status", Json.encode(service),
              res -> {
                if (res.succeeded()) {
                  System.out.println("Updated status for service " + service.getName() + " to: " + service.getStatus());
                } else {
                  System.out.println("Failed to update status for service " + service.getName() + " to: " + service.getStatus());
                }
              });
          }

        })
        .onFailure(err -> {
          if ("OK".equals(service.getStatus())) {
            service.setStatus("FAIL");
            vertx.eventBus().request("service.service-update-status", Json.encode(service),
              res -> {
                if (res.succeeded()) {
                  System.out.println("Updated status for service " + service.getName() + " to: " + service.getStatus());
                } else {
                  System.out.println("Failed to update status for service " + service.getName() + " to: " + service.getStatus());
                }
              });
          }
        });
    }
  }
}
