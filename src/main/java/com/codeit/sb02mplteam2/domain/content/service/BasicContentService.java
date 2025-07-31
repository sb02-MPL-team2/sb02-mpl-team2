package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.binary.repository.BinaryContentRepository;
import com.codeit.sb02mplteam2.domain.content.dto.ContentRequestDto;
import com.codeit.sb02mplteam2.domain.content.dto.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.mapper.ContentMapper;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicContentService implements ContentService{

  private final ContentRepository contentRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ContentMapper contentMapper;

  @Override
  @Transactional(readOnly = true)
  public ContentResponseDto findById(Long id) {
    Content content = contentRepository.findById(id)
        .orElseThrow(() -> new MplException(ErrorCode.CONTENT_NOT_FOUND));
    return contentMapper.toDto(content);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentResponseDto> findAll() {
    return contentRepository.findAll().stream()
        .map(contentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentResponseDto> findByCategory(String category) {
    return contentRepository.findByCategory(category).stream()
        .map(contentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!contentRepository.existsById(id)) {
      throw new MplException(ErrorCode.CONTENT_NOT_FOUND);
    }
    contentRepository.deleteById(id);
  }

  @Override
  @Transactional
  public void saveContent(ContentRequestDto dto) {
    BinaryContent binaryContent = binaryContentRepository.findById(dto.binaryContentId())
        .orElseThrow(() -> new MplException(ErrorCode.BINARY_CONTENT_NOT_FOUND));

    Content content = contentMapper.toEntity(dto, binaryContent);
    contentRepository.save(content);
  }
}
