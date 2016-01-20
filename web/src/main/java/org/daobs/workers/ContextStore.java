package org.daobs.workers;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francois on 20/01/16.
 */
public class ContextStore implements CamelContextAware {
    List<CamelContext> camelContexts = new ArrayList<>();

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContexts.add(camelContext);
    }

    @Override
    public CamelContext getCamelContext() {
        return null;
    }

    public List<CamelContext> getCamelContexts() {
        return this.camelContexts;
    }
}
