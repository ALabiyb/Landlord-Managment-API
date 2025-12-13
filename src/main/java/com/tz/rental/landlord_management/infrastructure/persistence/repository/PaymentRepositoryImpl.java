package com.tz.rental.landlord_management.infrastructure.persistence.repository;

import com.tz.rental.landlord_management.domain.model.aggregate.Payment;
import com.tz.rental.landlord_management.domain.repository.PaymentRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.mapper.PaymentMapper;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Payment save(Payment payment) {
        var entity = paymentMapper.toEntity(payment);
        var savedEntity = jpaPaymentRepository.save(entity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaPaymentRepository.findById(id).map(paymentMapper::toDomain);
    }

    @Override
    public List<Payment> findByLeaseId(UUID leaseId) {
        return jpaPaymentRepository.findByLeaseId(leaseId).stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaPaymentRepository.deleteById(id);
    }
}