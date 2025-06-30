package com.backend.karyanestApplication.Interface;

import com.backend.karyanestApplication.DTO.ContactUsRequestDTO;
import com.backend.karyanestApplication.DTO.ContactUsResponseDTO;
import com.example.Authentication.DTO.JWTUserDTO;

import java.util.List;

public interface ContactUsService {
    ContactUsResponseDTO saveContactQuery(ContactUsRequestDTO requestDTO, JWTUserDTO userDTO);

    List<ContactUsResponseDTO> getAllContactQueries();
    List<ContactUsResponseDTO> getQueriesByEmail(String username); // username → email → contact queries
}
