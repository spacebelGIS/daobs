package org.daobs.harvester;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.camel.Exchange;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francois on 9/1/14.
 */
public class CswHarvester {
    private String getRecordsTemplate = "";
    List<String> pages = new ArrayList<String>();

    public CswHarvester() {
        String fileName = "csw-get-records.xml";
        try {
            getRecordsTemplate = Files.toString(
                    new ClassPathResource(fileName).getFile(),
                    Charsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Can't find '" + fileName + "'.");
        }
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    private int maxRecords = 20;
    private int startPosition = 1;
    private int numberOfRecordsMatched;

    public void setNumberOfRecords(String numberOfRecordsMatched) {
        this.numberOfRecordsMatched = Integer.parseInt(numberOfRecordsMatched);
    }

    public int getNumberOfPages() {
        return pages.size();
    }

    ;

    public List<String> getPages() {
        int numberOfPages = numberOfRecordsMatched / maxRecords;
        int remainingRecords = numberOfRecordsMatched - (numberOfPages * maxRecords);

        int i;
        for (i = 0; i < numberOfPages; i++) {
            pages.add(i + "");
        }

        // Add one more page to collect remaining records
        if (remainingRecords > 0) {
            pages.add(i++ + "");
        }
        System.out.println("numberOfRecordsMatched " + numberOfRecordsMatched + "'.");
        System.out.println("maxRecords " + maxRecords + "'.");
        System.out.println("numberOfPages " + pages.size() + "'.");
        System.out.println("remainingRecords " + remainingRecords + "'.");
        return pages;
    }

    public String generateGetRecordsQuery(Exchange exchange) {
        int page = (Integer) exchange.getProperty("CamelSplitIndex");
        int startPosition = this.startPosition + (page * this.maxRecords);

        String getRecordsRequestBody =
                this.getRecordsTemplate.
                        replaceAll("\\$\\{maxRecords\\}", this.maxRecords + "");
        getRecordsRequestBody = getRecordsRequestBody.
                replaceAll("\\$\\{startPosition\\}", startPosition + "");

        return getRecordsRequestBody;
    }
}
