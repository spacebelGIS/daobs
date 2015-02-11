package org.daobs.harvester.repository;

import org.daobs.harvester.config.Harvester;
import org.daobs.harvester.config.Harvesters;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A really simple harvester repository based on the
 * XML configuration file.
 *
 * Created by francois on 11/02/15.
 */
public class HarvesterConfigRepository implements InitializingBean {
    private Harvesters harvesters;
    private String configurationFilepath;
    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    @Autowired
    ResourceLoader resourceLoader;
    private String harvestingTasksFolder;

    public HarvesterConfigRepository() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadConfig();
    }

    private boolean loadConfig () {
        JAXBContext jaxbContext = null;

        try {
            File configurationFile = new File(configurationFilepath);
            if (configurationFile.exists()) {
                jaxbContext = JAXBContext.newInstance(Harvesters.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                harvesters = (Harvesters) unmarshaller.unmarshal(configurationFile);
            } else {
                // TODO: if no file exist initialized an empty configuration file
                System.out.println("No configuration file available.");
            }
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;
    }
    public boolean reload () {
        return loadConfig();
    }

    public Harvesters getAll () {
        return harvesters;
    }

    public synchronized Harvester add(Harvester harvester) {

        return null;
    }

    public synchronized boolean remove(String harvesterUuid) {

        return false;
    }

    public synchronized boolean start(String harvesterUuid) throws Exception {
        Harvester harvester = findByUuid(harvesterUuid);
        if (harvester == null) {
            throw new Exception(
                    String.format(
                            "No harvester with UUID '%s' found. Can't start it.",
                            harvesterUuid));
        }

        Harvesters harvestingConfig = new Harvesters();
        harvestingConfig.getHarvester().add(harvester);

        File harvestingConfigFile = new File(buildTaskFileName(harvesterUuid));
        JAXBContext jaxbContext = null;

        jaxbContext = JAXBContext.newInstance(Harvesters.class);
        Marshaller marshaller = jaxbContext.createMarshaller();

        FileOutputStream file = null;
        try {
            if (!harvestingConfigFile.exists()) {
                harvestingConfigFile.createNewFile();
            }
            file = new FileOutputStream(harvestingConfigFile);

            marshaller.marshal(harvestingConfig, file);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
            if (file != null) {
                file.close();
            }
        }

        return true;
    }

    private String buildTaskFileName(String harvesterUuid) {
        Date dataStamp = new Date();
        String dataStampString = dateFormat.format(dataStamp);
        return harvestingTasksFolder + File.separator +
                harvesterUuid + "_" +
                dataStampString + ".xml";
    }

    public Harvester findByUuid(String harvesterUuid) {
        for (Harvester harvester : harvesters.getHarvester()) {
            if (harvester.getUuid().equals(harvesterUuid)) {
                return harvester;
            }
        }
        return null;
    }

    public String getConfigurationFilepath() {
        return configurationFilepath;
    }

    public void setConfigurationFilepath(String configurationFilepath) {
        this.configurationFilepath = configurationFilepath;
    }

    public void setHarvestingTasksFolder(String harvestingTasksFolder) {
        this.harvestingTasksFolder = harvestingTasksFolder;
    }

    public String getHarvestingTasksFolder() {
        return harvestingTasksFolder;
    }
}
