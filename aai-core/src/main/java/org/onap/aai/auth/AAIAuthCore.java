/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.auth;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.onap.aai.auth.exceptions.AAIUnrecognizedFunctionException;
import org.onap.aai.logging.ErrorLogHelper;
import org.onap.aai.logging.LoggingContext;
import org.onap.aai.logging.LoggingContext.StatusCode;
import org.onap.aai.util.AAIConfig;
import org.onap.aai.util.AAIConstants;
import org.onap.aai.util.FileWatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The Class AAIAuthCore.
 */
public final class AAIAuthCore {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AAIAuthCore.class);

	private static final String ERROR_CODE_AAI_4001 = "AAI_4001";

	private String globalAuthFileName = AAIConstants.AAI_AUTH_CONFIG_FILENAME;
	
	private final Pattern AUTH_POLICY_PATTERN;
	private final Set<String> validFunctions = new HashSet<>();
	private Map<String, AAIUser> users;
	private boolean timerSet = false;
	private Timer timer = null;

	private String basePath;
	/**
	 * Instantiates a new AAI auth core.
	 */
	public AAIAuthCore(String basePath) {
		this.basePath = basePath;
		AUTH_POLICY_PATTERN = Pattern.compile("^" + this.basePath +"/v\\d+/([\\w\\-]*)");
		init();
	}

	/**
	 * Inits the.
	 */
	private synchronized void init() {

		LOGGER.debug("Initializing Auth Policy Config");

		reloadUsers();

		/*
		 * this timer code is setting up a recurring task that checks if the
		 * auth config file has been updated and reloads the users if so to get
		 * the most up to date info (that update check logic is within
		 * FileWatcher)
		 * 
		 * the timing this method uses is coarser than the frequency of requests
		 * AI&I gets so we're looking at better ways of doing this (TODO)
		 */
		TimerTask task = new FileWatcher(new File(globalAuthFileName)) {
			@Override
			protected void onChange(File file) {
				reloadUsers();
			}
		};

		if (!timerSet) {
			timerSet = true;
			timer = new Timer();

			// repeat the check every second
			timer.schedule(task, new Date(), 10000);
		}
		LOGGER.debug("Static Initializiation complete");
	}

	/**
	 * Cleanup.
	 */
	// just ends the auth config file update checking timer
	public void cleanup() {
		timer.cancel();
	}

	/**
	 * Reload users.
	 */
	/*
	 * this essentially takes the data file, which is organized role-first with
	 * users under each role and converts it to data organized user-first with
	 * each user containing their role with its associated allowed functions
	 * this data stored in the class field users
	 */
	private synchronized void reloadUsers() {

		Map<String, AAIUser> tempUsers = new HashMap<>();

		try {
			LOGGER.debug("Reading from " + globalAuthFileName);
			String authFile = new String(Files.readAllBytes(Paths.get(globalAuthFileName)));
			
			JsonParser parser = new JsonParser();
			JsonObject authObject = parser.parse(authFile).getAsJsonObject();
			if (authObject.has("roles")) {
				JsonArray roles = authObject.getAsJsonArray("roles");
				for (JsonElement role : roles) {
					if (role.isJsonObject()) {
						JsonObject roleObject = role.getAsJsonObject();
						String roleName = roleObject.get("name").getAsString();
						Map<String, Boolean> usrs = this.getUsernamesFromRole(roleObject);
						List<String> aaiFunctions = this.getAAIFunctions(roleObject);
						
						usrs.forEach((key, value) -> {
							final AAIUser au = tempUsers.getOrDefault(key, new AAIUser(key, value));
							au.addRole(roleName);
								aaiFunctions.forEach(f -> {
								List<String> httpMethods = this.getRoleHttpMethods(f, roleObject);
								httpMethods.forEach(hm -> au.setUserAccess(f, hm));
								this.validFunctions.add(f);
							});
								
							tempUsers.put(key, au);
							
						});
					}
				}
				if (!tempUsers.isEmpty()) {
					users = tempUsers;
				}	
			}
		} catch (FileNotFoundException e) {
			ErrorLogHelper.logError(ERROR_CODE_AAI_4001, globalAuthFileName + ". Exception: " + e);
		} catch (JsonProcessingException e) {
			ErrorLogHelper.logError(ERROR_CODE_AAI_4001, globalAuthFileName + ". Not valid JSON: " + e);
		} catch (Exception e) {
			ErrorLogHelper.logError(ERROR_CODE_AAI_4001, globalAuthFileName + ". Exception caught: " + e);
		}
	}

	private List<String> getRoleHttpMethods(String aaiFunctionName, JsonObject roleObject) {
		List<String> httpMethods = new ArrayList<>();
		
		JsonArray ja = roleObject.getAsJsonArray("functions");
		for (JsonElement je : ja) {
			if (je.isJsonObject() && je.getAsJsonObject().has("name") && je.getAsJsonObject().get("name").getAsString().equals(aaiFunctionName)) {
				JsonArray jaMeth = je.getAsJsonObject().getAsJsonArray("methods");
				for (JsonElement jeMeth : jaMeth) {
					if (jeMeth.isJsonObject() && jeMeth.getAsJsonObject().has("name")) {
						httpMethods.add(jeMeth.getAsJsonObject().get("name").getAsString());
					}
				}
			}
		}
		
		return httpMethods;
	}

	private List<String> getAAIFunctions(JsonObject roleObject) {
		List<String> aaiFunctions = new ArrayList<>();
		
		JsonArray ja = roleObject.getAsJsonArray("functions");
		for (JsonElement je : ja) {
			if (je.isJsonObject() && je.getAsJsonObject().has("name")) {
				aaiFunctions.add(je.getAsJsonObject().get("name").getAsString());
			}
		}
		
		return aaiFunctions;
	}

	private Map<String, Boolean> getUsernamesFromRole(JsonObject roleObject) throws UnsupportedEncodingException {
		Map<String, Boolean> usernames = new HashMap<>();
		
		JsonArray uja = roleObject.getAsJsonArray("users");
		for (JsonElement je : uja) {
			if (je.isJsonObject()) {
				if (je.getAsJsonObject().has("username")) {
					if (je.getAsJsonObject().has("is-wildcard-id")) {
						usernames.put(je.getAsJsonObject().get("username").getAsString().toLowerCase(), je.getAsJsonObject().get("is-wildcard-id").getAsBoolean());
					} else {
						usernames.put(je.getAsJsonObject().get("username").getAsString().toLowerCase(), false);
					}
				} else if (je.getAsJsonObject().has("user")) {
					String auth = je.getAsJsonObject().get("user").getAsString() + ":" + Password.deobfuscate(je.getAsJsonObject().get("pass").getAsString());
					String authorizationCode = new String(Base64.base64Encode(auth.getBytes("utf-8")));
					usernames.put(authorizationCode, false);
				}
			}
		}
		
		return usernames;
	}
	
	public String getAuthPolicyFunctName(String uri) {
		String authPolicyFunctionName = "";
		if (uri.startsWith(basePath + "/search")) {
			authPolicyFunctionName = "search";
        } else if (uri.startsWith(basePath + "/recents")) {
            authPolicyFunctionName = "recents";
        }else if (uri.startsWith(basePath + "/util/echo")) {
			authPolicyFunctionName = "util";
		} else if (uri.startsWith(basePath + "/tools")) {
			authPolicyFunctionName = "tools";
		} else {
			Matcher match = AUTH_POLICY_PATTERN.matcher(uri);
			if (match.find()) {
				authPolicyFunctionName = match.group(1);
			}
		}
		return authPolicyFunctionName;
	}

	/**
	 * for backwards compatibility
	 * @param username
	 * @param uri
	 * @param httpMethod
	 * @param haProxyUser
	 * @return
	 * @throws AAIUnrecognizedFunctionException
	 */
	public boolean authorize(String username, String uri, String httpMethod, String haProxyUser) throws AAIUnrecognizedFunctionException {
		return authorize(username, uri, httpMethod, haProxyUser, null);
	}

	/**
	 *
	 * @param username
	 * @param uri
	 * @param httpMethod
	 * @param haProxyUser
	 * @param issuer issuer of the cert
	 * @return
	 * @throws AAIUnrecognizedFunctionException
	 */
	public boolean authorize(String username, String uri, String httpMethod, String haProxyUser, String issuer) throws AAIUnrecognizedFunctionException {
		String aaiMethod = this.getAuthPolicyFunctName(uri);
		if (!this.validFunctions.contains(aaiMethod)) {
			throw new AAIUnrecognizedFunctionException(aaiMethod);
		}
		boolean wildcardCheck = isWildcardIssuer(issuer);
		boolean authorized;
		LOGGER.debug("Authorizing the user for the request cert {}, haproxy header {}, aai method {}, httpMethod {}, cert issuer {}",
				username, haProxyUser, aaiMethod, httpMethod, issuer);
		Optional<AAIUser> oau = this.getUser(username, wildcardCheck);
		if (oau.isPresent()) {
			AAIUser au = oau.get();
			if (au.hasRole("HAProxy")) {
			    LOGGER.debug("User has HAProxy role");
				if ("GET".equalsIgnoreCase(httpMethod) && "util".equalsIgnoreCase(aaiMethod) && haProxyUser.isEmpty()) {
					LOGGER.debug("Authorized user has HAProxy role with echo request");
					authorized = this.authorize(au, aaiMethod, httpMethod);
				} else {
					authorized = this.authorize(haProxyUser, uri, httpMethod, "", issuer);
				}
			} else {
				LOGGER.debug("User doesn't have HAProxy role so assuming its a regular client");
				authorized = this.authorize(au, aaiMethod, httpMethod);
			}
		} else {
			LOGGER.debug("User not found: " + username + " on function " + aaiMethod + " request type " + httpMethod);
			authorized = false;
		}
		
		return authorized;
	}

	private boolean isWildcardIssuer(String issuer) {
		if (issuer != null && !issuer.isEmpty()) {
			List<String> validIssuers = Arrays.asList(AAIConfig.get("aaf.valid.issuer.wildcard", UUID.randomUUID().toString()).split("\\|"));
			for (String validIssuer : validIssuers) {
				if (issuer.contains(validIssuer)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns aai user either matching the username or containing the wildcard.
	 * @param username
	 * @return
	 */
	public Optional<AAIUser> getUser(String username, boolean wildcardCheck) {
		if (users.containsKey(username)) {
			return Optional.of(users.get(username));
		} else if (wildcardCheck){
			List<AAIUser> laus = users.entrySet().stream().filter(e -> e.getValue().isWildcard() && username.contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
			if (!laus.isEmpty()) {
				return Optional.of(laus.get(0));
			}
		}
		return Optional.empty();
	}

	/**
	 *
	 * @param aaiUser
	 * 			aai user with the username
	 * @param aaiMethod
	 * 			aai function the authorization is required on
	 * @param httpMethod
	 * 			http action user is attempting
	 * @return true, if successful
	 */
	private boolean authorize(AAIUser aaiUser, String aaiMethod, String httpMethod) {
		if (aaiUser.hasAccess(aaiMethod, httpMethod)) {
			LoggingContext.statusCode(StatusCode.COMPLETE);
			LOGGER.debug("AUTH ACCEPTED: " + aaiUser.getUsername() + " on function " + aaiMethod + " request type " + httpMethod);
			return true;
		} else {
			LoggingContext.statusCode(StatusCode.ERROR);
			LOGGER.debug("AUTH FAILED: " + aaiUser.getUsername() + " on function " + aaiMethod + " request type " + httpMethod);
			return false;
		}
	}
}
