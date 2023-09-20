-- Write a query that finds the titles of books and the sum of pages across all editions of each
-- respective book. The column containing the cumulative total should be named Total_Pages.
-- Only books that have editions listed need to be included in the results, which should be sorted
-- in descending order by the cumulative total number of pages across all editions.
select Title, sum(Pages) as Total_Pages
from Books
inner join Editions on Books.Book_ID = Editions.Book_ID
group by Title having sum(Pages) > 0
order by Total_Pages desc;