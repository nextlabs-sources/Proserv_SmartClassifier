CREATE VIEW [dbo].[View_Failure_By_Type]
AS
/****** Script for SelectTopNRows command from SSMS  ******/
SELECT [FILE_TYPE],
      COUNT(*) as total_failed_file
  FROM [DOCUMENT_RECORDS]
  and [ERROR_MESSAGE] is not null
  GROUP BY [FILE_TYPE]
GO