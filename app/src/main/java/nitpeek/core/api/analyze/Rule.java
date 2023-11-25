package nitpeek.core.api.analyze;

/**
 * A rule with a name and a description.<br>
 * Note that although there is a single description per rule, many Problem instances of potentially different types can
 * be produced by the rule's Analyzer.<br>
 * Each Problem has its own name and description which are not necessarily related
 * to the name and description of the rule.
 */
public interface Rule {

    String getName();

    String getDescription();

    /**
     * @return an Analyzer that identifies violations of this Rule.
     * Note that one Analyzer can identify many different kinds of Problems.
     */
    Analyzer getAnalyzer();
}
