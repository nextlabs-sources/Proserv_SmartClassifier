package com.nextlabs.smartclassifier.controller.v1;

import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.dto.v1.CodeValueDTO;
import com.nextlabs.smartclassifier.dto.v1.request.IndexQueryRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.service.v1.solr.DocumentQueryService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.documentquery.DocumentQueryValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/v1/documentQuery")
public class DocumentQueryController
        extends Controller {

    private static final Logger logger = LogManager.getLogger(DocumentQueryController.class);

    @GET
    @Path("/sortFields")
    @Produces(MediaType.APPLICATION_JSON)
    public String sortFields() {
        try {
            CollectionResponse response = new CollectionResponse();

            List<CodeValueDTO> codeValueDTOs = new ArrayList<>();

            codeValueDTOs.add(new CodeValueDTO(SolrPredefinedField.DOCUMENT_NAME_LCASE, "Document Name"));
            codeValueDTOs.add(new CodeValueDTO(SolrPredefinedField.DIRECTORY_LCASE, "Directory"));
            codeValueDTOs.add(new CodeValueDTO(SolrPredefinedField.AUTHOR, "Author"));
            codeValueDTOs.add(new CodeValueDTO(SolrPredefinedField.FOLDER_URL, "Folder URL"));

            response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
            response.setMessage(MessageUtil.getMessage("success.data.loaded"));
            response.setTotalNoOfRecords(codeValueDTOs.size());
            response.setData(codeValueDTOs);

            return response.toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(String requestBody) {
        try {
            logger.debug("Index Query Request =  " + requestBody);

            IndexQueryRequest queryRequest = getIndexQueryRequest(requestBody);

            Validation validation = DocumentQueryValidator.validate(queryRequest);
            if (!validation.isValid()) {
                return error(validation.getErrorCode(), validation.getErrorMessage());
            }

            String responseString = (new DocumentQueryService(servletContext)).getDocuments(queryRequest).toJson();
            logger.debug(responseString);
            return responseString;
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }
}
