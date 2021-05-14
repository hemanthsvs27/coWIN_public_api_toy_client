package com.api.client.api.serviceImpl;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.api.client.api.model.Center;
import com.api.client.api.model.CenterWrap;
import com.api.client.api.model.Session;
import com.api.client.api.service.Service;

/**
 * @author Hemu
 *
 */
@Component
public class ServiceImpl implements Service {

	@Autowired
	RestTemplate restTemplate;

	// base url until the ? comes from the application properties file
	@Value("${calanderByPinBaseUrl}")
	String calanderByPinBaseUrl;

	/**
	 * Service implementation method
	 * Fetch Centers based on pincode
	 * refer Service interface for documentation
	 */
	@Override
	public List<CenterWrap> getFilteredCentersByPincode(Integer pincode, String date) {
		
		// will always be a size 1 list
		List<CenterWrap> centerWrapList = fetchCentersFromApi(pincode, date);
		
		// perform business logic here: filtering based on required params
		/**
		 * Filter based on: 
		 * Include the center in the result if the condition is met for any one of the center's session
		 * 		Iterate over the session and check:
		 * 			vaccine name is COVISHIELD
		 * 			slots is not an empty list
		 * 			available_capacity > 0
		 */
		// centerwrap is itself a list
		
		List<Center> centers = centerWrapList.get(0).getCenters();
		
		// Step 1 
		// filter sessions based on capacity and vaccine 
		ListIterator<Center> citer = centers.listIterator();
		while(citer.hasNext()) {
			Center dupCenter = citer.next();
			// loop in this duplicate thing and do filter and all and pacca replace the og
			List<Session> a = dupCenter.getSessions().stream()
			.filter(session -> session.getAvailableCapacity() > 0)
			.filter(session -> "COVISHIELD".equals(session.getVaccine()))
			.collect(Collectors.toList());
			dupCenter.setSessions(a);
			
			citer.set(dupCenter);
		}
		
		// step 2
		// only if a center has session length > 0 keep it
		List<Center> newCenters = centers.stream()
		.filter(center -> center.getSessions().size() > 0).collect(Collectors.toList());
		
		CenterWrap cw = new CenterWrap();
		cw.setCenters(newCenters);
		return new ArrayList<CenterWrap>(Arrays.asList(cw));

	}

	/**
	 * Helper
	 * Make the API call to get the centers for the given pincode and date (7 day window)
	 * 
	 * @param pincode
	 * @param date
	 * @return
	 */
	private List<CenterWrap> fetchCentersFromApi(Integer pincode, String date) {
		// format the given date and set current date if not specified
		//null check not required for requestDate
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = convertToDate(date).format(formatter);

		String url =
				calanderByPinBaseUrl
				+ "?pincode="
				+ pincode
				+"&date="
				+ formattedDate;

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		
		headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		HttpEntity <String> entity = new HttpEntity<String>("", headers);
		
		ResponseEntity<CenterWrap> response = restTemplate.exchange(url,
				HttpMethod.GET, entity, CenterWrap.class);
		
		// O(1) for asList
		List<CenterWrap> centersList = Arrays.asList(response.getBody());
		return centersList;
	}

	/**
	 * Helper
	 * refer Service interface for documentation
	 */
	@Override
	public List<CenterWrap> getFilteredCentersByDistrictId(Integer pincode, String date) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * returns the LocalDate object of the requestData param
	 * If null is passed returns today's date
	 * @param date
	 * @return LocalDate
	 */
	LocalDate convertToDate(String date) throws DateTimeException{
		if(date == null) {
			return LocalDate.now();
		}
		//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(""
				+ "[dd-MM-yyyy]"
				+ "[dd-M-yyyy]"
				+ "[d-MM-yyyy]"
				+ "[d-M-yyyy]"
				+ "[dd-MM-yy]"
				+ "[dd-M-yy]"
				+ "[d-MM-yy]"
				+ "[d-M-yy]"
				);


		formatter = formatter.withLocale(Locale.US);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
		return LocalDate.parse(date, formatter);
	}

}
