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
package org.daobs.routing.utility;

import org.apache.camel.Exchange;
import org.apache.camel.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Really simple pagination utility.
 *
 *
 * Created by francois on 10/12/14.
 */
public class Pagination {

    int recordsPerPage;

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    /**
     * Return a list of pages based on the total number of records
     * and the number of records per page.
     *
     * Could be used with a split component to loop over pages.
     *
     * <pre>
     *     <cm:split parallelProcessing="false">
     *       <cm:method bean="pagination" method="getPages"/>
     *       <cm:setHeader headerName="start">
     *          <cm:simple>${bean:pagination?method=getStart}</cm:simple>
     *       </cm:setHeader>
     *       <cm:setHeader headerName="row">
     *           <cm:simple>{{records.per.page}}</cm:simple>
     *       </cm:setHeader>
     * </pre>
     *
     * @param records
     * @return
     */
    public List<String> getPages(@Header("numberOfRecordsMatched") int records) {
        // TODO: This maybe synchronised ?
        List<String> pages = new ArrayList<>();
        int numberOfPages = records / recordsPerPage;
        int remainingRecords = records - (numberOfPages * recordsPerPage);

        int i;
        for (i = 0; i < numberOfPages; i++) {
            pages.add(i + "");
        }
        // Add one more page to collect remaining records
        if (remainingRecords > 0) {
            pages.add(i++ + "");
        }
        return pages;
    }

    public int getStart(Exchange exchange) {
        int page = (Integer) exchange.getProperty("CamelSplitIndex");
        return page * recordsPerPage;
    }
}
