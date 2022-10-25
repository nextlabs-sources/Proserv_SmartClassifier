CREATE VIEW [dbo].[View_Success_Summary]
AS
/****** Script for SelectTopNRows command from SSMS  ******/
SELECT [FILE_TYPE],
	   SUM([EXTRACTION_TIME]) AS total_extraction_time,
       AVG([EXTRACTION_TIME]) as avg_extraction_time,
      SUM([INDEXING_TIME]) as total_indexing_time,
      AVG([INDEXING_TIME]) as avg_indexing_time,
	  SUM([EXTRACTION_TIME] + [INDEXING_TIME]) as total_processing_time,
	  AVG([EXTRACTION_TIME] + [INDEXING_TIME]) as avg_processing_time,
	  SUM([FILE_SIZE]) as total_file_size,
	  AVG([FILE_SIZE]) AS avg_file_size,
      COUNT(*) as file_count
  FROM [DOCUMENT_RECORDS]
  [ERROR_MESSAGE] is null
  GROUP BY [FILE_TYPE]
GO