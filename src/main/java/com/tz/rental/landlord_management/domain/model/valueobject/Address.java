package com.tz.rental.landlord_management.domain.model.valueobject;

import com.tz.rental.landlord_management.common.constant.ApplicationConstants;
import com.tz.rental.landlord_management.common.util.ValidationUtil;
import lombok.Getter;

@Getter
public class Address {
    private final String streetAddress;
    private final String ward;
    private final String district;
    private final String region;
    private final String country;
    private final String postalCode;

    public Address(String streetAddress, String district, String region) {
        this(streetAddress, null, district, region, ApplicationConstants.DEFAULT_COUNTRY, null);
    }

    public Address(String streetAddress, String ward, String district, String region) {
        this(streetAddress, ward, district, region, ApplicationConstants.DEFAULT_COUNTRY, null);
    }

    public Address(String streetAddress, String ward, String district,
                   String region, String country, String postalCode) {
        this.streetAddress = streetAddress != null ? streetAddress.trim() : null;
        this.ward = ward != null ? ward.trim() : null;
        this.district = district != null ? district.trim() : null;
        this.region = region != null ? region.trim() : null;
        this.country = country != null ? country.trim() : ApplicationConstants.DEFAULT_COUNTRY;
        this.postalCode = postalCode != null ? postalCode.trim() : null;
        validate();
    }

    private void validate() {
        if (ValidationUtil.isNullOrEmpty(streetAddress)) {
            throw new IllegalArgumentException("Street address is required");
        }
        if (ValidationUtil.isNullOrEmpty(district)) {
            throw new IllegalArgumentException("District is required");
        }
        if (ValidationUtil.isNullOrEmpty(region)) {
            throw new IllegalArgumentException("Region is required");
        }
        if (!ValidationUtil.isValidTanzanianRegion(region)) {
            throw new IllegalArgumentException("Invalid Tanzanian region: " + region);
        }
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress);
        if (ward != null && !ward.isEmpty()) {
            sb.append(", ").append(ward);
        }
        sb.append(", ").append(district)
                .append(", ").append(region)
                .append(", ").append(country);
        return sb.toString();
    }

    public boolean isInDarEsSalaam() {
        return "Dar es Salaam".equalsIgnoreCase(region);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return getFullAddress().equals(address.getFullAddress());
    }

    @Override
    public int hashCode() {
        return getFullAddress().hashCode();
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}