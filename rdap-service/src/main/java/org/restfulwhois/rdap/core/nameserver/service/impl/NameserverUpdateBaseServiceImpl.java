/*
 * Copyright (c) 2012 - 2015, Internet Corporation for Assigned Names and
 * Numbers (ICANN) and China Internet Network Information Center (CNNIC)
 * 
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  
 * * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * * Neither the name of the ICANN, CNNIC nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL ICANN OR CNNIC BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.restfulwhois.rdap.core.nameserver.service.impl;

import static org.restfulwhois.rdap.common.util.UpdateValidateUtil.MAX_LENGTH_LDHNAME;
import static org.restfulwhois.rdap.common.util.UpdateValidateUtil.MAX_LENGTH_UNICODENAME;

import java.util.List;

import org.restfulwhois.rdap.common.dao.UpdateDao;
import org.restfulwhois.rdap.common.dto.NameserverDto;
import org.restfulwhois.rdap.common.dto.embedded.IpAddressDto;
import org.restfulwhois.rdap.common.model.Nameserver;
import org.restfulwhois.rdap.common.service.AbstractUpdateService;
import org.restfulwhois.rdap.common.util.BeanUtil;
import org.restfulwhois.rdap.common.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * create service implementation.
 * 
 * @author jiashuo
 * 
 */
public abstract class NameserverUpdateBaseServiceImpl extends
        AbstractUpdateService<NameserverDto, Nameserver> {
    @Autowired
    protected UpdateDao<Nameserver, NameserverDto> nameserverDao;
    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(NameserverUpdateBaseServiceImpl.class);

    protected Nameserver convertDtoToModel(NameserverDto dto) {
        Nameserver Nameserver = convertDtoToNameserver(dto);
        super.convertCustomProperties(dto, Nameserver);
        return Nameserver;
    }

    protected void saveIpAddresses(Nameserver Nameserver) {
        LOGGER.debug("save ipAddresses ...");
        nameserverDao.saveRel(Nameserver);
    }

    protected void deleteIpAddresses(Nameserver Nameserver) {
        LOGGER.debug("delete ipAddresses ...");
        getNameserverDao().deleteRel(Nameserver);
    }

    protected void updateIpAddresses(Nameserver Nameserver) {
        deleteIpAddresses(Nameserver);
        saveIpAddresses(Nameserver);
    }

    private Nameserver convertDtoToNameserver(NameserverDto dto) {
        Nameserver Nameserver = new Nameserver();
        BeanUtil.copyProperties(dto, Nameserver, "entities", "events",
                "remarks", "links");
        return Nameserver;
    }

    protected ValidationResult validateForSaveAndUpdate(NameserverDto dto,
            ValidationResult validationResult) {
        checkNotEmptyAndMaxLength(dto.getLdhName(), MAX_LENGTH_LDHNAME,
                "ldhName", validationResult);
        checkNotEmptyAndMaxLengthForHandle(dto.getHandle(), validationResult);
        checkMaxLength(dto.getUnicodeName(), MAX_LENGTH_UNICODENAME,
                "unicodeName", validationResult);
        checkIpAddress(dto, validationResult);
        validateBaseDto(dto, validationResult);
        return validationResult;
    }

    private void checkIpAddress(NameserverDto dto,
            ValidationResult validationResult) {
        IpAddressDto ipAddressDto = dto.getIpAddresses();
        if (null == ipAddressDto) {
            return;
        }
        checkIpList(ipAddressDto.getIpList(), "ipAddresses", validationResult);
    }

    private void checkIpList(List<String> ipList, String fieldName,
            ValidationResult validationResult) {
        if (validationResult.hasError()) {
            return;
        }
        if (null != ipList) {
            for (String ip : ipList) {
                checkIp(ip, fieldName, validationResult);
            }
        }
    }

    public UpdateDao<Nameserver, NameserverDto> getNameserverDao() {
        return nameserverDao;
    }

}
