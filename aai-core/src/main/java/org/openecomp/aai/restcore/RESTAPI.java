/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.restcore;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.openecomp.aai.db.props.AAIProperties;
import org.openecomp.aai.dbmap.DBConnectionType;
import org.openecomp.aai.exceptions.AAIException;
import org.openecomp.aai.introspection.Introspector;
import org.openecomp.aai.introspection.Loader;
import org.openecomp.aai.introspection.tools.CreateUUID;
import org.openecomp.aai.introspection.tools.DefaultFields;
import org.openecomp.aai.introspection.tools.InjectKeysFromURI;
import org.openecomp.aai.introspection.tools.IntrospectorValidator;
import org.openecomp.aai.introspection.tools.Issue;
import org.openecomp.aai.introspection.tools.RemoveNonVisibleProperty;
import org.openecomp.aai.logging.ErrorLogHelper;
import org.openecomp.aai.logging.LoggingContext;
import org.openecomp.aai.util.AAIConfig;
import org.openecomp.aai.util.FormatDate;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.google.common.base.Joiner;


/**
 * Base class for AAI REST API classes.
 * Provides method to validate header information
 * TODO should authenticate caller and authorize them for the API they are calling
 * TODO should store the transaction
 
 *
 */
public class RESTAPI {
	
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(RESTAPI.class);

	protected final String COMPONENT = "aairest";

	/**
	 * The Enum Action.
	 */
	public enum Action {
		GET, PUT, POST, DELETE
	};

	/**
	 * Gets the from app id.
	 *
	 * @param headers the headers
	 * @param logline the logline
	 * @return the from app id
	 * @throws AAIException the AAI exception
	 */
	protected String getFromAppId(HttpHeaders headers) throws AAIException { 
		String fromAppId = null;
		if (headers != null) {
			List<String> fromAppIdHeader = headers.getRequestHeader("X-FromAppId");
			if (fromAppIdHeader != null) {
				for (String fromAppIdValue : fromAppIdHeader) {
					fromAppId = fromAppIdValue;
				}
			} 
		}

		if (fromAppId == null) {
			throw new AAIException("AAI_4009");
		}

		LoggingContext.partnerName(fromAppId);

		return fromAppId;
	}
	
	/**
	 * Gets the trans id.
	 *
	 * @param headers the headers
	 * @param logline the logline
	 * @return the trans id
	 * @throws AAIException the AAI exception
	 */
	protected String getTransId(HttpHeaders headers) throws AAIException { 
		String transId = null;
		if (headers != null) {
			List<String> transIdHeader = headers.getRequestHeader("X-TransactionId");
			if (transIdHeader != null) {
				for (String transIdValue : transIdHeader) {
					transId = transIdValue;
				}
			}
		}

		if (transId == null) {
			throw new AAIException("AAI_4010");
		}

		LoggingContext.requestId(transId);

		return transId;
	}
	
	
	/**
	 * Gen date.
	 *
	 * @return the string
	 */
	protected String genDate() {
		FormatDate fd = new FormatDate( "YYMMdd-HH:mm:ss:SSS");
		
		return fd.getDateTime();
	}

	/**
	 * Gets the media type.
	 *
	 * @param mediaTypeList the media type list
	 * @return the media type
	 */
	protected String getMediaType(List <MediaType> mediaTypeList) {
		String mediaType = MediaType.APPLICATION_JSON;  // json is the default    
		for (MediaType mt : mediaTypeList) {
			if (MediaType.APPLICATION_XML_TYPE.isCompatible(mt)) {
				mediaType = MediaType.APPLICATION_XML;
			} 
		}
		return mediaType;
	}
	

	/* ----------helpers for common consumer actions ----------- */
	
	/**
	 * Sets the depth.
	 *
	 * @param depthParam the depth param
	 * @return the int
	 * @throws AAIException the AAI exception
	 */
	protected int setDepth(String depthParam) throws AAIException {
		int depth = AAIProperties.MAXIMUM_DEPTH; //default 
		if (depthParam != null && depthParam.length() > 0 && !depthParam.equals("all")){
			try {
				depth = Integer.valueOf(depthParam);
			} catch (Exception e) {
				throw new AAIException("AAI_4016");
			}
		}
		return depth;
	}

	/**
	 * Consumer exception response generator.
	 *
	 * @param headers the headers
	 * @param info the info
	 * @param templateAction the template action
	 * @param e the e
	 * @return the response
	 */
	protected Response consumerExceptionResponseGenerator(HttpHeaders headers, UriInfo info, HttpMethod templateAction, AAIException e) {
		ArrayList<String> templateVars = new ArrayList<String>();
		templateVars.add(templateAction.toString()); //GET, PUT, etc
		templateVars.add(info.getPath().toString());
		templateVars.addAll(e.getTemplateVars());

		return Response
				.status(e.getErrorObject().getHTTPResponseCode())
				.entity(ErrorLogHelper.getRESTAPIErrorResponseWithLogging(headers.getAcceptableMediaTypes(), e, templateVars))
				.build();
	}
	
	/**
	 * Validate introspector.
	 *
	 * @param obj the obj
	 * @param loader the loader
	 * @param uri the uri
	 * @param validateRequired the validate required
	 * @throws AAIException the AAI exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	protected void validateIntrospector(Introspector obj, Loader loader, URI uri, HttpMethod method) throws AAIException, UnsupportedEncodingException {
		
		int maximumDepth = AAIProperties.MAXIMUM_DEPTH;
		boolean validateRequired = true;
		if (method.equals(HttpMethod.MERGE_PATCH)) {
			validateRequired = false;
			maximumDepth = 0;
		}
		IntrospectorValidator validator = new IntrospectorValidator.Builder()
				.validateRequired(validateRequired)
				.restrictDepth(maximumDepth)
				.addResolver(new RemoveNonVisibleProperty())
				.addResolver(new CreateUUID())
				.addResolver(new DefaultFields())
				.addResolver(new InjectKeysFromURI(loader, uri))
				.build();
		boolean result = validator.validate(obj);
		if (!result) {
			result = validator.resolveIssues();
		}
		if (!result) {
			List<String> messages = new ArrayList<>();
			for (Issue issue : validator.getIssues()) {
				if (!issue.isResolved()) {
					messages.add(issue.getDetail());
				}
			}
			String errors = Joiner.on(",").join(messages);
			throw new AAIException("AAI_3000", errors);
		}
		//check that key in payload and key in request uri are the same
        String objURI = obj.getURI();
        //if requested object is a parent objURI will have a leading slash the input uri will lack
        //this adds that leading slash for the comparison
        String testURI = "/" + uri.getRawPath();
        if (!testURI.endsWith(objURI)) {
        	throw new AAIException("AAI_3000", "uri and payload keys don't match");
        }
	}
	
	protected DBConnectionType determineConnectionType(String fromAppId, String realTime) {
		DBConnectionType type = DBConnectionType.REALTIME;
		boolean isRealTimeClient = AAIConfig.get("aai.realtime.clients", "").contains(fromAppId);
		if (isRealTimeClient || realTime != null) {
			type = DBConnectionType.REALTIME;
		} else {
			type = DBConnectionType.CACHED;
		}
		
		return type;
	}
	
	/**
	 * Gets the input media type.
	 *
	 * @param mediaType the media type
	 * @return the input media type
	 */
	protected String getInputMediaType(MediaType mediaType) {
		String result = mediaType.getType() + "/" + mediaType.getSubtype();
		
		return result;
		
	}

}
