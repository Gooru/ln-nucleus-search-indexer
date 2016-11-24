package org.gooru.nucleus.search.indexers.app.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class InternalHelper {

    private static final Logger LOG = LoggerFactory.getLogger(InternalHelper.class);

    public static List<String> getAppropriateTags(JsonArray tags, String type) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<String>();
        }
        JsonObject conceptsTags = tags.getJsonObject(0);
        JsonObject taxonomyTags = tags.getJsonObject(1);
        JsonObject keywordsTags = tags.getJsonObject(2);
        JsonObject entitiesTags = tags.getJsonObject(3);

        switch (type) {
        case "concepts":
            List<String> concepts = new ArrayList<>();
            Iterator<Object> cIter = conceptsTags.getJsonArray("concepts").iterator();
            while (cIter.hasNext()) {
                JsonObject concept = (JsonObject) cIter.next();
                concepts.add(concept.getString("text"));
            }
            return concepts;
        case "taxonomy":
            List<String> taxonomies = new ArrayList<>();
            Iterator<Object> tIter = taxonomyTags.getJsonArray("taxonomy").iterator();
            while (tIter.hasNext()) {
                JsonObject taxonomy = (JsonObject) tIter.next();
                taxonomies.add(taxonomy.getString("label"));
            }
            return taxonomies;
        case "entities":
            List<String> entities = new ArrayList<>();
            Iterator<Object> eIter = entitiesTags.getJsonArray("entities").iterator();
            while (eIter.hasNext()) {
                JsonObject entity = (JsonObject) eIter.next();
                entities.add(entity.getString("text") + ":" + entity.getString("type"));
            }
            return entities;
        case "keywords":
            List<String> keywords = new ArrayList<>();
            Iterator<Object> kIter = keywordsTags.getJsonArray("keywords").iterator();
            while (kIter.hasNext()) {
                JsonObject concept = (JsonObject) kIter.next();
                keywords.add(concept.getString("text"));
            }
            return keywords;
        }

        return new ArrayList<>();

    }

    public static JsonArray parseWatsonTags(JsonArray enhancedMetadata) {
      Iterator<Object> iter = enhancedMetadata.iterator();
        while (iter.hasNext()) {
            JsonObject cur = (JsonObject) iter.next();
            if (cur.getString("source").equals("watson")) {
                return cur.getJsonArray("result");
            }
        }
        return null;
    }

}
