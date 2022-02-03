package livi.monitor.repository;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import livi.monitor.model.Service;
import java.time.LocalDate;
import java.util.*;

public class ServiceRepository extends AbstractVerticle {

  private static final Map<UUID, Service> SERVICES = new HashMap<>();

  private final String DB_URI_EXTERNAL = "mysql://root:secret@localhost:3309/dev";
  private final String DB_URI = "mysql://root:secret@monitor-db-1:3306/dev";
  private MySQLPool pool;

  @Override
  public void start() throws Exception {
    System.out.println("ServiceRepository starting...");
    pool = MySQLPool.pool(vertx, DB_URI);

    vertx.eventBus().consumer("service.service-add", msg -> {
      Service service = Json.decodeValue((String)msg.body(), Service.class);
      service.setServiceId(UUID.randomUUID());
      service.setCreationDate(LocalDate.now().toString());
      addService(service, msg);
    });

    vertx.eventBus().consumer("service.service-update-status", msg -> {
      Service service = Json.decodeValue((String)msg.body(), Service.class);
      updateService(service, msg);
    });

    vertx.eventBus().consumer("service.service-get", msg -> {
      getAllServices(msg);
    });
  }

  private void addService(Service service, Message msg){
    Tuple data = ServiceMapper.to(service);
    pool.getConnection(res1->{
      if (res1.succeeded()){
        SqlConnection con = res1.result();
        con.preparedQuery("INSERT INTO services (service_id, name, url, creation_date, status) VALUES (?,?,?,?,?)").execute((data), res2 -> {
          if (res2.succeeded()){
            msg.reply(Json.encode(service));
            SERVICES.put(service.getServiceId(), service);
          } else {
            System.out.println(res2.cause().getMessage());
            msg.fail(500, res2.cause().getMessage());
          }
          con.close();
        });
      } else {
        System.out.println(res1.cause().getMessage());
        msg.fail(500, res1.cause().getMessage());
      }
    });
  }

  private void updateService(Service service, Message msg){
    Tuple data = Tuple.of(service.getStatus(), service.getServiceId());
    pool.getConnection(res1->{
      if (res1.succeeded()){
        SqlConnection con = res1.result();
        con.preparedQuery("UPDATE services SET status = ? WHERE service_id = ?").execute((data), res2 -> {
          if (res2.succeeded()){
            msg.reply(Json.encode(service));
            SERVICES.put(service.getServiceId(), service);
          } else {
            System.out.println(res2.cause().getMessage());
            msg.fail(500, res2.cause().getMessage());
          }
          con.close();
        });
      } else {
        System.out.println(res1.cause().getMessage());
        msg.fail(500, res1.cause().getMessage());
      }
    });
  }

  private void getAllServices(Message msg){
    if (SERVICES.isEmpty()) {
      pool.getConnection(res1->{
        if (res1.succeeded()){
          SqlConnection con = res1.result();
          con.query("SELECT * FROM services ")
            .execute(ar -> {
              if (ar.succeeded()) {
                List<Service> services = new ArrayList<>();
                RowSet<Row> rows = ar.result();
                rows.forEach(row->{
                  Service service = ServiceMapper.from(row);
                  services.add(service);
                  SERVICES.put(service.getServiceId(), service);
                });
                con.close();
                JsonArray result = new JsonArray(services);
                msg.reply(result.encode());
              } else {
                con.close();
                System.out.println(ar.cause().getMessage());
                msg.fail(500, ar.cause().getMessage());
              }
            });
        } else {
          System.out.println(res1.cause().getMessage());
          msg.fail(500, res1.cause().getMessage());
        }
      });
    } else {
      JsonArray result = new JsonArray(Arrays.asList(SERVICES.values().toArray()));
      msg.reply(result.encode());

    }
  }

  public static Map<UUID, Service> getServiceCache() {
    return Map.copyOf(SERVICES);
  }


}
