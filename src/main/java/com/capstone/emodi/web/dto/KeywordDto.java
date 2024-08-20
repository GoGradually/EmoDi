package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.Keyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class KeywordDto {
    private String keywordTag;
    public KeywordDto(Keyword keyword) {
        this.keywordTag = keyword.getKeywordTag();
    }
}
