// Query 3
// Create a collection "cities" to store every user that lives in every city. Each document(city) has following schema:
// {
//   _id: city
//   users:[userids]
// }
// Return nothing.

function cities_table(dbname) {
    db = db.getSiblingDB(dbname);

    // TODO: implement cities collection here
    db.createCollection("cities");
    db.users.aggregate([
        {
            $group: {
                _id: "$current.city",
                users: { $push: "$user_id" }
            }
        }
    ]).forEach((doc) => {
        db.cities.insert(doc);
    });
    

    return;
}
