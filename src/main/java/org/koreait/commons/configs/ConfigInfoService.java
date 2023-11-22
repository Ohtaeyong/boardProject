package org.koreait.commons.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.koreait.entities.Configs;
import org.koreait.repositories.ConfigsRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ConfigInfoService { // 설정을 조회

    private final ConfigsRepository repository;

    public <T> T get(String code, Class<T> clazz) { // 단일객체
        return get(code, clazz, null);
    }

    public <T> T get(String code, TypeReference<T> type) { // collection같은
        return get(code, null, type);
    }

    public <T> T get(String code, Class<T> clazz, TypeReference<T> typeReference) { // 단일, 복잡

            Configs config = repository.findById(code).orElse(null); // 데이터를 조회
            if (config == null || StringUtils.hasText(config.getValue())) { // String에있는 문자열가공 편의기능 -> null값 체크
                return null;

            }

            String json = config.getValue();

            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());

        try {
            T data = clazz == null ? om.readValue(json, typeReference) : om.readValue(json, clazz);

            return data;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}