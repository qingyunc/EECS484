-- Write a query that finds the first and last names of all authors who have written at least one
-- children’s/young adult book (subject: “Children/YA”) and at least one book of fiction (subject:
-- “Fiction”). The results should be sorted first in ascending order by first name and then in
-- ascending order by last name. Note: if there are multiple authors with the same first and last
-- name, their names should appear multiple times in the result.
SELECT Authors.First_Name, Authors.Last_Name
FROM Authors
WHERE Authors.Author_ID IN
(
    SELECT DISTINCT Authors.Author_ID
    FROM Authors
    INNER JOIN Books ON Books.Author_ID = Authors.Author_ID
    INNER JOIN Subjects ON Subjects.Subject_ID = Books.Subject_ID
    WHERE Subjects.Subject = 'Children/YA'
)
AND Authors.Author_ID IN
(
    SELECT DISTINCT Authors.Author_ID
    FROM Authors
    INNER JOIN Books ON Books.Author_ID = Authors.Author_ID
    INNER JOIN Subjects ON Subjects.Subject_ID = Books.Subject_ID
    WHERE Subjects.Subject = 'Fiction'
)
ORDER BY Authors.First_Name, Authors.Last_Name;