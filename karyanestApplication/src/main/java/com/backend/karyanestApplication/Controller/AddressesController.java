package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.AddressResponseDTO;
import com.backend.karyanestApplication.Model.Addresses;
import com.backend.karyanestApplication.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/addresses")
@Tag(name = "Address Management", description = "APIs for managing addresses and location-based queries")
public class AddressesController {

    @Autowired
    private AddressesService addressesService;

    @Operation(summary = "Get All Addresses", description = "Fetches a list of all addresses.")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('address_getAll')")
    @GetMapping
    public List<Addresses> getAllAddresses() {
        return addressesService.getAllAddresses();
    }

    @Operation(summary = "Get Address by ID", description = "Fetches an address using its unique ID.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('users_getByID')")
    @GetMapping("/{id}")
    public ResponseEntity<Addresses> getAddressById(@PathVariable Long id) {
        Optional<Addresses> address = addressesService.getAddressById(id);
        return address.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Addresses by City", description = "Fetches addresses based on city name.")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('address_city')")
    @GetMapping("/city")
    public List<Addresses> getAddressesByCity(@RequestParam String city) {
        return addressesService.getAddressesByCity(city);
    }

    @Operation(summary = "Get Addresses by Area", description = "Fetches addresses based on area name.")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or  hasAuthority('address_area')")
    @GetMapping("/area")
    public List<Addresses> getAddressesByArea(@RequestParam String area) {
        return addressesService.getAddressesByArea(area);
    }

    @Operation(summary = "Get Addresses by District", description = "Fetches addresses based on district name.")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('address_district')")
    @GetMapping("/district")
    public List<Addresses> getAddressesByDistrict(@RequestParam String district) {
        return addressesService.getAddressesByDistrict(district);
    }

    @Operation(summary = "Get Addresses by State", description = "Fetches addresses based on state name.")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('address_state')")
    @GetMapping("/state")
    public List<Addresses> getAddressesByState(@RequestParam String state) {
        return addressesService.getAddressesByState(state);
    }

    @Operation(summary = "Get Addresses by Pincode", description = "Fetches addresses based on pincode.")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('address_pincode')")
    @GetMapping("/pincode")
    public List<Addresses> getAddressesByPincode(@RequestParam String pincode) {
        return addressesService.getAddressesByPincode(pincode);
    }

    @Operation(summary = "Create a New Address", description = "Creates and saves a new address.")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('address_create')")
    @PostMapping
    public Addresses createAddress(@RequestBody Addresses address) {
        return addressesService.saveAddress(address);
    }

    @Operation(summary = "Update Address", description = "Updates an existing address based on ID.")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('address_update')")
    @PutMapping("/{id}")
    public ResponseEntity<Addresses> updateAddress(@PathVariable Long id, @RequestBody Addresses newAddress) {
        Optional<Addresses> updatedAddress = addressesService.updateAddress(id, newAddress);
        return updatedAddress.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete Address", description = "Deletes an address based on ID.")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('address_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressesService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search Addresses by Keyword", description = "Fetches addresses based on a search keyword.")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('addresses_search')")
    @GetMapping("/search")
    public ResponseEntity<List<AddressResponseDTO>> searchAddressesByKeyword(@RequestParam String keyword) {
        List<AddressResponseDTO> addresses = addressesService.getSearchAddressesByKeyword(keyword);
        return ResponseEntity.ok(addresses);
    }
}
