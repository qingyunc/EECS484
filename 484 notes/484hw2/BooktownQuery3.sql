--select ISBN of all editions of books written by Agatha Christie in descending order
SELECT DISTINCT ISBN FROM Editions
INNER JOIN Books ON Books.Book_ID = Editions.Book_ID
INNER JOIN Authors ON Authors.Author_ID = Books.Author_ID
WHERE First_Name = 'Agatha' and Last_Name = 'Christie' ORDER BY ISBN DESC;