package com.company.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus;

import com.company.feign.CompanyClient;
import com.company.feign.SectorClient;
import com.company.message.ResponseMessage;
import com.company.model.StockPrice;
import com.company.repository.StockPriceRepository;

@CrossOrigin(origins ="https://reactapp--service.herokuapp.com")
@Controller
@RequestMapping("/excel")
public class ExcelController {
	
	@Autowired
	StockPriceRepository repository;
	
	@Autowired
	private CompanyClient companyClient;
	
	@Autowired
	private SectorClient sectorClient;
	
	List<StockPrice> getDataFromDB(String code,String exchange,LocalDate fromPeriod,LocalDate toPeriod){
		//Query query = new Query();
		//List<Criteria> criteria = new ArrayList<>();
		
		//criteria.add(Criteria.where("companyCode").is(code));
		//criteria.add(Criteria.where("stockExchange").is(exchange));
		//criteria.add(Criteria.where("fromPeriod").gt(fromPeriod));
		//criteria.add(Criteria.where("toPeriod").lt(toPeriod));
		
		//query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
		//List<StockPrice> filtered = mongoTemplate.find(query,StockPrice.class);
		//System.out.println(filtered);
		List<StockPrice> g = repository.findBydateBetween(fromPeriod, toPeriod);
		int length = g.size();
		List<StockPrice> filtered = new ArrayList<>();
		System.out.println(g);
		for(int i=0;i<length;i++) {
			if(g.get(i).getCompanyCode().equals(code) && g.get(i).getStockExchange().equals(exchange)) {
				filtered.add(g.get(i));
			}
		}
		return filtered;
	}
	
	@RequestMapping(value="/getCompaniesByDate",method = RequestMethod.POST)
	public ResponseEntity<List<StockPrice>> getCompaniesByDate(@RequestBody String requiredDetails){
		System.out.println(requiredDetails);
		JSONObject jsonObj = new JSONObject(requiredDetails);
	    //System.out.println(jsonObj.get("company"));    
	    String company = (String) jsonObj.get("company");
	    String exchange = (String) jsonObj.get("exchange");
	    
	    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	    LocalDate fromPeriod = LocalDate.parse((String)jsonObj.get("fromPeriod"));
	    LocalDate toPeriod = LocalDate.parse((String)jsonObj.get("toPeriod"));
	    
	    String periodicity = (String) jsonObj.get("periodicity");
	    
	    ResponseEntity<String> code = companyClient.giveCode(company,exchange);
	    String companyCode = code.getBody();
	    
	    List<StockPrice> ex = getDataFromDB(companyCode,exchange,fromPeriod,toPeriod);
	    System.out.println(ex);
	    return new ResponseEntity<>(ex,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getSectorByDate",method = RequestMethod.POST)
	public ResponseEntity<List<StockPrice>> getSectorsByDate(@RequestBody String requiredDetails){
		System.out.println(requiredDetails);
		JSONObject jsonObj = new JSONObject(requiredDetails);
	    //System.out.println(jsonObj.get("company"));    
	    String sector = (String) jsonObj.get("sector");
	     //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	    LocalDate fromPeriod = LocalDate.parse((String)jsonObj.get("fromPeriod"));
	    LocalDate toPeriod = LocalDate.parse((String)jsonObj.get("toPeriod"));
	    String periodicity = (String) jsonObj.get("periodicity");
	    
	    //Get All Companies of the sector
	    List<String> companiesInSector = sectorClient.getAllCompaniesInSector(sector);
	    
	   
	    //Get All codes of the companies
	    int length = companiesInSector.size();
	    List<String> codesOfCompanies = new ArrayList<>();
	    
	    for(int i=0;i<length;i++) {
	    	codesOfCompanies.add(companyClient.giveCode(companiesInSector.get(i),"NSE").getBody());
	    }
	    
	    
	    List<StockPrice> returnValue = new ArrayList<>();
	    List<List<StockPrice>> listOfList = new ArrayList<>();
	    for(int i=0;i<length;i++) {
	    	List<StockPrice> temp = getDataFromDB(codesOfCompanies.get(i),"NSE",fromPeriod,toPeriod);
	    	listOfList.add(temp);
	    }
	    
	    int arrayLength = listOfList.get(0).size();
	    List<Double> averageStored = new ArrayList<Double>(Collections.nCopies(arrayLength, 0.0));
	   
	    for(int i=0;i<length;i++) {
	    	List<StockPrice> temp = listOfList.get(i);
	    	int tempLength = temp.size();
	    	for(int j=0;j<tempLength;j++) {
	    		averageStored.set(j,averageStored.get(j) + Double.parseDouble(temp.get(j).getCurrentPrice()));
	    	}
	    }
	    
	    int averageLength = averageStored.size();
	    for(int i=0;i<averageLength;i++) {
	    	averageStored.set(i, averageStored.get(i) / 3);
	    }
	    returnValue = listOfList.get(0);
	    int len = returnValue.size();
	    for(int i=0;i<len;i++) {
	    	returnValue.get(i).setCurrentPrice(Double.toString(averageStored.get(i)));
	    }
	    return new ResponseEntity<>(returnValue,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<ResponseMessage> uploadFile(@RequestBody String array) {
		
		array = array.substring(1,array.length()-1);
		array = array.replace("[", "");
		String[] res = array.split("],");
		ArrayList<String> finalRes = new ArrayList<String>();
		for(String data : res) {
			if(!data.isEmpty())
				finalRes.add(data);
		}
		
		int length = finalRes.size();
		for(int i = 1; i < length; i++) {
			String[] temp = finalRes.get(i).split(",");
			temp[0] = temp[0].substring(1,temp[0].length()-1);
			temp[1] = temp[1].substring(1,temp[1].length()-1);
			temp[2] = temp[2].substring(1,temp[2].length()-1);
			temp[3] = temp[3].substring(1,temp[3].length()-1);
			temp[4] = temp[4].substring(1,temp[4].length()-1);
			String companyCode = temp[0].trim();
			String stockExchange = temp[1];
			String currentPrice = temp[2];
			LocalDate date = LocalDate.parse(temp[3]);
			
			//String dateinStr = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			System.out.println(date);
			//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
			//LocalDate date = LocalDate.parse(temp[3],formatter);
			String time = temp[4].trim();
			StockPrice stockPrice = new StockPrice(companyCode,stockExchange,currentPrice,date,time);
			repository.save(stockPrice);
		}
		
		String message = "Done!";
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseMessage(message));
	}
	
	
	
}
