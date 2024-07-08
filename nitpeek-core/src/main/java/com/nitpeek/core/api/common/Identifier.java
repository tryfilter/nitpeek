package com.nitpeek.core.api.common;

import com.nitpeek.core.api.translate.Translation;

public interface Identifier {

    String getId();
    String getName(Translation translation);
    String getDescription(Translation translation);
}
