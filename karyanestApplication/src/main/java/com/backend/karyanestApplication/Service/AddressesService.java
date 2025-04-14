package com.backend.karyanestApplication.Service;




import com.backend.karyanestApplication.DTO.AddressResponseDTO;
import com.backend.karyanestApplication.Model.Addresses;
import com.backend.karyanestApplication.Repositry.AddressesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class AddressesService {

    @Autowired
    private AddressesRepository addressesRepository;

    public List<Addresses> getAllAddresses() {
        return addressesRepository.findAll();
    }

    public Optional<Addresses> getAddressById(Long id) {
        return addressesRepository.findById(id);
    }

    public List<Addresses> getAddressesByCity(String city) {
        return addressesRepository.findByCity(city);
    }

    public List<Addresses> getAddressesByArea(String area) {
        return addressesRepository.findByArea(area);
    }

    public List<Addresses> getAddressesByDistrict(String district) {
        return addressesRepository.findByDistrict(district);
    }

    public List<Addresses> getAddressesByState(String state) {
        return addressesRepository.findByState(state);
    }

    public List<Addresses> getAddressesByPincode(String pincode) {
        return addressesRepository.findByPincode(pincode);
    }

    public Addresses saveAddress(Addresses address) {
        return addressesRepository.save(address);
    }

    public Optional<Addresses> updateAddress(Long id, Addresses newAddress) {
        return addressesRepository.findById(id).map(address -> {
            address.setNearbyLandmarks(newAddress.getNearbyLandmarks());
            address.setLocationAddress(newAddress.getLocationAddress());
            address.setCity(newAddress.getCity());
            address.setArea(newAddress.getArea());
            address.setDistrict(newAddress.getDistrict());
            address.setState(newAddress.getState());
            address.setCountry(newAddress.getCountry());
            address.setPincode(newAddress.getPincode());
            address.setLatitude(newAddress.getLatitude());
            address.setLongitude(newAddress.getLongitude());
            return addressesRepository.save(address);
        });
    }

    public void deleteAddress(Long id) {
        addressesRepository.deleteById(id);
    }


    public List<AddressResponseDTO> getSearchAddressesByKeyword(String keyword){
        List<Addresses> addresses = addressesRepository.findByLocationAddressContaining(keyword);
        return addresses.stream().map(address -> new AddressResponseDTO(
                address.getId(),
                address.getNearbyLandmarks(),
                address.getLocationAddress(),
                address.getCity(),
                address.getArea(),
                address.getDistrict(),
                address.getState(),
                address.getCountry(),
                address.getPincode(),
                address.getLatitude(),
                address.getLongitude()
        )).collect(Collectors.toList());
    }
}

