package com.example.addressmodule.Repositery;




import com.example.addressmodule.Model.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

