--select 'Subject' of all subjects for which no book has written by any author in ascending order
SELECT DISTINCT Subjects.Subject
FROM Subjects
WHERE Subjects.Subject NOT IN
(
    SELECT DISTINCT Subjects.Subject
    FROM Subjects
    INNER JOIN Books ON Books.Subject_ID = Subjects.Subject_ID
    INNER JOIN Authors ON Authors.Author_ID = Books.Author_ID
)
ORDER BY Subjects.Subject ASC;