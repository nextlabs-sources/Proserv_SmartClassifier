package com.nextlabs.smartclassifier.validator.v1.documentquery;

import com.nextlabs.smartclassifier.constant.DataSection;
import com.nextlabs.smartclassifier.constant.MetadataMatchingCondition;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.dto.v1.CriteriaDTO;
import com.nextlabs.smartclassifier.dto.v1.CriteriaGroupDTO;
import com.nextlabs.smartclassifier.dto.v1.request.IndexQueryRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.util.StringFunctions;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DocumentQueryValidator {

    private static final Logger logger = LogManager.getLogger(DocumentQueryValidator.class);

    public static Validation validate(IndexQueryRequest request)
            throws Exception {
        logger.debug("Trying to validate the request");
        Validation validation = new Validation();
        List<CriteriaGroupDTO> criteriaGroupDTOs = request.getCriteria();

        if (criteriaGroupDTOs != null && criteriaGroupDTOs.size() > 0) {
            for (CriteriaGroupDTO criteriaGroupDTO : criteriaGroupDTOs) {
                if (criteriaGroupDTO.getCriterias() != null && criteriaGroupDTO.getCriterias().size() > 0) {
                    for (CriteriaDTO criteriaDTO : criteriaGroupDTO.getCriterias()) {

                        /*if (DataSection.getSection(criteriaDTO.getDataSection()) == DataSection.DIRECTORY) {
                            logger.debug("Data Section found");
                            if (repositoryType == RepositoryType.SHAREPOINT) {
                                logger.debug("Trying to remove white spaces");
                                criteriaDTO.setValue(StringFunctions.removeWhiteSpacesInURL(criteriaDTO.getValue()));
                            }
                        }*/


                        if (MetadataMatchingCondition.NUM_EQUALS.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_GREATER.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_GREATER_OR_EQUAL.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_LESSER.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_LESSER_OR_EQUAL.getCode().equals(criteriaDTO.getMatchingCondition())) {
                            try {
                                Integer.parseInt(criteriaDTO.getValue());
                            } catch (NumberFormatException err) {
                                validation.isValid(false);
                                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.maxvalue.code"));
                                validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.maxvalue", criteriaDTO.getField(), Integer.MAX_VALUE));

                                break;
                            }
                        }
                    }
                }
            }
        }

        List<CriteriaGroupDTO> dirCriteriaGroups = request.getDirectories();
        RepositoryType repositoryType = RepositoryType.getRepositoryType(request.getRepositoryType());

        if (dirCriteriaGroups != null && dirCriteriaGroups.size() > 0) {
            for (CriteriaGroupDTO dirCriteriaGroup : dirCriteriaGroups) {

                List<CriteriaDTO> criteria = dirCriteriaGroup.getCriterias();
                if (criteria != null && criteria.size() > 0) {
                    for (CriteriaDTO criterion : criteria) {

                        if (DataSection.getSection(criterion.getDataSection()) == DataSection.DIRECTORY) {
                            if (repositoryType == RepositoryType.SHAREPOINT) {
                                criterion.setValue(StringFunctions.removeWhiteSpacesInURL(criterion.getValue()));
                            }
                        }
                    }
                }
            }
        }

        return validation;
    }
}
