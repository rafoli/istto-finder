package com.rafoli.finder.core.domain

import com.rafoli.finder.core.Voo
import com.rafoli.finder.core.domain.config.Execution
import com.rafoli.finder.core.domain.config.PlanType

class Plan {

	String id
	String name
	List<String> originPlaces
	List<String> destinationPlaces
	Date minOutboundDate
	Date maxInboundDate
	int minDays
	int maxDays

	static List<Plan> getAll() {

		List<Plan> plans

		switch(Execution.planType) {
			case PlanType.DEBUG:
				plans = [
					new Plan(
					name:'REC-AWS-CNF',
					originPlaces: ['REC'],
					destinationPlaces: ['CNF'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-13' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-31' ),
					minDays:1,
					maxDays:1)
				]
				break
			case PlanType.ALL:
				plans = [
					new Plan(  
					name:'REC-Ago-CNF',
					originPlaces: ['REC'],
					destinationPlaces: ['CNF'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-13' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-31' ),
					minDays:1,
					maxDays:1),	
					new Plan(
					name:'REC-Ago-VCP',
					originPlaces: ['REC'],
					destinationPlaces: ['VCP'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-13' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-31' ),
					minDays:1,
					maxDays:1),									
					new Plan(
					name:'REC-Ago-GRU', 
					originPlaces: ['REC'],
					destinationPlaces: ['GRU'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-13' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-31' ),
					minDays:1,
					maxDays:1),
				new Plan(
					name:'REC-Ago-GIG',
					originPlaces: ['REC'],
					destinationPlaces: ['GIG'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-13' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-08-31' ),
					minDays:1,
					maxDays:1),
					new Plan(  
					name:'REC-AWS-CNF',
					originPlaces: ['REC'],
					destinationPlaces: ['CNF'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-01' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-30' ),
					minDays:1,
					maxDays:1),	
					new Plan(
					name:'REC-AWS-VCP',
					originPlaces: ['REC'],
					destinationPlaces: ['VCP'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-01' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-30' ),
					minDays:1,
					maxDays:1),									
					new Plan(
					name:'REC-AWS-GRU', 
					originPlaces: ['REC'],
					destinationPlaces: ['GRU'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-01' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-30' ),
					minDays:1,
					maxDays:1),
				new Plan(
					name:'REC-AWS-GIG',
					originPlaces: ['REC'],
					destinationPlaces: ['GIG'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-01' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-09-30' ),
					minDays:1,
					maxDays:1),				
					new Plan(
					name:'REC-Roma',
					originPlaces: ['REC'],
					destinationPlaces: ['FCO'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-11-01' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-11-30' ),
					minDays:9,
					maxDays:9),
					new Plan(
					name:'REC-Lisboa',
					originPlaces: ['REC'],
					destinationPlaces: ['LIS'],
					minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-11-01' ),
					maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-11-30' ),
					minDays:9,
					maxDays:9)
					/*new Plan(
				 name:'Bacana',
				 originPlaces: ['REC'],
				 destinationPlaces: ['MCO', 'MIA', 'TPA', 'FLL'],
				 minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-05-01' ),
				 maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-05-31' ),
				 minDays:10,
				 maxDays:15),*/
					/*new Plan(
				 id:'AWS',
				 name:'Beach For',
				 originPlaces: ['REC'],
				 destinationPlaces: ['FOR'],
				 minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-07-01' ),
				 maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-07-31' ),
				 minDays:3,
				 maxDays:5),
				 new Plan(
				 id:'AWS',
				 name:'Ita',
				 originPlaces: ['REC'],
				 destinationPlaces: ['VDC'],
				 minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-07-01' ),
				 maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-07-31' ),
				 minDays:6,
				 maxDays:10),
				 new Plan(
				 id:'AWS',
				 name:'Fam Ita',
				 originPlaces: ['VDC'],
				 destinationPlaces: ['REC'],
				 minOutboundDate:Date.parse( Voo.DATE_FORMAT, '2015-07-01' ),
				 maxInboundDate:Date.parse( Voo.DATE_FORMAT, '2015-07-31' ),
				 minDays:7,
				 maxDays:15)*/
				]
				break
		}

		return plans
	}
}
