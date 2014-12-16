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
