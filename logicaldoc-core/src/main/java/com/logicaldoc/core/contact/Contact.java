package com.logicaldoc.core.contact;

import com.logicaldoc.core.PersistentObject;

/**
 * A generic contact represented by a set of personal informations.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 6.8
 */
public class Contact extends PersistentObject {

	private static final long serialVersionUID = 1L;

	private Long userId;

	private String firstName;

	private String lastName;

	private String company;

	private String email;

	private String phone;

	private String mobile;

	private String address;

	public Contact() {
	}

	public Contact(Contact source) {
		this.userId = source.userId;
		this.firstName = source.firstName;
		this.lastName = source.lastName;
		this.company = source.company;
		this.email = source.email;
		this.phone = source.phone;
		this.mobile = source.mobile;
		this.address = source.address;

		setTenantId(source.getTenantId());
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFullName() {
		return firstName != null ? firstName : (lastName != null ? " " + lastName : "");
	}

	public String toString() {
		return getFullName() + (email != null ? " - " + email : "");
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}