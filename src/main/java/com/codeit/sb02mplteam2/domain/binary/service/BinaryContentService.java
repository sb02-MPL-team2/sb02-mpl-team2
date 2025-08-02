package com.codeit.sb02mplteam2.domain.binary.service;

import com.codeit.sb02mplteam2.domain.binary.dto.BinaryContentDto;
import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {
  BinaryContent create(MultipartFile file);

  BinaryContentDto upload(MultipartFile file) throws IOException;

  List<BinaryContentDto> findAll();
}
