package com.company.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value="company-service",url="https://company--service.herokuapp.com",configuration=FeignConfig.class)
public interface CompanyClient {
	
	@RequestMapping(value = "/stockexchange/addCompanyToExchange/{exchange}/{company}", method = RequestMethod.GET)
	public ResponseEntity<?> addCompanyToExchange(@PathVariable("exchange") String exchange,@PathVariable("company") String company);
	
	@RequestMapping(value = "company/giveCompany/{exchange}/{code}", method = RequestMethod.GET)
	public ResponseEntity<String> giveCompany(@PathVariable("exchange") String exchange,@PathVariable("code") String code) ;
	
	@RequestMapping(value = "company/giveCode/{company}/{exchange}", method = RequestMethod.GET)
	public ResponseEntity<String> giveCode(@PathVariable("company") String company,@PathVariable("exchange") String exchange); 
}
