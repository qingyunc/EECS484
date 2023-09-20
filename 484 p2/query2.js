// Query 2
// Unwind friends and create a collection called 'flat_users' where each document has the following schema:
// {
//   user_id:xxx
//   friends:xxx
// }
// Return nothing.

function unwind_friends(dbname) {
    db = db.getSiblingDB(dbname);

    // TODO: unwind friends
    db.createCollection("flat_users");
    db.users.aggregate([
        {
            $unwind: "$friends"
        },
        {
            $project: {
                _id: 0,
                user_id: "$user_id",
                friends: "$friends"
            }
        }
    ]).forEach((doc) => {
        db.flat_users.insert(doc);
    });

    return;
}
