package practica1.mockup_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practica1.mockup_api.entities.MockEndpoint;
import practica1.mockup_api.entities.Project;
import practica1.mockup_api.repositories.MockEndpointRepository;
import practica1.mockup_api.repositories.ProjectRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MockService {

    private final MockEndpointRepository mockRepository;
    private final ProjectRepository projectRepository;

    public List<MockEndpoint> findByProject(Long projectId) {
        return mockRepository.findByProjectId(projectId);
    }

    public MockEndpoint findById(Long id) {
        return mockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mock no encontrado"));
    }

    public MockEndpoint createMock(Long projectId, MockEndpoint mock, String expirationType) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        mock.setProject(project);

        // Tiempo de expiración
        LocalDateTime now = LocalDateTime.now();
        switch (expirationType) {
            case "1_HOUR" -> mock.setExpirationDate(now.plusHours(1));
            case "1_DAY" -> mock.setExpirationDate(now.plusDays(1));
            case "1_WEEK" -> mock.setExpirationDate(now.plusWeeks(1));
            case "1_MONTH" -> mock.setExpirationDate(now.plusMonths(1));
            case "1_YEAR" -> mock.setExpirationDate(now.plusYears(1)); // Defecto
            default -> mock.setExpirationDate(now.plusYears(1));
        }

        return mockRepository.save(mock);
    }

    public void deleteMock(Long id) {
        mockRepository.deleteById(id);
    }
}