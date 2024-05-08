package com.fireal.web.security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterBlockHolder {

    protected final List<FilterBlock> blocks = new ArrayList<>();

    public List<FilterBlock> getFilterBlocks() {
        return blocks;
    }

    public void addFilterBlock(FilterBlock filterBlock) {
        blocks.add(filterBlock);
    }

    public void addFilterBlock(FilterBlock... filterBlocks) {
        blocks.addAll(Arrays.asList(filterBlocks));
    }
}