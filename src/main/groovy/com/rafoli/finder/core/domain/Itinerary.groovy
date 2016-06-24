package com.rafoli.finder.core.domain

import groovy.transform.EqualsAndHashCode;

class Itinerary {
	
	String name
	
	Flight departure
	Flight arrive
	
	List<String> urls
	
	private Double price
	
	Double getPrice() {
		if (!price) {
			price = departure.price + arrive.price 
		}
		return price
	}
	
	void setPrice(Double price) {
		this.price = price
	}

}
