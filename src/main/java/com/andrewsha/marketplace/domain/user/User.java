package com.andrewsha.marketplace.domain.user;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import com.andrewsha.marketplace.config.Config;
import com.andrewsha.marketplace.domain.user.authority.Authority;
import com.andrewsha.marketplace.domain.user.authority.AuthoritySet;
import com.andrewsha.marketplace.domain.user.authority.Role;
import com.andrewsha.marketplace.domain.user.authority.RoleEnum;
import com.andrewsha.marketplace.security.authority.CustomGrantedAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users",
		uniqueConstraints = {@UniqueConstraint(columnNames = "email"),
				@UniqueConstraint(columnNames = "phone_number")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements UserDetails {
	private static final long serialVersionUID = 4206343094785042075L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Pattern(regexp = Config.nameRegexp, message = "wrong name format")
	@Column(name = "name", updatable = true, nullable = true)
	private String name;

	@Email(message = "cannot resolve to email address")
	@Column(name = "email", updatable = true)
	private String email;

	@Pattern(regexp = Config.phoneNumberRegexp, message = "cannot resolve to phone number")
	@Column(name = "phone_number", updatable = true)
	private String phoneNumber;

	@JsonIgnore
	private String password;

	@Pattern(regexp = Config.imageUrlRegexp, message = "cannot resolve to image url")
	@Column(name = "profile_icon", nullable = true)
	private String profileIcon;

	@Column(name = "dob", nullable = true, updatable = false)
	private LocalDate dob;

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonIgnore
	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
	private Set<AuthoritySet> authoritySets = new HashSet<>();

	@Column(name = "account_expired", nullable = true, updatable = true)
	private LocalDate accountExpired;

	@Column(name = "account_locked", nullable = true, updatable = true)
	private LocalDate accountLocked;

	@Column(name = "credentials_expired", nullable = true, updatable = true)
	private LocalDate credentialsExpired;

	// @Column(name = "disabled", nullable = true, updatable = true)
	// private LocalDate disabled;

	@AssertTrue(message = "email or phone number is required")
	private boolean isEmailOrPhoneNumberExists() {
		return this.email != null || this.phoneNumber != null;
	}

	@AssertTrue(message = "age must be greater than 14 and less than 110")
	private boolean isDateOfBirthValid() {
		long age = this.dob.until(LocalDate.now(), ChronoUnit.YEARS);
		if (age < 14 || age > 100) {
			return false;
		}
		return true;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<CustomGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : this.roles) {
			authorities.add(new CustomGrantedAuthority(role.getName()));
		}
		for (AuthoritySet authoritySet : this.authoritySets) {
			authorities.addAll(authoritySet.getAuthorities());
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	@JsonIgnore
	public String getUsername() {
		if (this.email != null) {
			return this.email;
		}
		if (this.phoneNumber != null) {
			return this.phoneNumber;
		}
		return null;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		if (this.accountExpired != null && this.accountExpired.isBefore(LocalDate.now())) {
			return false;
		}
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		if (this.accountLocked != null && this.accountLocked.isBefore(LocalDate.now())) {
			return false;
		}
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		if (this.credentialsExpired != null && this.credentialsExpired.isBefore(LocalDate.now())) {
			return false;
		}
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		// if(this.disabled != null && this.disabled.isBefore(LocalDate.now())) {
		// return false;
		// }
		return true;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getProfileIcon() {
		return profileIcon;
	}

	public void setProfileIcon(String profileIcon) {
		this.profileIcon = profileIcon;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	@JsonIgnore
	public Integer getAge() {
		return (int) LocalDate.now().until(this.dob, ChronoUnit.YEARS);
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addAuthority(Object authority) {
		if (authority instanceof Role) {
			((Role) authority).getUsers().add(this);
			this.roles.add((Role) authority);
		}
		if (authority instanceof Authority) {
			// TODO complete
			// ((Permission) authority)
		}
	}

	public boolean hasRole(RoleEnum roleEnum) {
		for (Role role : this.roles) {
			if (role.getName().equals(roleEnum)) {
				return true;
			}
		}
		return false;
	}

	public void addAuthoritySet(AuthoritySet set) {
		this.authoritySets.add(set);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public LocalDate getAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(LocalDate accountExpired) {
		this.accountExpired = accountExpired;
	}

	public LocalDate getAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(LocalDate accountLocked) {
		this.accountLocked = accountLocked;
	}

	public LocalDate getCredentialsExpired() {
		return credentialsExpired;
	}

	public void setCredentialsExpired(LocalDate credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public Set<AuthoritySet> getAuthoritySets() {
		return authoritySets;
	}

	public void setAuthoritySets(Set<AuthoritySet> authoritySets) {
		this.authoritySets = authoritySets;
	}

}
