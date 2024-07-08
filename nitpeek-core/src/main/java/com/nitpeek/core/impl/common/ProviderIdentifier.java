package com.nitpeek.core.impl.common;

import com.nitpeek.core.api.common.Identifier;
import com.nitpeek.core.api.translate.Translation;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ProviderIdentifier implements Identifier {

    private final Supplier<String> idSupplier;
    private final Function<Translation, String> nameSupplier;
    private final Function<Translation, String> descriptionSupplier;

    public ProviderIdentifier(Supplier<String> idSupplier, Function<Translation, String> nameSupplier, Function<Translation, String> descriptionSupplier) {
        this.idSupplier = idSupplier;
        this.nameSupplier = nameSupplier;
        this.descriptionSupplier = descriptionSupplier;
    }

    @Override
    public String getId() {
        return idSupplier.get();
    }

    @Override
    public String getName(Translation translation) {
        return nameSupplier.apply(translation);
    }

    @Override
    public String getDescription(Translation translation) {
        return descriptionSupplier.apply(translation);
    }
}
