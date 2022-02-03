package livi.monitor.repository;

import java.util.UUID;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import livi.monitor.model.Service;

public class ServiceMapper{

  public static Service from (Row row){
    UUID serviceId = UUID.fromString(row.getString("service_id"));
    String name = row.getString("name");
    String url = row.getString("url");
    String status = row.getString("status");
    String creationDate = row.getLocalDate("creation_date").toString();
    return new Service(name, url, status, serviceId, creationDate);
  }

  public static Tuple to (Service service){
    return Tuple.of(service.getServiceId(), service.getName(), service.getUrl(), service.getCreationDate(), service.getStatus());
  }
}
