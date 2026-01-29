package com.uniskills.main.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CreateSkillRequest {
    private String title;
    private String description;
    private String category;
    private String proficiency;
    private List<String> tags;
    private String type;
}
