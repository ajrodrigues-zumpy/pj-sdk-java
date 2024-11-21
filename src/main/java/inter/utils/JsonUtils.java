package inter.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class JsonUtils {

  private JsonUtils() {}

  public static ObjectMapper getObjectMapper() {
    ObjectMapper mapper =
        JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)

            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)

            .configure(SerializationFeature.CLOSE_CLOSEABLE, true)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .build();

    mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    return mapper;
  }

  public static <T> T read(String json, Class<T> cls) throws JsonProcessingException {
    ObjectMapper mapper = getObjectMapper();
    return mapper.readValue(json, cls);
  }

  public static <T> T read(String json, final TypeReference<T> type)
      throws JsonProcessingException {
    ObjectMapper mapper = JsonUtils.getObjectMapper();
    return mapper.readValue(json, type);
  }

  public static String write(Object object) throws JsonProcessingException {
    ObjectMapper mapper = getObjectMapper();
    return mapper.writeValueAsString(object);
  }

  public static String writePretty(Object object) throws JsonProcessingException {
    ObjectMapper mapper = getObjectMapper();
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
  }

}
