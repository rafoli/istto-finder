package com.rafoli.finder.core.aws.mail.credentials

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient

class UserProfile {

	static AmazonSimpleEmailServiceClient getClient() {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("rafoli").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
			"Cannot load the credentials from the credential profiles file. " +
			"Please make sure that your credentials file is at the correct " +
			"location (/Users/rafoli/.aws/credentials), and is in valid format.",
			e);
		}
		AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);

		return client
	}
}
