package org.gooru.nucleus.search.indexers.app.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gooru.nucleus.search.indexers.app.constants.HttpConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateResourceSuggestThreadExecutor;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.BadRequestException;
import org.gooru.nucleus.search.indexers.app.utils.UtilityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class PopulateSuggestServiceImpl implements PopulateSuggestService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateSuggestServiceImpl.class);

  private final ExecutorService service = Executors.newFixedThreadPool(10);

  @Override
  public void populateSuggestTable(RoutingContext context, String type, JsonObject body) {
    JsonObject resultObject = null;
    try {
      switch (type) {
      case IndexerConstants.TYPE_RESOURCE:
        if (!UtilityManager.getCache().containsKey("populate-resource-suggestion")) {
          LOGGER.debug("Cache set as In-Progress and proceed");
          resultObject = new JsonObject();
          resultObject.put("status", "in-progress");
          resultObject.put("message", "Population of resource suggest in-progress");
          UtilityManager.getCache().put("populate-resource-suggestion", resultObject);
          service.submit(new PopulateResourceSuggestThreadExecutor(body));
          context.response().setStatusCode(HttpConstants.HttpStatus.ACCEPTED.getCode())
                  .setStatusMessage(HttpConstants.HttpStatus.ACCEPTED.getMessage());
        } else {
          LOGGER.debug("Returning status from cache");
          resultObject = (JsonObject) UtilityManager.getCache().get("populate-resource-suggestion");
          if (resultObject.getString("status").equalsIgnoreCase("processed")) {
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
