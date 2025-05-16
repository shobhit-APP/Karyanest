package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.LeadNote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleNoteDtos {
    private String note;
    private List<String> notes;


}
