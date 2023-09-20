SELECT
    DISTINCT members.SID,
    Students.Name
FROM
    Members members
    JOIN Members members2 ON members.PID = members2.PID
    JOIN (
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
            AND e.SID = e2.SID
            AND e.SID = e3.SID
            AND e.CID != e3.CID
            AND e2.CID != e3.CID
            AND c.CID = e.CID
            AND c2.CID = e2.CID
            AND c3.CID = e3.CID
            AND (
                c.C_Name = 'EECS482'
                OR c.C_Name = 'EECS483'
            )
            AND (
                c2.C_Name = 'EECS484'
                OR c2.C_Name = 'EECS485'
            )
            AND (c3.C_Name = 'EECS280')
    ) result ON members2.SID = result.SID
    JOIN Students on Students.SID = members.SID
WHERE
    members.SID != members2.SID
ORDER BY
    Students.Name DESC;