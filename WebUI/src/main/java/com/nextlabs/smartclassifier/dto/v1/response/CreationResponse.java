package com.nextlabs.smartclassifier.dto.v1.response;

import com.google.gson.annotations.Expose;

public class CreationResponse extends ServiceResponse {

  @Expose private Long entityId;

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }
}
