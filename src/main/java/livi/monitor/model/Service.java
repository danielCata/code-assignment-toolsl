package livi.monitor.model;

import java.util.UUID;

public class Service {

  private String name;
  private String url;
  private String status;
  private String creationDate;
  private UUID serviceId;

  public Service() {
  }

  public Service(String name, String url, String status, UUID serviceId, String creationDate) {
    super();
    this.name = name;
    this.url = url;
    this.status = status;
    this.serviceId = serviceId;
    this.creationDate = creationDate;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getStatus() {
    return status;
  }

  public UUID getServiceId() {
    return serviceId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setServiceId(UUID serviceId) {
    this.serviceId = serviceId;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }
}
