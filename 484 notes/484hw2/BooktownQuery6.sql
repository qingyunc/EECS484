-- Write a query that finds titles, publication dates, author IDs, author first names, and author
-- last names of ALL editions of books written by authors who have written at least one book
-- with at least one edition published between the dates of 2003-01-01 and 2008-12-31. Both
-- dates should be included in the range. The results should be sorted in ascending order by the
-- author’s ID, then in ascending order by the book title, then in descending order by the date of
-- publication

select distinct Title, Publication_Date, Authors.Author_ID, Authors.First_Name, Authors.Last_Name
from Books 
inner join Editions on Books.Book_ID = Editions.Book_ID
inner join Authors on Books.Author_ID = Authors.Author_ID
where Authors.Author_ID in (select distinct Authors.Author_ID
from Books
inner join Editions on Books.Book_ID = Editions.Book_ID
inner join Authors on Books.Author_ID = Authors.Author_ID
where Publication_Date >= '2003-01-01' and Publication_Date <= '2008-12-31')
order by Author_ID, Title, Publication_Date desc;