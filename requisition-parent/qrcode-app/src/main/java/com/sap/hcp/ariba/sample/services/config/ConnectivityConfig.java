package com.sap.hcp.ariba.sample.services.config;

import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.UnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

/**
 * Used to configure the connectivity.
 */
public class ConnectivityConfig {

	private static final String ERROR_PROPERTY_NOT_FOUND = "Property [ {} ] not found in destination [ {} ]. Hint: Make sure to have the property configured in the destination.";
	private static final String ERROR_DESTINATION_NOT_FOUND = "Destination [ {} ] not found. Hint: Make sure to have the destination configured.";
	private static final String ERROR_COULD_NOT_LOOKUP_CONNECTIVITY_CONFIGURATION = "Could not lookup Connectivity Configuration. See logs for details.";

	private static final String CONNECTIVITY_CONFIGURATION = "java:comp/env/connectivityConfiguration";
	private static final String API_DESTINATION = "ariba-p2p-api";

	private static final Logger logger = LoggerFactory.getLogger(ConnectivityConfig.class);

	private Map<String, String> destinationProperties;

	/**
	 * Constructor used to initialize the Connectivity Configuration.
	 * 
	 * @throws UnavailableException
	 *             - thrown if connectivity configuration failed or destination
	 *             with the specified name was not found
	 */
	public ConnectivityConfig() throws UnavailableException {
		init();
	}

	/**
	 * Returns destination property value by specified destination property key
	 * 
	 * @param propertyKey
	 *            - the key of the searched destination property
	 * @return the value of the destination with the specified destination key.
	 * @throws UnavailableException
	 *             - thrown if destination property with the specified key is
	 *             not found
	 */
	public String getDestinationPropertyValue(String propertyKey) {

		String propertyValue = destinationProperties.get(propertyKey);

		if (propertyValue == null) {
			String errorMessage = MessageFormatter.format(ERROR_PROPERTY_NOT_FOUND, propertyKey, API_DESTINATION)
					.toString();
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		return propertyValue;
	}

	private void init() throws UnavailableException {

		try {
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup(CONNECTIVITY_CONFIGURATION);
			initDestinationProperties(API_DESTINATION, configuration);

		} catch (NamingException e) {
			logger.error(ERROR_COULD_NOT_LOOKUP_CONNECTIVITY_CONFIGURATION, e);
			throw new UnavailableException(ERROR_COULD_NOT_LOOKUP_CONNECTIVITY_CONFIGURATION);
		}
	}

	private void initDestinationProperties(String destinationName, ConnectivityConfiguration configuration)
			throws UnavailableException {

		if (configuration == null) {
			init();
		}

		DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);
		if (destConfiguration == null) {
			String errorMessage = MessageFormatter.format(ERROR_DESTINATION_NOT_FOUND, destinationName).toString();
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		destinationProperties = destConfiguration.getAllProperties();
	}
}
