package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.SCConstant;
import com.nextlabs.smartclassifier.database.entity.Action;
import com.nextlabs.smartclassifier.database.entity.ActionParam;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActionDTO extends BaseDTO {

  @Expose private ActionPluginDTO actionPlugin;
  @Expose private String toleranceLevel;
  @Expose private List<ActionParamDTO> actionParams;

  public ActionDTO() {
    super();
  }

  public ActionDTO(Action action) {
    super();
    copy(action);
  }

  public void copy(Action action) {
    if (action != null) {
      this.id = action.getId();

      if (action.getActionPlugin() != null) {
        this.actionPlugin = new ActionPluginDTO(action.getActionPlugin());
      }

      this.toleranceLevel = action.getToleranceLevel();

      if (action.getActionParams() != null) {
        this.actionParams = new ArrayList<>();
        Map<String, ActionParamDTO> actionParamMap = new LinkedHashMap<>();

        for (ActionParam param : action.getActionParams()) {
          if (actionParamMap.containsKey(param.getIdentifier())) {
            KeyValueDTO keyValueDTO = new KeyValueDTO();
            keyValueDTO.setKey(param.getMetadataKey());
            keyValueDTO.setValue(param.getValue());

            actionParamMap.get(param.getIdentifier()).getValues().add(keyValueDTO);
          } else {
            ActionParamDTO actionParamDTO = new ActionParamDTO();
            actionParamDTO.setIdentifier(param.getIdentifier());

            ActionPluginParamDTO actionPluginParam =
                getActionPluginParam(actionParamDTO.getIdentifier());
            if (actionPluginParam != null) {
              actionParamDTO.isCollections(actionPluginParam.isCollections());
              actionParamDTO.isKeyValue(actionPluginParam.isKeyValue());
              actionParamDTO.setDataType(actionPluginParam.getDataType());
              actionParamDTO.setLabel(actionPluginParam.getLabel());
            }

            KeyValueDTO keyValueDTO = new KeyValueDTO();
            keyValueDTO.setKey(param.getMetadataKey());
            keyValueDTO.setValue(param.getValue());
            actionParamDTO.getValues().add(keyValueDTO);

            actionParamMap.put(param.getIdentifier(), actionParamDTO);
          }
        }

        this.actionParams.addAll(actionParamMap.values());
      }
    }
  }

  public Action getEntity() {
    Action action = new Action();

    action.setId(this.id);
    if (this.actionPlugin != null) {
      action.setActionPlugin(this.getActionPlugin().getEntity());
    }

    // Default to success if not set
    action.setToleranceLevel(
        StringUtils.isBlank(this.toleranceLevel)
            ? ActionResult.SUCCESS.getCode()
            : this.toleranceLevel);

    if (this.actionParams != null && this.actionParams.size() > 0) {
      for (ActionParamDTO param : this.actionParams) {
        for (KeyValueDTO keyValue : param.getValues()) {
          ActionParam actionParam = new ActionParam();

          actionParam.setIdentifier(param.getIdentifier());
          actionParam.setMetadataKey(keyValue.getKey());
          actionParam.setValue(keyValue.getValue());

          if (SCConstant.ACTION_PARAM_TAG_IDENTIFIER.equals(actionParam.getIdentifier())
              && StringUtils.isBlank(actionParam.getMetadataKey())) {
            actionParam.setMetadataKey(actionParam.getValue());
          }

          action.getActionParams().add(actionParam);
        }
      }
    }

    return action;
  }

  public ActionPluginDTO getActionPlugin() {
    return actionPlugin;
  }

  public void setActionPlugin(ActionPluginDTO actionPlugin) {
    this.actionPlugin = actionPlugin;
  }

  public String getToleranceLevel() {
    return toleranceLevel;
  }

  public void setToleranceLevel(String toleranceLevel) {
    this.toleranceLevel = toleranceLevel;
  }

  public List<ActionParamDTO> getActionParams() {
    return actionParams;
  }

  public void setActionParams(List<ActionParamDTO> actionParams) {
    this.actionParams = actionParams;
  }

  private ActionPluginParamDTO getActionPluginParam(String identifier) {
    if (actionPlugin != null) {
      return actionPlugin.getPluginParamMap().get(identifier);
    }

    return null;
  }
}
