/**
 *     Copyright (C) 2013-2014  the original author or authors.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License,
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.excalibur.core.test.domain.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.excalibur.core.Status;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.InstanceTemplateStatus;
import org.excalibur.core.cloud.api.domain.SpotInstanceOffer;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferResult;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferStateType;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferStatus;
import org.excalibur.core.cloud.api.domain.SpotType;
import org.excalibur.core.domain.repository.SpotInstanceOfferRepository;
import org.excalibur.core.test.MockProvider;
import org.excalibur.core.test.TestSupport;
import org.junit.Test;

import ch.vorburger.exec.ManagedProcessException;

public class SpotInstanceOfferRepositoryTest extends TestSupport
{
    private SpotInstanceOfferRepository spotInstanceOfferRepository;

    @Override
    public void setup() throws java.io.IOException, ManagedProcessException
    {
        super.setup();
        this.spotInstanceOfferRepository = openRepository(SpotInstanceOfferRepository.class);
    };
    
    
    /**
     * TODO Please, consider to refactor this method. It is doing different chooses.
     */
    @Test
    public void must_insert_one_spot_instance_offer()
    {
        SpotInstanceOffer offer = new SpotInstanceOffer().setOfferValue("0.0021").setType(SpotType.ONE_TIME);
        offer.setCreateTime(new Date()).setImageId("ami-59a4a230").setKeyName("leite").setOwner(user).setProvider(new MockProvider());
        offer.setInstanceType("t1.micro").setRegion(new Region("us-east-1").setId(100)).setMaxCount(1);
        offer.setStatus(new InstanceTemplateStatus().setStatus(Status.SUCCESS).setStatusTime(new Date()));
        
        Integer offerId = spotInstanceOfferRepository.insertSpotInstanceOffer(offer);
        assertNotNull(offerId);
        assertThat(offerId, equalTo(1));
        
        SpotInstanceOffer offer2 = spotInstanceOfferRepository.findSpotInstanceOfferById(offerId);
        assertNotNull(offer2);
        
        offerId = spotInstanceOfferRepository.insertSpotInstanceOffer(offer2);
        offer2.setId(offerId);
        
        SpotInstanceOfferResult offerResultStatus = new SpotInstanceOfferResult();
        offerResultStatus.setCreateTime(new Date()).setOfferRequest(offer2).setSpotRequestId("spt-ad20").setState(SpotInstanceOfferStateType.OPEN);
        offerResultStatus.setStatus(new SpotInstanceOfferStatus().setCode("100").setMessage("open").setUpdateTime(new Date()));
        
        Integer offerStatusId = spotInstanceOfferRepository.insertSpotInstanceOfferResult(offerResultStatus);
        assertNotNull(offerStatusId);
        
        offerResultStatus = new SpotInstanceOfferResult();
        offerResultStatus.setCreateTime(new Date()).setOfferRequest(offer2).setSpotRequestId("spt-ad20").setState(SpotInstanceOfferStateType.FAILED);
        offerResultStatus.setStatus(new SpotInstanceOfferStatus().setCode("500").setMessage("price-too-low").setUpdateTime(new Date()));
        
        Integer spotOfferStatusId = spotInstanceOfferRepository.insertSpotInstanceOfferResult(offerResultStatus);
        
        SpotInstanceOfferResult statu_ = spotInstanceOfferRepository.findStatusSpotInstanceOfferById(spotOfferStatusId);
        assertThat(spotOfferStatusId, equalTo(statu_.getId()));
        
        SpotInstanceOfferResult lastStatusOfSpotInstanceOffer = spotInstanceOfferRepository.getLastStatusOfSpotInstanceOffer(offer2.getId(),
                offer2.getOwner(), offer2.getProvider());
        
        //FIXME this code must be refactored
//        assertThat(spotOfferStatusId, equalTo(lastStatusOfSpotInstanceOffer.getId()));
        
        List<SpotInstanceOfferResult> spotInstanceOffersOnState = 
                spotInstanceOfferRepository.getSpotInstanceOffersOnState(SpotInstanceOfferStateType.OPEN, user, offer.getProvider());
        
        assertThat(1, equalTo(spotInstanceOffersOnState.size()));
        
    }
}
