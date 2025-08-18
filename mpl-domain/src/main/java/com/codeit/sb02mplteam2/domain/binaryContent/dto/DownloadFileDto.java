package com.codeit.sb02mplteam2.domain.binaryContent.dto;

import org.springframework.core.io.Resource;

public record DownloadFileDto(
    Resource resource,
    String fileName,
    String contentType,
    Long size
) {

}
