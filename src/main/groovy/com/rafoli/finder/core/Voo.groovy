package com.rafoli.finder.core

import groovy.util.logging.Slf4j

import java.util.concurrent.Future

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import com.rafoli.finder.core.aws.mail.EmailService
import com.rafoli.finder.core.domain.Flight
import com.rafoli.finder.core.domain.Itinerary
import com.rafoli.finder.core.domain.Plan
import com.rafoli.finder.core.domain.Result
import com.rafoli.finder.core.domain.config.AWSProfile
import com.rafoli.finder.core.domain.config.Execution
import com.rafoli.finder.core.domain.config.MailTo
import com.rafoli.finder.core.domain.config.PlanType


@Slf4j
class Voo implements Job {

	protected static String DATE_FORMAT = 'yyyy-MM-dd'

	static main(args) {
		find("DEBUG", "USER_PROFILE", "ME")
	}

	@Override
	public void execute(final JobExecutionContext ctx)
	throws JobExecutionException {

		String planType = ctx.mergedJobDataMap.get("plan.type") as PlanType
		String awsProfile = ctx.mergedJobDataMap.get("aws.profile") as AWSProfile
		String mailTo = ctx.mergedJobDataMap.get("mail.to") as MailTo

		find(planType, awsProfile, mailTo)
	}


	static find(String planType, String awsProfile, String mailTo){
		
		Result result = new Result()

		Finder.count = 0

		Execution.planType = planType
		Execution.awsProfile = awsProfile
		Execution.mailTo = mailTo

		log.info Execution.planType.toString()
		log.info Execution.awsProfile.toString()
		log.info Execution.mailTo.toString()


		StringBuffer msg = new StringBuffer()

		Date now = new Date()
		log.info now.toString()
		result.startDate = now
		Voo voo = new Voo()

		def plans = Plan.all

		plans.each {
			plan ->
			
			try {
				// Departures
				Date outboundDate = plan.minOutboundDate
				Date inboundDate = plan.maxInboundDate.minus(plan.minDays)
				Set<Flight> departures = voo.searchFlights(plan.originPlaces, plan.destinationPlaces, outboundDate, inboundDate)
				List<Itinerary> itineraries = []
	
				// Arrives
				outboundDate = plan.minOutboundDate.plus(plan.minDays)
				inboundDate = plan.maxInboundDate
				Set<Flight> arrives = voo.searchFlights(plan.destinationPlaces, plan.originPlaces, outboundDate, inboundDate)
				def groupedArrives = arrives.sort{ it.outboundDate }.groupBy({it.outboundDate}, {it.destinationPlace})
	
				departures.sort{ it.outboundDate }.each { Flight departure ->
	
					log.debug "Departure: ${departure.outboundDate.format(DATE_FORMAT)} -> ${departure.originPlace}-${departure.destinationPlace}"
	
					(plan.minDays..plan.maxDays).each{
	
						Date out = departure.outboundDate.plus(it)
	
						if (out <= plan.maxInboundDate) {
							String origin = departure.originPlace
	
							try{
								groupedArrives[out][origin].each{Flight arrive ->
	
									log.debug "	Arrive: ${out.format(DATE_FORMAT)} -> ${arrive.originPlace}-${arrive.destinationPlace}"
	
									Itinerary itinerary = new Itinerary(
									name:plan.name,
									departure:departure,
									arrive:arrive
									)
	
									itinerary = comparePriceWithTwoWayFlight(departure, arrive, itinerary)
	
									itineraries << itinerary
								}
							}
							catch(Exception e){
								log.debug "Departure: " + departure.originPlace + " | " + out
								e.printStackTrace()
							}
	
						}
	
	
					}
				}
				 
				if (!itineraries.empty) {
					Itinerary itinerary = itineraries.sort{it.price}[0]
					result.plans[plan] = itinerary
				}
			}
			catch(Exception e) {
				e.printStackTrace()
			}
		}

		Date finish = new Date()
		log.info finish.toString()
		result.endDate = finish
		result.totalFlights = Finder.processedFlights

		new EmailService().send(result)
	}




	private static Itinerary comparePriceWithTwoWayFlight(Flight departure, Flight arrive, Itinerary itinerary) {
		if (departure.originPlace.equals(arrive.destinationPlace) && departure.destinationPlace.equals(arrive.originPlace)) {

			Finder finder = new Finder(
			originPlace:departure.originPlace,
			destinationPlace:departure.destinationPlace,
			outboundDate:departure.outboundDate,
			inboundDate:arrive.outboundDate
			)
			Thread.sleep(1000)
			Flight flight = finder.call()
			if (flight && (flight.price < itinerary.price) ){
				itinerary.price = flight.price
				itinerary.urls = []
				itinerary.urls << flight.url
				return itinerary
			}
		}

		itinerary.urls = []
		itinerary.urls << departure.url
		itinerary.urls << arrive.url

		return itinerary
	}

	private static String prettyPrintingItineraries(Map groupedItineraries) {
		StringBuffer msg = new StringBuffer()

		groupedItineraries.each{ name, i ->
			msg.with {
				append "=====${name}=======" + "<br>"
				Itinerary itinerary = i.sort{it.price}[0]
				append "${itinerary.departure.outboundDate.format(DATE_FORMAT)} [${itinerary.departure.originPlace}-${itinerary.departure.destinationPlace}] -> ${itinerary.departure.price}"+ "<br>"
				append "${itinerary.arrive.outboundDate.format(DATE_FORMAT)} [${itinerary.arrive.originPlace}-${itinerary.arrive.destinationPlace}] -> ${itinerary.arrive.price}"+ "<br>"
				append "Total: ${itinerary.price} "
				if (itinerary.urls.size() == 2) {
					append " <a href=\"${itinerary.urls[0]}\">Departure</a> | <a href=\"${itinerary.urls[1]}\">Arrival</a>"
				} else if (itinerary.urls.size() == 1) {
					append " <a href=\"${itinerary.urls[0]}\">Departure/Arrival</a>"
				}
				append "<br>"
			}
		}

		return msg.toString()
	}

	private static prettyPrinting(Set<Flight> flights) {
		flights.sort{ it.outboundDate }.each{ Flight flight -> prettyPrinting(flight) }
	}

	private static prettyPrinting(Flight flight) {
		log.info "${flight.originPlace}->${flight.destinationPlace} | ${flight.outboundDate}: ${flight.price} "
	}

	Set<Flight> searchFlights(List<String> originPlaces, List<String> destinationPlaces, Date from, Date to) throws Exception {

		Set<Flight> flights = []
		List<Future<Flight>> tasks = []

		originPlaces.each{ originPlace ->
			destinationPlaces.each{ destinationPlace ->

				(from..to).each { eachDay ->

					Thread.sleep(1000)
					Finder finder = new Finder(
					originPlace:originPlace,
					destinationPlace:destinationPlace,
					outboundDate:eachDay)


					Flight flight = finder.call()
					if (flight) {
						flights << flight
					}
				}

			}
		}


		return flights
	}


}
