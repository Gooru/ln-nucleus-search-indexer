package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;

import javax.sql.DataSource;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult;
import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CollectionRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LessonRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.OriginalResourceRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.RubricRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TenantRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchContentHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchContentHandler.class);
  private final ProcessorContext context;

  public FetchContentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<JsonObject> checkSanity() {
    if (context.getId() == null || context.getId().isEmpty()) {
      LOGGER.debug("checkSanity() failed");
      return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.FAILED);
    }
    LOGGER.debug("checkSanity() passed");
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<JsonObject> validateRequest() {
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<JsonObject> executeRequest() {
    JsonObject result = null;
    String operationName = context.getOperationName();
    LOGGER.debug("Repository operation name : " + operationName);
    try {
      switch (operationName) {
        case ExecuteOperationConstants.GET_RESOURCE:
          result = OriginalResourceRepository.instance().getResource(context.getId());
          break;

        case ExecuteOperationConstants.GET_QUESTION_OR_RESOURCE_REFERENCE:
          result = ContentRepository.instance().getQuestionOrResourceReference(context.getId());
          break;

        case ExecuteOperationConstants.GET_COLLECTION_QUESTION_AND_ORIGINAL_RESOURCE_IDS:
          result = ContentRepository.instance().getQuestionAndOriginalResourceIds(context.getId());
          break;

        case ExecuteOperationConstants.GET_COLLECTION:
          result = CollectionRepository.instance().getCollection(context.getId());
          break;

        case ExecuteOperationConstants.GET_DELETED_RESOURCE:
          result = OriginalResourceRepository.instance().getDeletedContent(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_QUESTION_OR_RESOURCE_REFERENCE:
          result = ContentRepository.instance().getDeletedContent(context.getId());
          break;

        case ExecuteOperationConstants.GET_DELETED_COLLECTION:
          result = CollectionRepository.instance().getDeletedCollection(context.getId());
          break;

        case ExecuteOperationConstants.GET_USER_QUESTIONS:
          result = ContentRepository.instance().getUserQuestions(context.getId());
          break;
        
        case ExecuteOperationConstants.GET_USER_ORIGINAL_RESOURCES:
          result = OriginalResourceRepository.instance().getUserOriginalResources(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_USER_COLLECTIONS:
          result = CollectionRepository.instance().getUserCollections(context.getId());
          break;

        case ExecuteOperationConstants.GET_COURSE:
          result = CourseRepository.instance().getCourse(context.getId());
          break;

        case ExecuteOperationConstants.GET_DELETED_COURSE:
          result = CourseRepository.instance().getDeletedCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_GDT_MAPPING:
          result = TaxonomyRepository.instance().getGdtMapping(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_CROSSWALK:
          result = TaxonomyRepository.instance().getCrosswalkCodes(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_UNIT:
          result = UnitRepository.instance().getUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_UNIT:
          result = UnitRepository.instance().getDeletedUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_LESSON:
          result = LessonRepository.instance().getLesson(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_LESSON:
          result = LessonRepository.instance().getDeletedLesson(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_RUBRIC:
          result = RubricRepository.instance().getRubric(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_RUBRIC:
          result = RubricRepository.instance().getDeletedRubric(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_TAXONOMY_CODE:
          result = TaxonomyCodeRepository.instance().getTaxonomyCode(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_TENANT:
          result = TenantRepository.instance().getTenant(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_GUT:
          result = TaxonomyCodeRepository.instance().getGutCode(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_USER_RESOURCE_REFERENCES:
          result = ContentRepository.instance().getUserCopiedResources(context.getId());
          break;  
          
        case ExecuteOperationConstants.GET_ITEMS_OF_COURSE:
          result = CollectionRepository.instance().getItemsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_ITEM_IDS_OF_COURSE:
          result = CollectionRepository.instance().getDeletedItemIdsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_ITEM_IDS_OF_UNIT:
          result = CollectionRepository.instance().getDeletedItemIdsOfUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_ITEM_IDS_OF_LESSON:
          result = CollectionRepository.instance().getDeletedItemIdsOfLesson(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_UNIT_IDS_OF_COURSE:
          result = UnitRepository.instance().getDeletedUnitsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_LESSON_IDS_OF_COURSE:
          result = LessonRepository.instance().getDeletedLessonsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_LESSON_IDS_OF_UNIT:
          result = LessonRepository.instance().getDeletedLessonsOfUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_RUBRIC_IDS_OF_COURSE:
          result = RubricRepository.instance().getDeletedRubricsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_RUBRIC_IDS_OF_UNIT:
          result = RubricRepository.instance().getDeletedRubricsOfUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_RUBRIC_IDS_OF_LESSON:
          result = RubricRepository.instance().getDeletedRubricsOfLesson(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_DELETED_RUBRIC_IDS_OF_ITEM:
          result = RubricRepository.instance().getDeletedRubricsOfItem(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_ITEMS_OF_UNIT:
          result = CollectionRepository.instance().getItemsOfUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_ITEMS_OF_LESSON:
          result = CollectionRepository.instance().getItemsOfLesson(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_UNITS_OF_COURSE:
          result = UnitRepository.instance().getUnitsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_LESSONS_OF_COURSE:
          result = LessonRepository.instance().getLessonsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_LESSONS_OF_UNIT:
          result = LessonRepository.instance().getLessonsOfUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_RUBRICS_OF_COURSE:
          result = RubricRepository.instance().getRubricsOfCourse(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_RUBRICS_OF_UNIT:
          result = RubricRepository.instance().getRubricsOfUnit(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_RUBRICS_OF_LESSON:
          result = RubricRepository.instance().getRubricsOfLesson(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_RUBRICS_OF_ITEM:
          result = RubricRepository.instance().getRubricsOfItem(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_CONTENTS_OF_COLLECTION:
          result = ContentRepository.instance().getContentsOfItem(context.getId());
          break;
                    
        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          throw new InvalidRequestException();
      }
      if (result != null) {
       // LOGGER.debug("Processed operation : " + operationName + " data : " + result.toString());
        return new ExecutionResult<>(result, ExecutionStatus.SUCCESSFUL);
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch operation " + operationName + " content Id : " + context.getId() + " Exception : " + ex);
    }
    return new ExecutionResult<>(null, ExecutionStatus.FAILED);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  @Override
  public DataSource getDataSource() {
    return DataSourceRegistry.getInstance().getDefaultDataSource();
  }
  
  @Override
  public String getDatabase() {
    return DataSourceRegistry.getInstance().getDefaultDatabase();
  }

}
