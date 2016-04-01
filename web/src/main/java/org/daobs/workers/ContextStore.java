/**
 * Copyright 2014-2016 European Environment Agency
 *
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
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
    public CamelContext getCamelContext() {
        return null;
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContexts.add(camelContext);
    }

    public List<CamelContext> getCamelContexts() {
        return this.camelContexts;
    }
}
