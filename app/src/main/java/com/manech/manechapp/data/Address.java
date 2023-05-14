package com.manech.manechapp.data;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.manech.manechapp.helper.Converters;
import com.manech.manechapp.helper.MyUtils;

import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE)
        },
        indices = {@Index("userId")})
public class Address extends ManechEntity {

    private String strAddress;
    private String city;
    private String zipCode;
    private String country;
    private Location location;
    private String phones;
    private String email;
    private UUID userId;

    public Address(String strAddress, String city, String zipCode, String country, Location location,String phones, String email, UUID userId) {
        this.strAddress = strAddress;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.phones = phones;
        this.email = email;
        this.userId = userId;
        this.location=location;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address clone(){
        return new Address(getStrAddress(),getCity(),getZipCode(),getCountry(),getLocation(),
                getPhones(),getEmail(), getUserId());
    }
    @NonNull
    @Override
    public String toString() {
      return getStrAddress()  + ", "+ getCity()+ ", "+ getZipCode() +"\n"
              + getCountry() +"\n"+getEmail();
    }
}
