package com.nextlabs.smartclassifier.validator.v1.sourceauthentication;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class SourceAuthenticationCreationValidator {

  public static Validation validate(CreationRequest request) throws Exception {

    Validation validation = new Validation();
    SourceAuthenticationDTO saDTO = (SourceAuthenticationDTO) request.getData();

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

    // Username should not be blank
    if (StringUtils.isBlank(saDTO.getUsername())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Username"));

      return validation;
    }

    // Password should not be blank
    if (StringUtils.isBlank(saDTO.getPassword())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Password"));

      return validation;
    }
    return validation;
  }
}
