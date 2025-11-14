package com.nitpeek.core.api.common;

public final class NoFilter implements PageAwareFeatureFilter {
    @Override
    public boolean test(Feature feature) {
        return true;
    }

    @Override
    public void consumePage(TextPage page) {
        // intentionally NO-OP
    }

    @Override
    public Void finish() {
        return null;
    }
}
