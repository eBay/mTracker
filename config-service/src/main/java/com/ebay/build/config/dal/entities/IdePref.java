/**
 * 
 */
package com.ebay.build.config.dal.entities;

import org.springframework.data.annotation.Id;

/**
 * @author bishen
 * 
 */
public class IdePref {

	@Id
	private String id;

	private String key;
	private String value;
	private String ideName;
	private String ideVersion;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the ideName
	 */
	public String getIdeName() {
		return ideName;
	}

	/**
	 * @param ideName
	 *            the ideName to set
	 */
	public void setIdeName(String ideName) {
		this.ideName = ideName;
	}

	/**
	 * @return the ideVersion
	 */
	public String getIdeVersion() {
		return ideVersion;
	}

	/**
	 * @param ideVersion
	 *            the ideVersion to set
	 */
	public void setIdeVersion(String ideVersion) {
		this.ideVersion = ideVersion;
	}

}
