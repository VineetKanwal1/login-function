package com.techprimers.serverless;

import java.util.Iterator;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LoginHandler implements RequestHandler<LoginRequest, LoginResponse> {

	private String DYNAMODB_TABLE_NAME = "User";
	private static Regions REGION = Regions.US_EAST_2;
	GetItemRequest request = null;

	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	@Override
	public LoginResponse handleRequest(LoginRequest loginReq, Context context) {
		
		LoginResponse response = new LoginResponse();
		Item user = getUserFromDB(loginReq);
		if (user != null) {
			System.out.println("User found: " + user.get("userId"));
			response.setMessage("Login successful" + ", welcome User: " + user.get("userId"));
		} else {
			System.out.format("No User found with the key %s!\n", loginReq.getUserId());
			response.setMessage("Invalid credentials. Try again!");
		}

		return response;
	}

	private Item getUserFromDB(LoginRequest login) throws ConditionalCheckFailedException {

		Table table = dynamoDB.getTable(DYNAMODB_TABLE_NAME);

		QuerySpec spec = new QuerySpec().withKeyConditionExpression("userId = :v_userId")
				.withFilterExpression("password = :v_password").withValueMap(new ValueMap()
						.withString(":v_userId", login.getUserId()).withString(":v_password", login.getPassword()));

		ItemCollection<QueryOutcome> items = table.query(spec);

		System.out.println("\nUser:");

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item user = iterator.next();
			System.out.println(user.toJSONPretty());
			return user;
		}
		return null;

	}


}
