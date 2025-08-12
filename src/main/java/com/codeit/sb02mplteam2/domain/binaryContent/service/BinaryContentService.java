package com.codeit.sb02mplteam2.domain.binaryContent.service;

import com.codeit.sb02mplteam2.domain.binaryContent.dto.BinaryContentDto;
import com.codeit.sb02mplteam2.domain.binaryContent.dto.DownloadFileDto;
import com.codeit.sb02mplteam2.domain.binaryContent.entity.BinaryContent;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {
  BinaryContent upload(MultipartFile file) throws IOException;

  BinaryContentDto find(Long binaryContentId);

  List<BinaryContentDto> findAllByIdIn(List<Long> binaryContentIds);

  void delete(Long binaryContentId);

  DownloadFileDto download(Long binaryContentId);
}
