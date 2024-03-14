package nitpeek.core.api.process;

public final class StandardRuleSetTags {

    private StandardRuleSetTags() {
    }

    private static final String CATEGORY_CONTENT = "CONTENT";

    public static RuleSetTag contentAny() {
        return nitpeekTag(
                "ANY_ALL",
                CATEGORY_CONTENT,
                "Marks a rule set as having no restrictions to the type of content it can operate on."
        );
    }

    public static RuleSetTag contentBody() {
        return nitpeekTag(
                "BODY",
                CATEGORY_CONTENT,
                "Marks a rule set as supporting being applied to the body of a document."
        );
    }

    public static RuleSetTag contentFootnotes() {
        return nitpeekTag(
                "FOOTNOTES",
                CATEGORY_CONTENT,
                "Marks a rule set as supporting being applied to the footnotes section of a document."
        );
    }

    private static RuleSetTag nitpeekTag(String tagName, String category, String description) {
        return new RuleSetTag("nitpeek" + "." + category + "." + tagName, description);
    }
}