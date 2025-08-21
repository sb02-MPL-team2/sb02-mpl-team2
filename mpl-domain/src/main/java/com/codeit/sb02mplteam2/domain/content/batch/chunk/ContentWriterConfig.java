package com.codeit.sb02mplteam2.domain.content.batch.chunk;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRow;
import javax.sql.DataSource;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentWriterConfig {

  @Bean
  public JdbcBatchItemWriter<ContentRow> contentUpsertWriter(DataSource ds) {
    JdbcBatchItemWriter<ContentRow> w = new JdbcBatchItemWriter<>();
    w.setDataSource(ds);
    w.setSql("""
            INSERT INTO contents
              (provider, external_id, title, description, category, image_url, runtime)
            VALUES
              (:provider, :externalId, :title, :description, :category, :imageUrl, :runtime)
            ON CONFLICT (provider, external_id) DO UPDATE
            SET title       = EXCLUDED.title,
                description = EXCLUDED.description,
                category    = EXCLUDED.category,
                image_url   = EXCLUDED.imageUrl,
                runtime     = EXCLUDED.runtime
            """);
    w.setItemSqlParameterSourceProvider(
        new BeanPropertyItemSqlParameterSourceProvider<>());
    return w;
  }
}