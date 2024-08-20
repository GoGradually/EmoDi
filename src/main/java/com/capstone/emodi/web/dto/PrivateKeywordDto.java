package com.capstone.emodi.web.dto;

import com.capstone.emodi.domain.PrivateKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class PrivateKeywordDto {

    private String keywordTag;
    public PrivateKeywordDto(PrivateKeyword keyword) {
        this.keywordTag = keyword.getKeywordTag();
    }
}
