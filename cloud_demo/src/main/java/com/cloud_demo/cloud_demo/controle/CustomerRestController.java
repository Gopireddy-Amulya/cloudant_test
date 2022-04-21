package com.cloud_demo.cloud_demo.controle;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_demo.cloud_demo.model.Customer;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {
	@Autowired
	private CloudantClient client;
	private Database db;

	// Create a new customer
	@PostMapping("save")
	// @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String saveReview(@RequestBody Customer cust) {
		db = client.database("customer", false);
		System.out.println("Save Customer " + cust);
		Response r = null;
		if (cust != null) {
			r = db.post(cust);
		}
		return r.getId();
	}

	// Query reviews for all documents or by customerId

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Customer> getAll() throws IOException {
			db = client.database("customer", false);
			List<Customer> allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Customer.class);
			return allDocs;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public @ResponseBody Customer getById(@PathVariable String id) throws IOException {
			db = client.database("customer", false);
			Customer customer = db.find(Customer.class, id);
			return customer;
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteGreeting(@PathVariable String id) throws IOException {
		db = client.database("customer", false);
		Customer customer = db.find(Customer.class, ""+id+"");
		Response remove = db.remove(customer.get_id(),customer.get_rev());
		return new ResponseEntity<String>(remove.getReason(), HttpStatus.valueOf(remove.getStatusCode()));
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity<?> updatedCustomer(@PathVariable String id, @RequestBody Customer customerDetails) {
		db = client.database("customer", false);
		Customer customer = db.find(Customer.class, id);
		customer.setCustomerId(customerDetails.getCustomerId());
		customer.setCustomerName(customerDetails.getCustomerName());
		customer.setDepartment(customerDetails.getDepartment());
			
			Response updatedCustomer = db.post(customer);
			
		return ResponseEntity.ok(updatedCustomer);
	}
}
