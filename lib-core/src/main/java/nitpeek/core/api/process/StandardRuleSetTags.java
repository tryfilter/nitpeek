package nitpeek.core.api.process;

public final class StandardRuleSetTags {

    private StandardRuleSetTags() {
    }

    public enum Category {
        CONTENT,
        LANGUAGE;

        public String getId() {
            return "nitpeek" + "." + name();
        }
    }

    public static RuleSetTag contentAny() {
        return nitpeekTag(
                "ANY_ALL",
                Category.CONTENT,
                "Marks a rule set as having no restrictions to the type of content it can operate on."
        );
    }

    public static RuleSetTag contentBody() {
        return nitpeekTag(
                "BODY",
                Category.CONTENT,
                "Marks a rule set as supporting being applied to the body of a document."
        );
    }

    public static RuleSetTag contentFootnotes() {
        return nitpeekTag(
                "FOOTNOTES",
                Category.CONTENT,
                "Marks a rule set as supporting being applied to the footnotes section of a document."
        );
    }

    public static RuleSetTag languageAny() {
        return nitpeekTag(
                "ANY",
                Category.LANGUAGE,
                "Marks a rule set as supporting being applied to a document with content in any language."
        );
    }

    public static RuleSetTag languageEnglish() {
        return nitpeekTag(
                "EN",
                Category.LANGUAGE,
                "Marks a rule set as supporting being applied to a document with english content."
        );
    }

    public static RuleSetTag languageGerman() {
        return nitpeekTag(
                "DE",
                Category.LANGUAGE,
                "Marks a rule set as supporting being applied to a document with german content."
        );
    }

    private static RuleSetTag nitpeekTag(String tagName, Category category, String description) {
        return new RuleSetTag(category.getId(), tagName, description);
    }
}