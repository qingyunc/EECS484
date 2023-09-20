--select IDs of all authors who have written exactly1 book in ascending order
SELECT DISTINCT Authors.Author_ID
FROM Authors INNER JOIN Books ON Authors.Author_ID = Books.Author_ID
GROUP BY Authors.Author_ID HAVING COUNT(Books.Book_ID) = 1 ORDER BY Authors.Author_ID ASC;