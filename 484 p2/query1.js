// Query 1
// Find users who live in city "city".
// Return an array of user_ids. The order does not matter.

function find_user(city, dbname) {
    db = db.getSiblingDB(dbname);

    let results = [];
    // TODO: find all users who live in city
    // db.users.find(...);
    db.users.find({ "hometown.city": city }).forEach((user) => {
        results.push(user.user_id);
    });

    // See test.js for a partial correctness check.

    return results;
}
