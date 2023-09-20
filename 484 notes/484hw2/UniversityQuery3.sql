SELECT
    DISTINCT e.SID
FROM
    Enrollments e,
    Courses c,
    Enrollments e2,
    Courses c2,
    Enrollments e3,
    Courses c3
WHERE
    e.CID != e2.CID
    AND c.C_Name = 'EECS442'
    AND c2.C_Name = 'EECS445'
    AND c3.C_Name = 'EECS492'
    AND e.SID = e2.SID
    AND e.SID = e3.SID
    AND e.CID != e3.CID
    AND e2.CID != e3.CID
    AND c.CID = e.CID
    AND c2.CID = e2.CID
    AND c3.CID = e3.CID
UNION
SELECT
    DISTINCT e.SID
FROM
    Enrollments e,
    Courses c,
    Enrollments e2,
    Courses c2
WHERE
    e.CID != e2.CID
    AND c.C_Name = 'EECS482'
    AND c2.C_Name = 'EECS486'
    AND c.CID = e.CID
    AND c2.CID = e2.CID
    AND e.SID = e2.SID
UNION
SELECT
    DISTINCT Enrollments.SID
FROM
    Enrollments,
    Courses
WHERE
    Enrollments.CID = Courses.CID
    AND Courses.C_Name = 'EECS281'
ORDER BY
    SID;