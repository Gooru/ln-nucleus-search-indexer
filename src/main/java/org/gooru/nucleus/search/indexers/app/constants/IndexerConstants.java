package org.gooru.nucleus.search.indexers.app.constants;

import java.util.regex.Pattern;

public final class IndexerConstants {

  public static final String TYPE_RESOURCE = "resource";

  public static final String TYPE_QUESTION = "question";

  public static final String TYPE_COLLECTION = "collection";

  public static final String TYPE_ASSESSMENT = "assessment";

  public static final String TYPE_STATISTICS = "statistics";
  
  public static final String TYPE_CONTENT_INFO = "contentinfo";
  
  public static final Pattern RESOURCE_FORMATS = Pattern.compile("resource|question");

  public static final String COLLECTION_IDS = "collection_ids";

  public static final String RESOURCE_IDS = "resource_ids";

  public static final String QUESTION_IDS = "question_ids";

  public static final String PARENT_CONTENT_IDS = "parent_content_ids";

  public static final String ORIGINAL_CONTENT_IDS = "original_content_ids";

  public static final String CODE_ID = "codeId";

  public static final String LABEL = "label";

  public static final String SUBJECT = "subject";

  public static final String COURSE = "course";

  public static final String DOMAIN = "domain";
  
  public static final String STANDARD = "standard";
    
  public static final String CURRICULUM_CODE = "curriculumCode";

  public static final String CURRICULUM_DESC = "curriculumDesc";

  public static final String CURRICULUM_NAME = "curriculumName";

  public static final String CURRICULUM = "curriculum";

  public static final String HYPHEN_SEPARATOR = "-";
  
  public static final String RESOURCES = "resources";
  
  public static final String COLLECTIONS = "collections";

  public static final String LANG_OBJECTIVE = "languageObjective";
  
  public static final String TEXT = "text";
  
  public static final String RESOURCE_INFO = "resourceInfo";
  
  public static final String STATISTICS = "statistics";
  
  public static final String STATISTICS_DOT = "statistics.";

  public static final String COLLECTION_TITLE = "collection_title";

  public static final String RESOURCE_COURSE = "resource_course";
  
  public static final String RESOURCE_COURSE_ID = "resource_course_id";

  public static final String COLLECTION_COURSE = "collection_course";
  
  public static final String COLLECTION_COURSE_ID = "collection_course_id";
  
  public static final String TYPE_COURSE = "course";

  public static final String FRAMEWORK_CODE = "frameworkCode";
  
  public static final String CURRICULUM_INFO = "curriculumInfo";
  
  public static final String PARENT_TITLE = "parentTitle";
    
  public static final String STANDARD_DESC = "standardDesc";
  
  public static final String INTERNAL_CODE = "internalCode";
  
  public static final String LEARNING_TARGET = "learningTarget";
  
  public static final String LEARNING_TARGET_DESC = "learningTargetDesc";
  
  public static final String UNPUBLISH_STATUS = "unpublished";
  
  public static final String EMPTY_ARRAY = "[\"\"]";

  public static final String QUESTIONS = "questions";

  public static final String TWENTY_ONE_CENTURY_SKILL = "twentyOneCenturySkill";

  public static final String COMMA = ",";
  
  public static final String WATSON_TAGS = "watsonTags";

  public static final String INFO_WATSON_TAGS_DOT = "resourceInfo.watsonTags.";
  
  public static final String INDEX_UPDATED_TIME = "indexUpdatedTime";
  
  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  public static final String STR_NULL = "null";

}
