package com.rafoli.finder.core.aws;
/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

/**
 * This sample demonstrates how to make basic requests to Amazon SQS using the
 * AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web
 * Services developer account, and be signed up to use Amazon SQS. For more
 * information on Amazon SQS, see http://aws.amazon.com/sqs.
 * <p>
 * WANRNING:</b> To avoid accidental leakage of your credentials, DO NOT keep
 * the credentials file in your source directory.
 */
public class NotificationService {

    public void sendMessage(String message) throws Exception {

        /*
         * The ProfileCredentialsProvider will return your [rafoli]
         * credential profile by reading from the credentials file located at
         * (/Users/rafoli/.aws/credentials).
         */
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

        AmazonSNSClient sns = new AmazonSNSClient(credentials);
        Region usWest1 = Region.getRegion(Regions.US_EAST_1);
        sns.setRegion(usWest1);

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SNS");
        System.out.println("===========================================\n");

        try {
        
        	
        	String topicArn = "arn:aws:sns:us-east-1:545939451827:FinderTopic";

        	//publish to an SNS topic
        	PublishRequest publishRequest = new PublishRequest(topicArn, message, "AWS test");
        	PublishResult publishResult = sns.publish(publishRequest);
        	//print MessageId of message published to SNS topic
        	System.out.println("MessageId - " + publishResult.getMessageId());
            
            
        } catch (AmazonServiceException ase) {
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
