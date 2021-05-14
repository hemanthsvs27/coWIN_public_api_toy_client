package com.api.client.api.service;

import java.util.List;

import com.api.client.api.model.Center;
import com.api.client.api.model.CenterWrap;

/**
 * @author Hemu
 *
 */
@org.springframework.stereotype.Service
public interface Service {
	/**
	 * Makes a call to CalenderByDistrict API 
	 * Filters based on availability
	 * Filters based on type of vaccine (taken from properties file)
	 * 
	 * Request date is in dd-MM-yyyy format
	 * 
	 * 
	 * @param pincode
	 * @param requestDate
	 * @return List<Center>
	 */
	List<CenterWrap> getFilteredCentersByPincode(Integer pincode, String date);
	
	/**
	 * TODO
	 * @param pincode
	 * @param requestDate
	 * @return List<Center>
	 * 
	 * 
	 * Request date is in dd-MM-yyyy format
	 * 
	 * 
	 */
	List<CenterWrap> getFilteredCentersByDistrictId(Integer pincode, String date);
}
