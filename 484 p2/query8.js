// Query 8
// Find the city average friend count per user using MapReduce.

let city_average_friendcount_mapper = function () {
    // TODO: Implement the map function
    emit(this.hometown.city, {friend_count: this.friends.length, count: 1});
};

let city_average_friendcount_reducer = function (key, values) {
    // TODO: Implement the reduce function
    var reducedVal = {
        friend_count: 0,
        count: 0
    };
    for (var i = 0; i < values.length; i++) {
        reducedVal.friend_count += values[i].friend_count;
        reducedVal.count += values[i].count;
    }
    return reducedVal;
};

let city_average_friendcount_finalizer = function (key, reduceVal) {
    // We've implemented a simple forwarding finalize function. This implementation
    // is naive: it just forwards the reduceVal to the output collection.
    // TODO: Feel free to change it if needed.
    return reduceVal.friend_count / reduceVal.count;
};
