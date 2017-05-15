package com.luxoft.p6listeners;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

public class ChunkListener
{

    @BeforeChunk
    public void before(ChunkContext context)
    {
        System.out.println("===>>> Before chunk");
    }

    @AfterChunk
    public void after(ChunkContext context)
    {
        System.out.println("===>>> After chunk");
    }

}
