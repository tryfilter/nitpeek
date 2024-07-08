package com.nitpeek.core.api.process;

public interface ProcessorWithResult<R> extends Processor {
    R getProcessingResult();
}