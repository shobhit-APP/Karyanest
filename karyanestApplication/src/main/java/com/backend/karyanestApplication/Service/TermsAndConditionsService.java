package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.TermsAndConditionsRequestDTO;
import com.backend.karyanestApplication.DTO.TermsAndConditionsResponseDTO;
import com.backend.karyanestApplication.Model.TermsAndConditions;
import com.backend.karyanestApplication.Repositry.TermsAndConditionsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermsAndConditionsService {

    private final TermsAndConditionsRepository repository;

    @Transactional
    public TermsAndConditionsResponseDTO createTerms(TermsAndConditionsRequestDTO request) {
        TermsAndConditions terms = new TermsAndConditions();
        terms.setTitle(request.getTitle());
        terms.setContent(request.getContent());
        terms.setVersion(request.getVersion());
        terms.setStatus(TermsAndConditions.Status.valueOf(request.getStatus().toUpperCase()));
        TermsAndConditions savedTerms = repository.save(terms);
        return MapToDTO(savedTerms);
    }

    @Transactional(readOnly = true)
    public List<TermsAndConditionsResponseDTO> getAllTerms() {
        return repository.findAll().stream().map(this::MapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public TermsAndConditionsResponseDTO updateTerms(Long id, TermsAndConditionsRequestDTO request) {
        TermsAndConditions terms = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Terms and Conditions not found"));
        if (request.getTitle() != null) {
            terms.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            terms.setContent(request.getContent());
        }
        if (request.getVersion() != null) {
            terms.setVersion(request.getVersion());
        }
        if (request.getStatus() != null) {
            terms.setStatus(TermsAndConditions.Status.valueOf(request.getStatus().toUpperCase()));
        }

        TermsAndConditions updatedTerms = repository.save(terms);
        return MapToDTO(updatedTerms);
    }

    @Transactional
    public void deleteTerms(Long id) {
        repository.deleteById(id);
    }

    private TermsAndConditionsResponseDTO MapToDTO(TermsAndConditions terms) {
        return new TermsAndConditionsResponseDTO(
                terms.getId(),
                terms.getTitle(),
                terms.getContent(),
                terms.getVersion(),
                terms.getStatus().name(),
                terms.getCreatedAt(),
                terms.getUpdatedAt()
        );
    }
}
