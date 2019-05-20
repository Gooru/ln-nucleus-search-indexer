package org.gooru.nucleus.search.indexers.app.constants;

import java.util.regex.Pattern;

public final class IndexerConstants {

  public static final String TYPE_RESOURCE = "resource";

  public static final String TYPE_QUESTION = "question";

  public static final String TYPE_COLLECTION = "collection";

  public static final String TYPE_ASSESSMENT = "assessment";
  
  public static final String TYPE_OFFLINE_ACTIVITY = "offline-activity";

  public static final String TYPE_STATISTICS = "statistics";
  
  public static final String TYPE_CONTENT_INFO = "contentinfo";
  
  public static final String TYPE_TAXONOMY = "taxonomy";

  public static final String TYPE_GUT = "gut";

  public static final String TYPE_TENANT = "tenant";
  
  public static final String TYPE_RESOURCE_REFERENCE = "resource-reference";

  public static final Pattern RESOURCE_FORMATS = Pattern.compile("resource|question|resource-reference");

  public static final Pattern COLLECTION_FORMATS = Pattern.compile("collection|assessment|assessment-external|collection-external|offline-activity");

  public static final String COLLECTION_IDS = "collection_ids";

  public static final String RESOURCE_IDS = "resource_ids";

  public static final String QUESTION_IDS = "question_ids";

  public static final String PARENT_CONTENT_IDS = "parent_content_ids";

  public static final String ORIGINAL_CONTENT_IDS = "original_content_ids";

  public static final String RESOURCE_REFERENCES_IDS = "resource_reference_ids";

  public static final String RESOURCE_REFERENCES = "resource_references";
  
  public static final String SUBJECT = "subject";

  public static final String COURSE = "course";

  public static final String DOMAIN = "domain";
  
  public static final String STANDARD = "standard";

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
      
  public static final String LEARNING_TARGET = "learningTarget";
  
  public static final String UNPUBLISH_STATUS = "unpublished";
  
  public static final String EMPTY_ARRAY = "[\"\"]";

  public static final String EMPTY_OBJECT = "{}";

  public static final String QUESTIONS = "questions";

  public static final String COMMA = ",";
  
  public static final String WATSON_TAGS = "watsonTags";

  public static final String INFO_WATSON_TAGS_DOT = "resourceInfo.watsonTags.";
  
  public static final String INDEX_UPDATED_TIME = "indexUpdatedTime";
  
  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  public static final String STR_NULL = "null";
  
  public static final String TENANT_ID = "tenantId";

  public static final String TENANT_ROOT_ID = "tenantRootId";
    
  public static final String TYPE_CROSSWALK = "crosswalk";

  public static final String UNIT = "unit";

  public static final String LESSON = "lesson";

  public static final String COLLECTION = "collection";

  public static final String ASSESSMENT = "assessment";

  public static final String ASSESSMENT_EXTERNAL = "assessment-external";
  
  public static final String COLLECTION_EXTERNAL = "collection-external";

  public static final String TYPE_UNIT = "unit";
  
  public static final String TYPE_LESSON = "lesson";
  
  public static final Pattern MATCH_CUL_CW = Pattern.compile("course|unit|lesson|crosswalk");

  public static final String PUBLISHED = "published";

  public static final String FEATURED = "featured";

  public static final String TYPE_RUBRIC = "rubric";
  
  public static final String _LEARNING_TARGET = "learning_target";
  
  public static final String STOP_WORDS = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";

  public static final String REGEXP = "[^a-zA-Z0-9\\']";
  
  public static final String KEYWORD = "keyword";
  
  public static final String PUBLISHER = "publisher";
  
  public static final String REGEXP_NON_WORDS = "[^a-zA-Z0-9]";

  public static final String EMPTY_STRING = "";
  
  public static final String CAPS_CROSSWALK_CODES = "CROSSWALK_CODES";

  public static final Pattern STANDARD_MATCH = Pattern.compile("standard_level_1|standard_level_2");
  
  public static final String LEARNING_TARGET_TYPE_0 = "learning_target_level_0";
  
  public static final String ABOVE_AVERAGE = "H";
  
  public static final String AVERAGE = "M";
  
  public static final String BELOW_AVERAGE = "L";
  
  public static final String GUT_FRAMEWORK = "GDT";

  public static final String ACTIVE = "active";
  
  public static final Pattern STATIC_CONTENT_MATCH = Pattern.compile("rubric|taxonomy|tenant|crosswalk");

  public static final String SHORT_NAME = "shortName";

  public static final String[] TW_FRAMEWORKS = new String[] {"hewlett_deep_learning_model", "conley_four_keys_model","p21_framework_model","national_research_center_model"};

  public static final String VALUE = "value";
  
  public static final String TWCS = "21cs";

  public static final String UNDERSCORE = "_";
  
  public static final String PRIMARY_LANGUAGE = "primaryLanguage";
  
  public enum LMContentFormat {

    RESOURCE("resource"),
    QUESTION("question"),
    COLLECTION("collection"),
    ASSESSMENT("assessment"),
    ASSESSMENT_EXTERNAL("ext_assessment"),
    COLLECTION_EXTERNAL("ext_collection"),
    OFFLINE_ACTIVITY("offline_activity"),
    RUBRIC("rubric"),
    COURSE("course"),
    UNIT("unit"),
    LESSON("lesson");

    private String contentFormat;

    LMContentFormat(String contentFormat) {
      this.contentFormat = contentFormat;
    }

    public String getContentFormat() {
      return this.contentFormat;
    }

  }


}
