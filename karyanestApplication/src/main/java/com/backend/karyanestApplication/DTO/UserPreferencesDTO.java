package com.backend.karyanestApplication.DTO;

import lombok.Data;
import java.util.Map;

@Data
public class UserPreferencesDTO {
    private Map<String, Object> preferences;
}
