package com.company.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="sector-service",url="https://sector--service.herokuapp.com",configuration=FeignConfig.class)
public interface SectorClient {

	@RequestMapping(value = "sector/{sector}", method = RequestMethod.GET)
	public List<String> getAllCompaniesInSector(@PathVariable("sector") String sector); 
}
