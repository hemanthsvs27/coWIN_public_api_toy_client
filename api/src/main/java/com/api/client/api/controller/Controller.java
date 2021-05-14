package com.api.client.api.controller;

import java.time.DateTimeException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.api.client.api.model.CenterWrap;
import com.api.client.api.service.Service;

@RestController
public class Controller {
	
	@Autowired
	Service service;
	
	@GetMapping(value = {"/centers/{pincode}", "/centers/{pincode}/{date}"})
	public List<CenterWrap> get(@PathVariable Integer pincode, @PathVariable(required = false) String date) {
		try {
			List<CenterWrap> centerList =  service.getFilteredCentersByPincode(pincode, date);
	        return centerList;
	     }
	    catch (DateTimeException dte) {
	         throw new ResponseStatusException(
	           HttpStatus.BAD_REQUEST, "Invalid date passed", dte);
	    }
		catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, "Unknown exception has occured", e);
		}
	}
}
