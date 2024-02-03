package nitpeek.core.impl.translate;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CoreTranslationKeys {

    MISSING_PAGES_COMPONENT_DESCRIPTION,
    MISSING_PAGES_FEATURE_NAME,
    MISSING_PAGES_FEATURE_DESCRIPTION,
    PROCESSED_PAGES_FEATURE_NAME,
    PROCESSED_PAGES_FEATURE_DESCRIPTION,
    PROCESSED_PAGES_COMPONENT_DESCRIPTION,
    PROCESSED_PAGES_COMPONENT_DESCRIPTION_CHUNK,
    PROCESSED_SINGLE_PAGE_COMPONENT_DESCRIPTION_CHUNK,
    FOUND_FEATURE_NAME,
    DESCRIPTION,
    TEXT_MATCH,
    PAGE,
    LINE,
    CHARACTER,
    AXIS_COMPOUND,
    AXIS_PINPOINT,
    AXIS_START,
    AXIS_END,
    DESCRIBE_PAGE_PROCESSING_INFO_RULE_NAME,
    DESCRIBE_PAGE_PROCESSING_INFO_RULE_DESCRIPTION,
    APPLIED_RULE_NAME,
    APPLIED_RULE_DESCRIPTION,
    REPLACE_LITERAL_FEATURE_NAME,
    REPLACE_LITERAL_FEATURE_DESCRIPTION,
    REPLACE_LITERAL_COMPONENT_DESCRIPTION,
    REPLACE_LITERAL_COMPONENT_DESCRIPTION_DELETE,
    DEBUG_FEATURE_NAME,
    DEBUG_FEATURE_DESCRIPTION,
    REPLACE_REGEX_FEATURE_NAME,
    REPLACE_REGEX_FEATURE_DESCRIPTION,
    UNPAIRED_PARENTHESES_FEATURE_NAME,
    UNPAIRED_PARENTHESES_FEATURE_DESCRIPTION,
    UNPAIRED_OPEN_PARENTHESIS_COMPONENT_DESCRIPTION,
    UNPAIRED_CLOSING_PARENTHESIS_COMPONENT_DESCRIPTION,
    PLUGIN_ALL_RULES_ID_NAME,
    PLUGIN_ALL_RULES_ID_DESCRIPTION,
    ;

    public static CoreTranslationKeys getForKey(String key) {
        return keyMapping.get(key);
    }

    private static final Map<String, CoreTranslationKeys> keyMapping =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(CoreTranslationKeys::key, key -> key));

    public String key() {
        return "nitpeek.core." + name();
    }

}
