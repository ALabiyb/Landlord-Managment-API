package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.ContractTemplateResponse;
import com.tz.rental.landlord_management.application.dto.CreateContractTemplateRequest;
import com.tz.rental.landlord_management.domain.model.aggregate.ContractTemplate;
import com.tz.rental.landlord_management.domain.repository.ContractTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractTemplateService {

    private final ContractTemplateRepository contractTemplateRepository;
    private final ContractTemplateMapper applicationContractTemplateMapper;

    @Transactional
    public ContractTemplateResponse createTemplate(CreateContractTemplateRequest request) {
        if (contractTemplateRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Contract template with this name already exists.");
        }
        ContractTemplate template = ContractTemplate.create(
                request.getName(),
                request.getContent(),
                request.getDescription()
        );
        ContractTemplate savedTemplate = contractTemplateRepository.save(template);
        return applicationContractTemplateMapper.toResponse(savedTemplate);
    }

    @Transactional(readOnly = true)
    public ContractTemplateResponse getTemplate(UUID id) {
        ContractTemplate template = contractTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract template not found."));
        return applicationContractTemplateMapper.toResponse(template);
    }

    @Transactional(readOnly = true)
    public List<ContractTemplateResponse> getAllTemplates() {
        return contractTemplateRepository.findAll().stream()
                .map(applicationContractTemplateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContractTemplateResponse updateTemplate(UUID id, CreateContractTemplateRequest request) {
        ContractTemplate template = contractTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract template not found."));

        // Check for name conflict if name is changed
        if (!template.getName().equals(request.getName()) && contractTemplateRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Another contract template with this name already exists.");
        }

        template.updateDetails(
                request.getName(),
                request.getContent(),
                request.getDescription()
        );
        ContractTemplate updatedTemplate = contractTemplateRepository.save(template);
        return applicationContractTemplateMapper.toResponse(updatedTemplate);
    }

    @Transactional
    public void deleteTemplate(UUID id) {
        contractTemplateRepository.deleteById(id);
    }

    @Transactional
    public ContractTemplateResponse activateTemplate(UUID id) {
        ContractTemplate template = contractTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract template not found."));
        template.activate();
        ContractTemplate activatedTemplate = contractTemplateRepository.save(template);
        return applicationContractTemplateMapper.toResponse(activatedTemplate);
    }

    @Transactional
    public ContractTemplateResponse deactivateTemplate(UUID id) {
        ContractTemplate template = contractTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract template not found."));
        template.deactivate();
        ContractTemplate deactivatedTemplate = contractTemplateRepository.save(template);
        return applicationContractTemplateMapper.toResponse(deactivatedTemplate);
    }
}