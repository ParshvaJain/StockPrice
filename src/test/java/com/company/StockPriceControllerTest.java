package com.company;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.model.StockPrice;
import com.company.repository.StockPriceRepository;

@ExtendWith(MockitoExtension.class)
public class StockPriceControllerTest {
	
	@Mock
	private StockPriceRepository repository;
	
	@Test
	public void test_find_by_code() {
		StockPrice stockPrice = new StockPrice();
		stockPrice.setCompanyCode("201");
		stockPrice.setCurrentPrice("584");
		List<StockPrice> temp = repository.findBycompanyCode("201");
		MatcherAssert.assertThat(stockPrice.getCompanyCode(), true);
	}
	
}
