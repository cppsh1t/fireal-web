package com.fireal.web.core;

import java.util.ArrayList;
import java.util.List;

import com.fireal.web.anno.RequestParam;

import fireal.structure.Tuple;

public class RequestParamHolder {
    
    public final List<Tuple<RequestParam, Object>> contents = new ArrayList<>();

}
