package com.rafoli.finder.core.aws.mail;

import groovy.text.SimpleTemplateEngine

import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.amazonaws.util.IOUtils
import com.rafoli.finder.core.aws.mail.credentials.InstanceProfile
import com.rafoli.finder.core.aws.mail.credentials.UserProfile
import com.rafoli.finder.core.domain.Result
import com.rafoli.finder.core.domain.config.AWSProfile
import com.rafoli.finder.core.domain.config.Execution
import com.rafoli.finder.core.domain.config.MailTo

public class EmailService {

	static final String FROM = "rafoli@gmail.com";  // Replace with your "From" address. This address must be verified.
	static final String SUBJECT = "Amazon SES test (AWS SDK for Java)";
	

	public void send(Result result) throws IOException {


		try {
			System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");



			AmazonSimpleEmailServiceClient client;

			switch(Execution.awsProfile) {
				case AWSProfile.INSTANCE_PROFILE:
					client = InstanceProfile.client
					break
				case AWSProfile.USER_PROFILE:
					client = UserProfile.client
					break
			}

			ListVerifiedEmailAddressesResult verifiedEmailAddresses = client.listVerifiedEmailAddresses();

			List<String> to = verifiedEmailAddresses.getVerifiedEmailAddresses();
			
			AmazonS3 s3Client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
			S3Object object = s3Client.getObject(
							  new GetObjectRequest("templates.finder.rafoli.com", "email.tpl"));
			InputStream objectData = object.getObjectContent();
			String emailTemplate = IOUtils.toString(objectData)
			objectData.close();
			
			def binding = [result:result]
			def engine = new SimpleTemplateEngine()
			def template = engine.createTemplate(emailTemplate).make(binding)
			String bodyContent = template.toString()
		

			SendEmailRequest request = buildRequest(bodyContent, to);

			// Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your production
			// access status, sending limits, and Amazon SES identity-related settings are specific to a given
			// AWS region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
			// the US East (N. Virginia) region. Examples of other regions that Amazon SES supports are US_WEST_2
			// and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
			Region REGION = Region.getRegion(Regions.US_EAST_1);
			client.setRegion(REGION);

			// Send the email.
			client.sendEmail(request);
			System.out.println("Email sent!");

		} catch (Exception ex) {
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + ex.getMessage());
		}
	}


	private SendEmailRequest buildRequest(String bodyContent, List<String> to) {


		// Construct an object to contain the recipient address.
		Destination destination;
		switch(Execution.mailTo) {
			case MailTo.ME:
				List<String> s = new ArrayList<String>();
				s.add("rafoli@gmail.com");
				destination = new Destination().withToAddresses(s);
				break
			case MailTo.ALL:
				destination = new Destination().withToAddresses(to)
				break
		}

		// Create the subject and body of the message.
		Content subject = new Content().withData(SUBJECT);
		Content textBody = new Content().withData(bodyContent);
		Body body = new Body().withHtml(textBody);

		// Create a message with the specified subject and body.
		Message message = new Message().withSubject(subject).withBody(body);

		// Assemble the email.
		SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
		return request;
	}
}
