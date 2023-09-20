SELECT o.CID
FROM Courses o
WHERE o.CID IN (
    SELECT C.CID
    FROM Courses C
    MINUS
    SELECT E.CID
    FROM Enrollments E
    JOIN Students S ON E.SID = S.SID
    WHERE (S.Major <> 'CS' OR S.Major IS NULL)
    GROUP BY E.CID
    HAVING COUNT(*) >= 10
)
ORDER BY o.CID DESC;

