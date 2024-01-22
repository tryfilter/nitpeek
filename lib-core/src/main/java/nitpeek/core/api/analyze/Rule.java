package nitpeek.core.api.analyze;

/**
 * A rule with an id, a name and a description.<br>
 * Note that although there is a single description per rule, many Feature instances of potentially different types can
 * be produced by the rule's Analyzer.<br>
 * Each Feature has its own name and description which are not necessarily related
 * to the name and description of the rule.
 * A rule has an abstract description of its intent, while a feature has a description specific to the way in
 * which it conforms to the rule, potentially including suggested actions.
 */
public interface Rule {

    RuleType getType();

    /**
     * @return an Analyzer that identifies instances conforming to this Rule<br>
     * <br>
     * Note that one Analyzer may identify many different types of Feature.
     */
    Analyzer getAnalyzer();
}
