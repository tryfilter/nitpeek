package com.nitpeek.core.impl.common;

import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.translate.Translation;

public record SimpleIdentifier(String id, String nameTranslationKey, String descriptionTranslationKey) implements Identifier {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName(Translation translation) {
        return translation.translate(nameTranslationKey);
    }

    @Override
    public String getDescription(Translation translation) {
        return translation.translate(descriptionTranslationKey);
    }
}
