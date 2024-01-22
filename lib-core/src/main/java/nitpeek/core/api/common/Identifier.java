package nitpeek.core.api.common;

import nitpeek.core.api.translate.Translation;

public interface Identifier {

    String getId();
    String getName(Translation translation);
    String getDescription(Translation translation);
}
