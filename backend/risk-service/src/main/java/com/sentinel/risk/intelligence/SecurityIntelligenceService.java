package com.sentinel.risk.intelligence;

import com.sentinel.risk.domain.entity.EndpointRiskEntity;
import com.sentinel.risk.domain.entity.IpRiskEntity;
import com.sentinel.risk.domain.entity.UserRiskEntity;
import com.sentinel.risk.repository.EndpointRiskRepository;
import com.sentinel.risk.repository.IpRiskRepository;
import com.sentinel.risk.repository.UserRiskRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityIntelligenceService {
    private final UserRiskRepository userRiskRepository;
    private final EndpointRiskRepository endpointRiskRepository;
    private final IpRiskRepository ipRiskRepository;

    public SecurityIntelligenceService(UserRiskRepository userRiskRepository,
                                       EndpointRiskRepository endpointRiskRepository,
                                       IpRiskRepository ipRiskRepository) {
        this.userRiskRepository = userRiskRepository;
        this.endpointRiskRepository = endpointRiskRepository;
        this.ipRiskRepository = ipRiskRepository;
    }

    @Transactional(readOnly = true)
    public List<UserRiskEntity> getTopRiskUsers() {
        return userRiskRepository.findTop10ByOrderByRiskScoreDesc(PageRequest.of(0, 10));
    }

    @Transactional(readOnly = true)
    public List<EndpointRiskEntity> getTopRiskApis() {
        return endpointRiskRepository.findTop10ByOrderByRiskScoreDesc(PageRequest.of(0, 10));
    }

    @Transactional(readOnly = true)
    public List<IpRiskEntity> getTopRiskIps() {
        return ipRiskRepository.findTop10ByOrderByRiskScoreDesc(PageRequest.of(0, 10));
    }
}
