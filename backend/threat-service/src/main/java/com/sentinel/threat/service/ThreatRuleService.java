package com.sentinel.threat.service;

import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.dto.CreateRuleRequest;
import com.sentinel.threat.dto.ThreatRuleDto;
import com.sentinel.threat.dto.UpdateRuleRequest;
import com.sentinel.threat.mapper.ThreatMapper;
import com.sentinel.threat.repository.ThreatRuleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreatRuleService {
    private final ThreatRuleRepository ruleRepository;
    private final ThreatMapper mapper;

    public ThreatRuleService(ThreatRuleRepository ruleRepository, ThreatMapper mapper) {
        this.ruleRepository = ruleRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ThreatRuleDto> findAll() {
        return ruleRepository.findAll().stream().map(mapper::toThreatRuleDto).toList();
    }

    @Transactional(readOnly = true)
    public ThreatRuleDto get(UUID id) {
        return mapper.toThreatRuleDto(findRule(id));
    }

    @Transactional
    public ThreatRuleDto create(CreateRuleRequest request) {
        if (ruleRepository.existsByRuleCode(request.ruleCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Rule code already exists");
        }
        ThreatRuleEntity entity = new ThreatRuleEntity(
                request.ruleCode(),
                request.name(),
                request.description(),
                request.threatType(),
                request.severity(),
                request.enabled(),
                request.threshold(),
                request.recommendation()
        );
        return mapper.toThreatRuleDto(ruleRepository.save(entity));
    }

    @Transactional
    public ThreatRuleDto update(UUID id, UpdateRuleRequest request) {
        ThreatRuleEntity entity = findRule(id);
        entity.update(
                request.name(),
                request.description(),
                request.severity(),
                request.enabled(),
                request.threshold(),
                request.recommendation()
        );
        return mapper.toThreatRuleDto(entity);
    }

    public ThreatRuleEntity findRule(UUID id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Threat rule not found"));
    }
}
