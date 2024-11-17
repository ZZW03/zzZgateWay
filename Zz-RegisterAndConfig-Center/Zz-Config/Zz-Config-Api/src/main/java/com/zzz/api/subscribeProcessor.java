package com.zzz.api;


import com.zzz.model.Rule;

import java.util.List;

public interface subscribeProcessor {

    void process(List<Rule> rules);

}
