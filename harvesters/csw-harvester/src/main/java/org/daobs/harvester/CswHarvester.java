package org.daobs.harvester;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by francois on 9/1/14.
 */
public class CswHarvester {
    class Config {
        public void Config() {

        }
        List<String> pages = new ArrayList<String>();

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


        public int getNumberOfPages() {
            return pages.size();
        }

        public List<String> getPages() {
            return pages;
        }
        public List<String> computeNumberOfPages() {
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

            System.out.println(this);
            System.out.println("numberOfRecordsMatched " + numberOfRecordsMatched + ".");
            System.out.println("maxRecords " + maxRecords + ".");
            System.out.println("numberOfPages " + pages.size() + ".");
            System.out.println("remainingRecords " + remainingRecords + ".");
            return pages;
        }

        public void setNumberOfRecordsMatched(int numberOfRecordsMatched) {
            this.numberOfRecordsMatched = numberOfRecordsMatched;
        }
    }

    private Map<String, Config> harvesters = new HashMap<String, Config>();
    private String getRecordsTemplate = "";

    private int maxRecords = 100;
    public int getMaxRecords() {
        return maxRecords;
    }
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

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

    private Config getConfig(@Header("harvesterUrl") String identifier) {
        Config config = harvesters.get(identifier);
        if (config == null) {
            config = new Config();
            config.setMaxRecords(maxRecords);
            harvesters.put(identifier, config);
        }
        System.out.println(identifier + ">getConfig:" + config);

        return config;
    }

    public void setNumberOfRecords(@Header("harvesterUrl") String identifier, String numberOfRecordsMatched) {
        Config config = getConfig(identifier);
        config.setNumberOfRecordsMatched(Integer.parseInt(numberOfRecordsMatched));
        System.out.println(identifier + ">setNumberOfRecords:" + numberOfRecordsMatched);

    }
    public List<String> getPages(@Header("harvesterUrl") String identifier) {
        Config config = getConfig(identifier);
        return config.computeNumberOfPages();
    }
    public int getNumberOfPages(@Header("harvesterUrl") String identifier) {
        Config config = getConfig(identifier);
        System.out.println(identifier + ">getNumberOfPages:" + config.getPages().size());
        return config.getPages().size();
    }


    public synchronized String generateGetRecordsQuery(@Header("harvesterUrl") String identifier, Exchange exchange) {
        int page = (Integer) exchange.getProperty("CamelSplitIndex");
        Config config = getConfig(identifier);

        int startPosition = page * config.getMaxRecords() + 1;
        System.out.println(identifier + ">generateGetRecordsQuery: page " + page);
        System.out.println(identifier + ">generateGetRecordsQuery:" + startPosition);

        String getRecordsRequestBody =
                this.getRecordsTemplate.
                        replaceAll("\\$\\{maxRecords\\}", config.getMaxRecords() + "");
        getRecordsRequestBody = getRecordsRequestBody.
                replaceAll("\\$\\{startPosition\\}", startPosition + "");

        return getRecordsRequestBody;
    }
}
