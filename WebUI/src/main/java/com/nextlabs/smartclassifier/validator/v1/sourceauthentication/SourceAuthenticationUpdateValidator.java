package com.nextlabs.smartclassifier.validator.v1.sourceauthentication;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class SourceAuthenticationUpdateValidator {

  public static Validation validate(UpdateRequest updateRequest) {
    Validation validation = new Validation();

    SourceAuthenticationDTO saDTO = (SourceAuthenticationDTO) updateRequest.getData();

    if (saDTO.getId() == null || saDTO.getId() == 0) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
      validation.setErrorMessage(
          MessageUtil.getMessage("invalid.input.field.zero", "SourceAuthenticationID"));

      return validation;
    }
    // Name should not be blank
    if (StringUtils.isBlank(saDTO.getName())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));

      return validation;
    }

    // Domain Name should not be blank
    if (StringUtils.isBlank(saDTO.getDomain())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(
          MessageUtil.getMessage("invalid.input.field.blank", "Domain Name"));

      return validation;
    }

    // Name should not be blank
    if (StringUtils.isBlank(saDTO.getUsername())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Username"));

      return validation;
    }

    // Name should not be blank
    if (StringUtils.isBlank(saDTO.getPassword())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Password"));

      return validation;
    }

    return validation;
  }
}
