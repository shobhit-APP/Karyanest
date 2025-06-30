package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.ContactUsRequestDTO;
import com.backend.karyanestApplication.DTO.ContactUsResponseDTO;
import com.backend.karyanestApplication.Interface.ContactUsService;
import com.backend.karyanestApplication.Model.ContactUs;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Repositry.ContactUsRepo;
import com.backend.karyanestApplication.Repositry.UserRepo;
import com.example.Authentication.DTO.JWTUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactUsServiceImpl implements ContactUsService {

    private final ContactUsRepo contactUsRepository;
    private final UserRepo userRepository;

    @Override
    public ContactUsResponseDTO saveContactQuery(ContactUsRequestDTO requestDTO, JWTUserDTO userDTO) {
        ContactUs contact = new ContactUs();
        contact.setFullName(userDTO.getFullname());
        User user = userRepository.findByUsername(userDTO.getUsername());
        contact.setEmail(user.getEmail());
        contact.setQuery(requestDTO.getQuery());

        // Map string to enum with default fallback
        ContactUs.ContactType typeEnum = mapStringToContactType(requestDTO.getType());
        contact.setType(typeEnum);

        ContactUs saved = contactUsRepository.save(contact);
        return convertToDTO(saved);
    }

    @Override
    public List<ContactUsResponseDTO> getAllContactQueries() {
        return contactUsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContactUsResponseDTO> getQueriesByEmail(String username) {
        User user = userRepository.findByUsername(username);

        return contactUsRepository.findByEmail(user.getEmail()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ContactUs.ContactType mapStringToContactType(String typeStr) {
        try {
            return ContactUs.ContactType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ContactUs.ContactType.OTHER;
        }
    }

    private ContactUsResponseDTO convertToDTO(ContactUs contact) {
        ContactUsResponseDTO dto = new ContactUsResponseDTO();
        dto.setId(contact.getId());
        dto.setFullName(contact.getFullName());
        dto.setEmail(contact.getEmail());
        dto.setType(contact.getType().name()); // return string type
        dto.setQuery(contact.getQuery());
        dto.setCreatedAt(contact.getCreatedAt());
        return dto;
    }
}
