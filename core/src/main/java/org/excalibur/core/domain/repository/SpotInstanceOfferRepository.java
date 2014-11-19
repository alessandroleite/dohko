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
package org.excalibur.core.domain.repository;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.SpotInstanceOffer;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferResult;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferStateType;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferStatus;
import org.excalibur.core.cloud.api.domain.SpotType;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.repository.SpotInstanceOfferRepository.SpotInstanceOfferMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(SpotInstanceOfferMapper.class)
public interface SpotInstanceOfferRepository extends Closeable
{
    // -------------------------------------------------------- //
    // ---                 Constants                        --- //
    // -------------------------------------------------------- //
    
    String INSERT_OFFER = "INSERT INTO spot_instance_offer (instance_type_id, provider_id, region_id, owner_id, instance_image_id, offer_price, offer_from, offer_until,\n" +
    		"offer_type_id, number_instances, create_time, user_keyname)\n" +
    		" VALUES ((select it.id from instance_type it where it.name = :instanceType.name), " +
    		" :provider.id, (select r.id from region r where r.name = :region.name), :owner.id, :imageId, :offerValue, :validFrom,\n" +
    		" :validUntil, :type.id, :numberOfInstances, :createTime, :keyName)";
    
    String INSERT_OFFER_STATUS = "INSERT INTO spot_instance_offer_status " +
            " (spot_instance_offer_id, instance_id, spot_offer_state_id, spot_offer_id, offer_status_code, status_time, status_message)" +
            " VALUES (:offerRequest.id, :instance.id, :state.id, :spotRequestId, :status.code, :status.updateTime, :status.message)";

    		
    String QUERY_SELECT_OFFER = "SELECT so.id as spot_instance_offer_id, so.offer_price as offer_value, so.offer_from, so.offer_until,\n" + 
                                " so.offer_type_id, so.number_instances, so.create_time, so.owner_id, so.instance_image_id, so.user_keyname,\n" + 
                                " pr.id as provider_id, pr.name as provider_name, pr.class_name as provider_class_name,\n" + 
                                " it.id as instance_type_id, it.name as instance_type_name,\n"+
                                " r.id as region_id, r.name as region_name\n" +
                                "FROM spot_instance_offer so \n" +
                                " join provider           pr ON pr.id = so.provider_id\n" +
                                " join instance_type      it ON it.id = so.instance_type_id\n" +
                                " join region              r ON  r.id = so.region_id\n";
    
    
    String QUERY_SELECT_ALL_SPOT_OFFERS_STATUSES =            
            "SELECT\n" +
    		" so.id as spot_instance_offer_id, so.offer_price as offer_value, so.offer_from, so.offer_until,\n" +
    		" so.offer_type_id, so.number_instances, so.create_time, so.owner_id, so.instance_image_id, so.user_keyname,\n" +
    		" pr.id as provider_id, pr.name as provider_name, pr.class_name as provider_class_name,\n" +
    		" it.id as instance_type_id, it.name as instance_type_name,\n" +
    		" r.id as region_id, r.name as region_name,\n" +
    		" ofs.id as spot_instance_offer_status_id, ofs.instance_id, ofs.spot_offer_state_id, ofs.spot_offer_id,\n " +
    		" ofs.offer_status_code, ofs.status_time, ofs.status_message\n" +
    		"FROM spot_instance_offer_status ofs \n" +
    		" join spot_instance_offer so ON so.id = ofs.spot_instance_offer_id\n" +
    		" join provider            pr ON pr.id = so.provider_id\n" +
    		" join instance_type       it ON it.id = so.instance_type_id\n" +
    		" join region               r ON  r.id = so.region_id\n";
    
    String QUERY_SELECT_SPOT_OFFERS_BY_STATUS = QUERY_SELECT_ALL_SPOT_OFFERS_STATUSES + 
    		" WHERE\n" +
    		"  ofs.spot_offer_state_id = :state.id AND pr.id = :provider.id AND so.owner_id = :user.id AND\n" +
    		"  ofs.status_time = (SELECT max(status_time) FROM spot_instance_offer_status sof\n" +
    		"                      WHERE sof.spot_offer_state_id = ofs.spot_offer_state_id\n AND" +
    		"                         sof.spot_instance_offer_id = so.id)\n" +
    		"ORDER BY ofs.status_time";
    
    String QUERY_SELECT_LAST_STATUS_OF_SPOT_OFFER = QUERY_SELECT_ALL_SPOT_OFFERS_STATUSES + 
            " WHERE \n" +
            "  so.id = :spotOfferId AND pr.id = :provider.id AND so.owner_id = :user.id AND\n" +
            "  ofs.status_time = (SELECT max(status_time) FROM spot_instance_offer_status sof\n WHERE sof.spot_instance_offer_id = so.id)\n";
    
    // -------------------------------------------------------- //
    // ---                 Update methods                   --- //
    // -------------------------------------------------------- //
    
    @SqlUpdate(INSERT_OFFER)
    @GetGeneratedKeys
    Integer insertSpotInstanceOffer(@BindBean SpotInstanceOffer offer);

    @SqlUpdate(INSERT_OFFER)
    void insertSpotInstanceOffers(@BindBean Collection<SpotInstanceOffer> requests);

    @SqlQuery(QUERY_SELECT_OFFER + " WHERE so.id = :offerId")
    SpotInstanceOffer findSpotInstanceOfferById(@Bind("offerId") Integer offerId);
    
    // -------------------------------------------------------- //
    // ---              Offer status methods                --- //
    // -------------------------------------------------------- //
    
    @SqlUpdate(INSERT_OFFER_STATUS)
    @GetGeneratedKeys
    Integer insertSpotInstanceOfferResult(@BindBean SpotInstanceOfferResult offerStatus);
    
    @SqlBatch(INSERT_OFFER_STATUS)
    @BatchChunkSize(10)
    void insertSpotInstanceOfferResults(@BindBean Collection<SpotInstanceOfferResult> offerStatuses);
    
    @RegisterMapper(SpotInstanceOfferStatusMapper.class)
    @SqlQuery(QUERY_SELECT_ALL_SPOT_OFFERS_STATUSES + " WHERE ofs.id = :offerStatusId")
    SpotInstanceOfferResult findStatusSpotInstanceOfferById(@Bind("offerStatusId") Integer offerStatusId);
    
    @RegisterMapper(SpotInstanceOfferStatusMapper.class)
    @SqlQuery(QUERY_SELECT_LAST_STATUS_OF_SPOT_OFFER)
    SpotInstanceOfferResult getLastStatusOfSpotInstanceOffer(@Bind("spotOfferId") Integer spotOfferId,
            @BindBean(params = { "user.id:id" }) User owner, @BindBean(params = { "provider.id:id" }) Provider provider);
    
    @RegisterMapper(SpotInstanceOfferStatusMapper.class)
    @SqlQuery(QUERY_SELECT_SPOT_OFFERS_BY_STATUS)
    List<SpotInstanceOfferResult> getSpotInstanceOffersOnState(@BindBean(params = { "state.id:id" }) SpotInstanceOfferStateType state,
            @BindBean(params = { "user.id:id" }) User owner, @BindBean(params = { "provider.id:id" }) Provider provider);
    
    
    // -------------------------------------------------------- //
    // ---                  Mapper types                    --- //
    // -------------------------------------------------------- //
    
    static final class SpotInstanceOfferMapper implements ResultSetMapper<SpotInstanceOffer>
    {
        @Override
        public SpotInstanceOffer map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            SpotInstanceOffer request = new SpotInstanceOffer()
                    .setOfferValue(r.getBigDecimal("offer_value"))
                    .setValidFrom(r.getTimestamp("offer_from"))
                    .setValidUntil(r.getTimestamp("offer_until"))
                    .setType(SpotType.valueOf(r.getInt("offer_type_id")))
                    .setNumberOfInstances(r.getInt("number_instances"));
            
            request.setId(r.getInt("spot_instance_offer_id"))
                    .setProvider(new ProviderSupport()
                                    .setId(r.getInt("provider_id"))
                                    .setName(r.getString("provider_name"))
                                    .setServiceClass(r.getString("provider_class_name")))
                   .setImageId(r.getString("instance_image_id"))
                   .setCreateTime(r.getTimestamp("create_time"))
                   .setInstanceType(new InstanceType().setId(r.getInt("instance_type_id"))
                                    .setName(r.getString("instance_type_name"))
                                    .setProvider((ProviderSupport) request.getProvider()))
                   .setKeyName(r.getString("user_keyname"))
                   .setRegion(new Region(r.getInt("region_id"), r.getString("region_name")))
                   .setOwner(new User().setId(r.getInt("owner_id")));
            
            return request;
        }
    }
    
    static final class SpotInstanceOfferStatusMapper implements ResultSetMapper<SpotInstanceOfferResult>
    {
        @Override
        public SpotInstanceOfferResult map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            InstanceTemplate request = new SpotInstanceOfferMapper().map(index, r, ctx);
            
            SpotInstanceOfferResult result = new SpotInstanceOfferResult()
                  .setCreateTime(r.getTimestamp("create_time"))
                  .setId(r.getInt("spot_instance_offer_status_id"))
                  .setOfferRequest((SpotInstanceOffer) request)
                  .setState(SpotInstanceOfferStateType.valueOf(r.getInt("spot_offer_state_id")))
                  .setStatus
                     (
                        new SpotInstanceOfferStatus()
                          .setCode(r.getString("offer_status_code"))
                          .setMessage(r.getString("status_message"))
                          .setUpdateTime(r.getTimestamp("status_time"))
                     );
            return result;
        }
    }
    
    void close();
}
