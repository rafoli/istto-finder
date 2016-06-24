package com.rafoli.finder.core.aws.mail.credentials

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

class InstanceProfile {

	static AmazonSimpleEmailServiceClient getClient() {
		
		AWSCredentialsProvider credentials = null;
		try {
			credentials = new InstanceProfileCredentialsProvider();
		} catch (Exception e) {
			throw e;
		}
		AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);

		return client
	}
}
