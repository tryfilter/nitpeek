package nitpeek.core.api.common;

import nitpeek.translation.Translation;

public interface Identifier {

    String getId();
    String getName(Translation translation);
    String getDescription(Translation translation);
}
