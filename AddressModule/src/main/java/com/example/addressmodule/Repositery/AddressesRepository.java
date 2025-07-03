package com.example.addressmodule.Repositery;




import com.example.addressmodule.Model.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressesRepository extends JpaRepository<Addresses, Long> {

    List<Addresses> findByCity(String city);

    List<Addresses> findByArea(String area);

    List<Addresses> findByDistrict(String district);

    List<Addresses> findByState(String state);

    List<Addresses> findByPincode(String pincode);

    List<Addresses> findByLocationAddressContaining(String keyword);
    @Query(value = "SELECT * FROM addresses WHERE " +
            "LOWER(location_address) LIKE %:keyword% OR " +
            "LOWER(city) LIKE %:keyword% OR " +
            "LOWER(state) LIKE %:keyword% OR " +
            "LOWER(country) LIKE %:keyword% OR " +
            "LOWER(district) LIKE %:keyword% OR " +
            "LOWER(area) LIKE %:keyword% OR " +
            "LOWER(pincode) LIKE %:keyword% OR " +
            "LOWER(nearby_landmarks) LIKE %:keyword%", nativeQuery = true)
    List<Addresses> searchNative(@Param("keyword") String keyword);

}

