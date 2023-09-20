-- Write a query that finds the IDs and names of publishing companies that have published at
-- least one edition of a book written by any author who has written exactly 3 books. The results
-- should be sorted in descending order by the publisher’s ID.
select distinct Publishers.Publisher_ID, Name from Publishers
inner join Editions on Publishers.Publisher_ID = Editions.Publisher_ID
inner join Books on Editions.Book_ID = Books.Book_ID
inner join Authors on Books.Author_ID = Authors.Author_ID
where Authors.Author_ID in(
select Authors.Author_ID from Authors
inner join Books on Authors.Author_ID = Books.Author_ID
group by Authors.Author_ID having count(*) = 3
)
order by Publisher_ID desc;