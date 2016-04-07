/*
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

package org.daobs.event;

import org.daobs.harvester.config.Harvester;

/**
 * JMS event.
 *
 */
public class HarvestCswEvent extends org.springframework.context.ApplicationEvent {
  private Harvester harvester;


  public HarvestCswEvent(Object source, Harvester harvester) {
    super(source);
    this.harvester = harvester;
  }

  public Harvester getHarvester() {
    return harvester;
  }

  public void setHarvester(Harvester harvester) {
    this.harvester = harvester;
  }
}
