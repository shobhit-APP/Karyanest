package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.InquiryRequestDTO;
import com.backend.karyanestApplication.DTO.InquiryResponseDTO;
import com.backend.karyanestApplication.Model.Inquiry;
import com.backend.karyanestApplication.Model.Property;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Repositry.InquiryRepository;
import com.backend.karyanestApplication.Repositry.PropertyRepository;
import com.backend.karyanestApplication.Repositry.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepo userRepo;
    private final PropertyRepository propertyRepository;

    @Transactional
    public InquiryResponseDTO createInquiry(InquiryRequestDTO requestDTO) {
        User user = userRepo.findById(requestDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Property property = propertyRepository.findById(requestDTO.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .property(property)
                .inquiryMessage(requestDTO.getInquiryMessage())
                .status(Inquiry.InquiryStatus.NEW)
                .build();

        inquiry = inquiryRepository.save(inquiry);
        return mapToDTO(inquiry);
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDTO> getAllInquiries() {
        return inquiryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public InquiryResponseDTO getInquiryById(Long id) {
//        Inquiry inquiry = inquiryRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
//        return mapToDTO(inquiry);
//    }
//
//    @Transactional
//    public InquiryResponseDTO updateInquiryStatus(Long id, Inquiry.InquiryStatus status) {
//        Inquiry inquiry = inquiryRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
//        inquiry.setStatus(status);
//        inquiry = inquiryRepository.save(inquiry);
//        return mapToDTO(inquiry);
//    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDTO> getInquiriesByUserId(Long userId) {
        return inquiryRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDTO> getInquiriesByPropertyId(Long propertyId) {
        return inquiryRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteInquiry(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new EntityNotFoundException("Inquiry not found");
        }
        inquiryRepository.deleteById(id);
    }

    private InquiryResponseDTO mapToDTO(Inquiry inquiry) {
        return InquiryResponseDTO.builder()
                .id(inquiry.getId())
                .userId(inquiry.getUser().getId())
                .propertyId(inquiry.getProperty().getId())
                .inquiryMessage(inquiry.getInquiryMessage())
                .status(inquiry.getStatus())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}
