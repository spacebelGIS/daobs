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
package org.daobs.harvester.repository;

import org.daobs.controller.exception.InvalidHarvesterException;
import org.daobs.harvester.config.Harvester;
import org.daobs.harvester.config.Harvesters;
import org.daobs.utility.UUIDFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A really simple harvester repository based on the
 * XML configuration file.
 *
 * Created by francois on 11/02/15.
 */
public class HarvesterConfigRepository implements InitializingBean {
    private static final SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    @Autowired
    ResourceLoader resourceLoader;
    private Harvesters harvesters;
    private String configurationFilepath;
    private String harvestingTasksFolder;

    public HarvesterConfigRepository() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadConfig();
    }

    private boolean loadConfig() {
        JAXBContext jaxbContext = null;

        try {
            File configurationFile = new File(configurationFilepath);
            if (configurationFile.exists()) {
                jaxbContext = JAXBContext.newInstance(Harvesters.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                harvesters = (Harvesters) unmarshaller.unmarshal(configurationFile);
            } else {
                System.out.println("No configuration file available. " +
                    "Creating empty configuration file.");
                harvesters = new Harvesters();
                commit();
            }
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean reload() {
        return loadConfig();
    }

    public Harvesters getAll() {
        return harvesters;
    }

    public synchronized Harvester addOrUpdate(Harvester harvester) throws Exception {
        if (harvester != null) {
            String uuid = harvester.getUuid();
            boolean harvesterExist = false;
            Harvester harvesterCheck = null;
            if (uuid != null) {
                harvesterCheck = findByUuid(uuid);
                harvesterExist = harvesterCheck != null;
            } else {
                harvester.setUuid(UUIDFactory.getNewUUID());
            }
            accomodate(harvester);

            List<String> listOfErrors = isValid(harvester);
            if (listOfErrors.size() == 0) {
                if (harvesterExist) {
                    harvesters.getHarvester().remove(harvesterCheck);
                }
                harvesters.getHarvester().add(harvester);
                commit();
            } else {
                Exception e = new InvalidHarvesterException(listOfErrors);
                throw e;
            }
        }
        return harvester;
    }

    /**
     *
     * @param harvester
     * @return Return list of errors
     */
    private List<String> isValid(Harvester harvester) {
        List<String> listOfErrors = new ArrayList<>();
        if (harvester.getUuid() == null) {
            listOfErrors.add("Harvester has no UUID.");
            return listOfErrors;
        }
        if (harvester.getUrl() == null) {
            listOfErrors.add(String.format("Harvester with UUID '%s' does not have URL.",
                harvester.getUuid()));
        }
        if (harvester.getFolder() == null) {
            listOfErrors.add(String.format("Harvester with UUID '%s' does not have folder.",
                harvester.getUuid()));
        }
        if (harvester.getTerritory() == null) {
            listOfErrors.add(String.format("Harvester with UUID '%s' does not have territory.",
                harvester.getUuid()));
        }
//        String filter = (String) harvester.getFilter();
//        if (filter != null) {
//            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//            domFactory.setNamespaceAware(true);
//            try {
//                DocumentBuilder builder = domFactory.newDocumentBuilder();
//                builder.parse(new InputSource(
//                                new StringReader(filter)));
//            } catch (Exception e) {
//                listOfErrors.add(String.format("Harvester with UUID '%s' does not have " +
//                                "a valid filter '%s'. Error is: %s.",
//                        harvester.getUuid(), filter, e.getMessage()));
//            }
//        }
        return listOfErrors;
    }

    /**
     * Set defaults for an incomplete harvester
     * <ul>
     *     <li>territory is set to UUID</li>
     *     <li>folder is set to territory</li>
     * </ul>
     * @param harvester
     */
    private void accomodate(Harvester harvester) {
        if (harvester.getTerritory() == null) {
            harvester.setTerritory(harvester.getUuid());
        }
        if (harvester.getFolder() == null) {
            harvester.setFolder(harvester.getTerritory());
        }
    }


    public synchronized boolean remove(String harvesterUuid) throws Exception {
        Harvester harvester = findByUuid(harvesterUuid);
        if (harvester == null) {
            throw new Exception(
                String.format(
                    "No harvester with UUID '%s' found. Can't start it.",
                    harvesterUuid));
        } else {
            harvesters.getHarvester().remove(harvester);
            commit();
        }
        return false;
    }

    private synchronized void commit() {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Harvesters.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(harvesters, new File(configurationFilepath));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        reload();
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
            if (harvesterUuid.equals(harvester.getUuid())) {
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

    public String getHarvestingTasksFolder() {
        return harvestingTasksFolder;
    }

    public void setHarvestingTasksFolder(String harvestingTasksFolder) {
        this.harvestingTasksFolder = harvestingTasksFolder;
    }
}
