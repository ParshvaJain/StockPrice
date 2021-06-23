package com.company.repository;

import com.company.model.StockPrice;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends MongoRepository<StockPrice,String> {

	List<StockPrice> findBycompanyCode(String code);

	List<StockPrice> findBydateBetween(LocalDate fromPeriod, LocalDate toPeriod);

}
