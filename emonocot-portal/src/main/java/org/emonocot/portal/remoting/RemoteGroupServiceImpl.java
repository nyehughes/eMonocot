/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.portal.remoting;

import java.util.ArrayList;
import java.util.List;

import org.emonocot.model.BaseData;
import org.emonocot.model.SecuredObject;
import org.emonocot.portal.controller.form.AceDto;
import org.emonocot.service.impl.GroupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ben
 *
 */
@Service
public class RemoteGroupServiceImpl extends GroupServiceImpl {

	/**
	 *
	 */
	private static HttpHeaders httpHeaders = new HttpHeaders();

	static {
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(acceptableMediaTypes);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	}

	/**
	 *
	 */
	 private String baseUri;

	 /**
	  *
	  */
	 private RestTemplate restTemplate;

	 /**
	  *
	  */
	 @Autowired
	 public final void setRestTemplate(final RestTemplate restTemplate) {
		 this.restTemplate = restTemplate;
	 }

	 /**
	  *
	  * @param newBaseUri
	  *            Set the base uri
	  */
	 public final void setBaseUri(final String newBaseUri) {
		 this.baseUri = newBaseUri;
	 }

	 /**
	  *
	  * @param object
	  *            Set the object being secured
	  * @param recipient
	  *            Set the principal who has the permission
	  * @param permission
	  *            Set the permission
	  * @param clazz
	  *            Set the class of the object
	  */
	 @Override
	 public final void addPermission(final SecuredObject object,
			 final String recipient, final Permission permission,
			 final Class<? extends SecuredObject> clazz) {
		 AceDto aceDto = new AceDto();
		 aceDto.setPrincipal(recipient);
		 aceDto.setPermission(permission);
		 aceDto.setClazz(clazz);
		 aceDto.setObject(((BaseData) object).getIdentifier());
		 HttpEntity<AceDto> requestEntity = new HttpEntity<AceDto>(aceDto,
				 httpHeaders);
		 restTemplate.exchange(baseUri + "/group/" + recipient
				 + "/permission", HttpMethod.POST, requestEntity, AceDto.class);
	 }

	 /**
	  *
	  * @param object
	  *            Set the object being secured
	  * @param recipient
	  *            Set the principal who has the permission
	  * @param permission
	  *            Set the permission
	  * @param clazz
	  *            Set the class of the object
	  */
	 @Override
	 public final void deletePermission(final SecuredObject object,
			 final String recipient, final Permission permission,
			 final Class<? extends SecuredObject> clazz) {
		 AceDto aceDto = new AceDto();
		 aceDto.setPrincipal(recipient);
		 aceDto.setPermission(permission);
		 aceDto.setObject(((BaseData) object).getIdentifier());
		 aceDto.setClazz(clazz);
		 HttpEntity<AceDto> requestEntity = new HttpEntity<AceDto>(aceDto,
				 httpHeaders);
		 restTemplate.exchange(baseUri + "/group/" + recipient
				 + "/permission?delete=true", HttpMethod.POST, requestEntity,
				 AceDto.class);
	 }

	 @Override
	 public void delete(final String identifier) {
		 dao.delete(identifier);
	 }
}
