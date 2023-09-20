-- Write a query that finds the IDs, first names, and last names of all authors who have written at
-- least one book in every subject for which J. K. Rowling has written at least one book, including
-- J. K. Rowling. You may assume there is only one author with a first name ‘J. K.’ and last name
-- ‘Rowling’. The results should be sorted in ascending order by the author’s last name, with ties
-- being broken in favor of the larger ID
select distinct Authors.Author_ID, First_Name, Last_Name from Authors
inner join Books on Authors.Author_ID = Books.Author_ID
inner join Editions on Books.Book_ID = Editions.Book_ID
inner join Subjects on Books.Subject_ID = Subjects.Subject_ID
where Subjects.Subject_ID in (
select distinct Subjects.Subject_ID from Subjects
inner join Books on Books.Subject_ID = Subjects.Subject_ID
inner join Editions on Books.Book_ID = Editions.Book_ID
inner join Authors on Books.Author_ID = Authors.Author_ID
where Authors.First_Name = 'J. K.' and Authors.Last_Name = 'Rowling'
)
group by Authors.Author_ID, First_Name, Last_Name having count(distinct Subjects.Subject_ID) = (
select count(distinct Subject_ID) from (
select distinct Subjects.Subject_ID from Subjects
inner join Books on Books.Subject_ID = Subjects.Subject_ID
inner join Editions on Books.Book_ID = Editions.Book_ID
inner join Authors on Books.Author_ID = Authors.Author_ID
where Authors.First_Name = 'J. K.' and Authors.Last_Name = 'Rowling'
)
)
order by Last_Name asc, Author_ID desc;