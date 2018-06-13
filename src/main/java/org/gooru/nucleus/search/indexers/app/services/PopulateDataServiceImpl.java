package org.gooru.nucleus.search.indexers.app.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gooru.nucleus.search.indexers.app.constants.HttpConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.jobs.MachineClassifyResourceToDomainThreadExecutor;
import org.gooru.nucleus.search.indexers.app.jobs.MachineClassifyResourceToStandardThreadExecutor;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.BadRequestException;
import org.gooru.nucleus.search.indexers.app.utils.UtilityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class PopulateDataServiceImpl implements PopulateDataService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateDataServiceImpl.class);

  private final ExecutorService service = Executors.newFixedThreadPool(10);
  
  @Override
    public void classifyResourcesToDomain(RoutingContext context, String type, JsonObject body) {
        JsonObject resultObject = null;
        try {
            switch (type) {
            case IndexerConstants.TYPE_RESOURCE:
                String statusInCache = null;
                if (UtilityManager.getCache().containsKey("mc-r-domain")) {
                    resultObject = (JsonObject) UtilityManager.getCache().get("mc-r-domain");
                    statusInCache = resultObject.getString("status");
                }
                if (!UtilityManager.getCache().containsKey("mc-r-domain")
                    || (resultObject != null && !statusInCache.matches("in-progress"))) {
                    LOGGER.debug("Cache set as In-Progress and proceed");
                    resultObject = new JsonObject();
                    resultObject.put("status", "in-progress");
                    resultObject.put("message", "classify-resource in-progress");
                    UtilityManager.getCache().put("classify-resource-domain", resultObject);
                    service.submit(new MachineClassifyResourceToDomainThreadExecutor(body));
                    context.response().setStatusCode(HttpConstants.HttpStatus.ACCEPTED.getCode())
                        .setStatusMessage(HttpConstants.HttpStatus.ACCEPTED.getMessage());
                } else {
                    LOGGER.debug("Returning status from cache");
                    resultObject = (JsonObject) UtilityManager.getCache().get("mc-r-domain");
                    if (resultObject.getString("status").matches("completed|disabled")) {
                        context.response().setStatusCode(HttpConstants.HttpStatus.SUCCESS.getCode())
                            .setStatusMessage(HttpConstants.HttpStatus.SUCCESS.getMessage());
                    } else if (resultObject.getString("status").equalsIgnoreCase("in-progress")) {
                        context.response().setStatusCode(HttpConstants.HttpStatus.ACCEPTED.getCode())
                            .setStatusMessage(HttpConstants.HttpStatus.ACCEPTED.getMessage());
                    }
                }
                context.response().end(resultObject.encodePrettily());
                break;
            case IndexerConstants.TYPE_COLLECTION:
                throw new BadRequestException("Unsupported Type!");
            default:
            }
        } catch (Exception e) {
            LOGGER.debug("Error !! : {}", e);
        }
    }
  
  @Override
    public void classifyResourcesToStandards(RoutingContext context, String type, JsonObject body) {
        JsonObject resultObject = null;
        try {
            switch (type) {
            case IndexerConstants.TYPE_RESOURCE:
                String statusInCache = null;
                if (UtilityManager.getCache().containsKey("mc-r-standard")) {
                    resultObject = (JsonObject) UtilityManager.getCache().get("mc-r-standard");
                    statusInCache = resultObject.getString("status");
                }
                if (!UtilityManager.getCache().containsKey("mc-r-standard")
                    || (resultObject != null && !statusInCache.matches("in-progress"))) {
                    LOGGER.debug("Cache set as In-Progress and proceed");
                    resultObject = new JsonObject();
                    resultObject.put("status", "in-progress");
                    resultObject.put("message", "classify-resource in-progress");
                    UtilityManager.getCache().put("classify-resource-standard", resultObject);
                    service.submit(new MachineClassifyResourceToStandardThreadExecutor(body));
                    context.response().setStatusCode(HttpConstants.HttpStatus.ACCEPTED.getCode())
                        .setStatusMessage(HttpConstants.HttpStatus.ACCEPTED.getMessage());
                } else {
                    LOGGER.debug("Returning status from cache");
                    resultObject = (JsonObject) UtilityManager.getCache().get("mc-r-standard");
                    if (resultObject.getString("status").matches("completed|disabled")) {
                        context.response().setStatusCode(HttpConstants.HttpStatus.SUCCESS.getCode())
                            .setStatusMessage(HttpConstants.HttpStatus.SUCCESS.getMessage());
                    } else if (resultObject.getString("status").equalsIgnoreCase("in-progress")) {
                        context.response().setStatusCode(HttpConstants.HttpStatus.ACCEPTED.getCode())
                            .setStatusMessage(HttpConstants.HttpStatus.ACCEPTED.getMessage());
                    }
                }
                context.response().end(resultObject.encodePrettily());
                break;
            case IndexerConstants.TYPE_COLLECTION:
                throw new BadRequestException("Unsupported Type!");
            default:
            }
        } catch (Exception e) {
            LOGGER.debug("Error !! : {}", e);
        }
    }

}
