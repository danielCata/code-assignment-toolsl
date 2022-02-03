package livi.monitor;

import io.netty.util.internal.StringUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import livi.monitor.model.Service;

import java.net.URL;

public class AppServer {
  private final Logger LOGGER = LoggerFactory.getLogger(AppServer.class);

  public void run(Vertx vertx) {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get().handler(StaticHandler.create());
    router.get("/services").handler(ctx -> getServices(ctx, vertx));
    router.post("/services").handler(ctx -> postService(ctx, vertx));
    server.requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        LOGGER.info("ServiceApp HTTP server started on port 8888");
      } else {
        LOGGER.info("ServiceApp HTTP server failed to start");
      }
    });
  }

  private void postService(RoutingContext ctx, Vertx vertx) {
    final Service service =  ctx.getBodyAsJson().mapTo(Service.class);
    if (StringUtil.isNullOrEmpty(service.getName()) ||
      StringUtil.isNullOrEmpty(service.getUrl())) {
      ctx.response()
        .setStatusCode(400)
        .putHeader("content-type", "application/json")
        .end("{ 'error': 'Name and url must by non-empty' }");
      return;
    }
    try {
      new URL(service.getUrl()).toURI();
    }
    catch (Exception e) {
      ctx.response()
        .setStatusCode(400)
        .putHeader("content-type", "application/json")
        .end("{ 'error': 'not a valid url' }");
      return;
    }
    vertx.eventBus().request("service.service-add", Json.encode(service),
      res -> {
        if (res.succeeded()) {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(res.result().body().toString());
        } else {
          ctx.fail(res.cause());
        }
      });
  }

  private void getServices(RoutingContext ctx, Vertx vertx) {
    vertx.eventBus().request("service.service-get", "", res -> {
      if ( res.succeeded() ) {
        ctx.response()
          .putHeader("content-type", "application/json")
          .end( res.result().body().toString() );
      } else {
        ctx.fail( res.cause() );
      }
    });
  }
}
