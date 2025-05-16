package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.*;
import com.backend.karyanestApplication.Model.FAQ;
import com.backend.karyanestApplication.Repositry.FAQRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FAQService {
    private final FAQRepository faqRepository;

    public FAQResponseDTO createFAQ(FAQRequestDTO request) {
        FAQ faq = new FAQ();
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setCategory(request.getCategory());
        faq.setStatus(request.getStatus() != null ? request.getStatus() : FAQ.Status.active);
        FAQ savedFAQ = faqRepository.save(faq);
        return mapToDTO(savedFAQ);
    }

    public List<FAQResponseDTO> getAllFAQs() {
        return faqRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FAQResponseDTO updateFAQ(Long id, FAQRequestDTO request) {
        FAQ faq = faqRepository.findById(id).orElseThrow(() -> new RuntimeException("FAQ not found"));
        applyFAQUpdates(faq, request);
        FAQ updatedFAQ = faqRepository.save(faq);
        return mapToDTO(updatedFAQ);
    }

    public void deleteFAQ(Long id) {
        faqRepository.deleteById(id);
    }

    private void applyFAQUpdates(FAQ faq, FAQRequestDTO request) {
        if (request.getQuestion() != null) faq.setQuestion(request.getQuestion());
        if (request.getAnswer() != null) faq.setAnswer(request.getAnswer());
        if (request.getCategory() != null) faq.setCategory(request.getCategory());
        if (request.getStatus() != null) faq.setStatus(request.getStatus());
    }

    private FAQResponseDTO mapToDTO(FAQ faq) {
        return new FAQResponseDTO(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCategory(),
                faq.getStatus().name(),
                faq.getCreatedAt(),
                faq.getUpdatedAt()
        );
    }
}
