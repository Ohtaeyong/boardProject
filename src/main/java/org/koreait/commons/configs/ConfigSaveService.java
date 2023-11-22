package org.koreait.commons.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.koreait.entities.Configs;
import org.koreait.repositories.ConfigsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // DB에 연동을 해야하므로 의존성주입을 위해
public class ConfigSaveService {

    private final ConfigsRepository repository;

    public <T> void save(String code, T value) { // code와 value인데 뭐가들어올지 모르므로 제네릭메서드형태로

        Configs config = repository.findById(code).orElseGet(Configs::new); // Optional클래스를 이용하면 null값처리 유용

        // 자바객체를 JSON으로 변환
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule()); // java.time패키지에 있는것도 가능

        String json = null;
        try {
            json = om.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        config.setCode(code);
        config.setValue(json);

        repository.saveAndFlush(config);
    }
}