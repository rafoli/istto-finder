package com.rafoli.finder.core

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.Method

import com.rafoli.finder.core.domain.Flight



@Slf4j
class Finder{

	protected static String API_URL = 'http://partners.api.skyscanner.net/apiservices/pricing/v1.0'
	protected static String API_KEY = 'cc379434454338361714672782744594'
	protected static String DATE_FORMAT = 'yyyy-MM-dd'

	protected static int count = 0

	String originPlace, destinationPlace
	Date outboundDate, inboundDate

	Flight call() throws Exception {

		count++

		String detailsURL = getRequestDetailsURL(originPlace, destinationPlace, outboundDate) + "?apiKey=${API_KEY}&pageindex=0&pagesize=1"

		Flight flight = getFlightDetails(detailsURL)

		return flight
	}

	static int getProcessedFlights(){
		return count
	}

	Flight getFlightDetails(String detailsURL) throws Exception{
		Flight flight

		try {
			def http = new HTTPBuilder( detailsURL )
			http.setHeaders(Accept: 'application/json')
	
			http.request(Method.GET, ContentType.JSON) { req ->
				response.success = { resp, json ->
	
					def builder = new JsonBuilder( json )
					builder.content.Itineraries.PricingOptions.each {
						flight =  new Flight(
								originPlace:originPlace,
								destinationPlace:destinationPlace,
								outboundDate:outboundDate,
								price:it.Price[0],
								url:it.DeeplinkUrl[0]
								)
					}
				}
			}
		}
		catch(Exception e){
			throw e
		}


		return flight
	}


	String getRequestDetailsURL(String originPlace, String destinationPlace, Date outboundDate) {
		def result = ''

		def http = new HTTPBuilder(API_URL)
		http.setHeaders(Accept: 'application/json')

		try {
			http.request(Method.POST, ContentType.URLENC) { req ->

				def params = [
					apiKey: API_KEY,
					country: 'BR',
					currency: 'BRL',
					locale: 'pt-BR',
					originplace: originPlace,
					destinationplace:destinationPlace,
					outbounddate:outboundDate.format(DATE_FORMAT),
					locationschema:'iata',
					adults:'1']

				if (inboundDate)
					params['inbounddate'] = inboundDate.format(DATE_FORMAT)

				send ContentType.URLENC, params

				response.success = { resp, reader ->
					result = resp.headers.Location.value.toString()
				}
				response.failure = { resp, reader ->
					log.error resp.statusLine.toString()
					log.error reader.toString()
					
				}
			}
		}
		catch(Exception e){
			if (e instanceof HttpResponseException){
				def r = ((HttpResponseException)e).response
				print r
				def q = r.getEntity().content
				
				
			}
			throw e
		}

		return result
	}
}
