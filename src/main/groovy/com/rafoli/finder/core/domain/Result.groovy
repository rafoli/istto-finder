package com.rafoli.finder.core.domain

class Result {
	
	Date startDate
	Date endDate
	
	Integer totalFlights
	
	Map<Plan, Itinerary> plans = [:]

}
