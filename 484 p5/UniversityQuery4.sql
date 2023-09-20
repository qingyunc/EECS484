CREATE VIEW StudentPairs AS
SELECT
    DISTINCT e.SID SID1,
    e2.SID SID2
FROM
    Enrollments e,
    Enrollments e2
WHERE
    e.CID = e2.CID
    AND e.SID < e2.SID
MINUS
SELECT
    DISTINCT members.SID SID1,
    members2.SID SID2
FROM
    Members members
    JOIN Members members2 on members.PID = members2.PID
WHERE
    members.SID < members2.SID;