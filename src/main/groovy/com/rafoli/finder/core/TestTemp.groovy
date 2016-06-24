package com.rafoli.finder.core

import groovy.text.SimpleTemplateEngine

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.util.IOUtils
import com.rafoli.finder.core.domain.Itinerary

class TestTemp {

	static main(args) {
		AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider("rafoli"));
		S3Object object = s3Client.getObject(
						  new GetObjectRequest("templates.finder.rafoli.com", "email.tpl"));
		InputStream objectData = object.getObjectContent();
		String emailTemplate = IOUtils.toString(objectData)
		objectData.close();
		
		
		def itineraries = []
		Itinerary i = new Itinerary(name:"REC-FCO")
		itineraries << i
		def binding = [itineraries:itineraries]
		def engine = new SimpleTemplateEngine()
		def text = '''
<html>
		<body>
			<% 
			String DATE_FORMAT = 'yyyy-MM-dd'

			itineraries.each{ itinerary->
			  	println """=====${itinerary.name}=======<br>"""
			  	println """${itinerary.departure.outboundDate.format(DATE_FORMAT)} [${itinerary.departure.originPlace}-${itinerary.departure.destinationPlace}] -> ${itinerary.departure.price}<br>"""
			  	println """${itinerary.arrive.outboundDate.format(DATE_FORMAT)} [${itinerary.arrive.originPlace}-${itinerary.arrive.destinationPlace}] -> ${itinerary.arrive.price}<br>"""
				if (itinerary.urls.size() == 2) {
					println """ <a href=${itinerary.urls[0]}>Departure</a> | <a href=${itinerary.urls[1]}>Arrival</a>"""
				} else if (itinerary.urls.size() == 1) {
					println """ <a href=${itinerary.urls[0]}>Departure/Arrival</a>"""
				}
				println """ <br>"""
			}
			%>
		</body>
</html>
		'''
		def template = engine.createTemplate(text).make(binding)
		println template.toString()
	}
}
