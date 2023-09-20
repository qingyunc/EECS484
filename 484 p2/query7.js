// Query 7
// Find the number of users born in each month using MapReduce

let num_month_mapper = function () {
    // TODO: Implement the map function
    emit(this.MOB, 1);
};

let num_month_reducer = function (key, values) {
    // TODO: Implement the reduce function
    return Array.sum(values);
};

let num_month_finalizer = function (key, reduceVal) {
    // We've implemented a simple forwarding finalize function. This implementation
    // is naive: it just forwards the reduceVal to the output collection.
    // TODO: Feel free to change it if needed.
    return reduceVal;
};
