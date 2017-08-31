///server.js

/*
        Setting up required packages
*/
var express = require("express");               //Framework to make server programming easier
var app = express();                                    //"app" now holds an instance of express
var mysql = require('mysql');                   //node-mysql package to interface with a mysql database
var bodyParser = require("body-parser");//body-parser is a middleware that assists in parsing the bodies of HTTP requests (great for POST requests)

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
        extended: true
}));

/*
        Setting up the database connection
*/

var connection = mysql.createConnection({
        host: "restroomsdb.cscqn08r9agg.us-east-2.rds.amazonaws.com",
        user: "csc495",
        password: "imtotesgonnagraduate",
        port: "3306",
        database: "Restroomsdb"
});
connection.connect();


/*
        RESTful routes

*/
app.get("/", function (request, response) {
        var data = [];
        connection.query("SELECT * FROM restrooms", function (error, results, fields) {
                if (error) throw error;

                console.log(JSON.stringify(results));
                return response.send(JSON.stringify(results));
        });
});

app.get("/restroom/:id([0-9]+)", function(request, response) {
        var id = request.params.id; //Thanks to bodyParser, I'm able to pull URL parameters like this

        if (!id) {
                return response.status(400).send({error: true, message: "No ID found. Please provide the ID of the restroom you're looking for."});
        }

        connection.query("SELECT * FROM restrooms WHERE id=?", id, function(error, results, fields) {

                var found = results.length >0;

                if (error) {
                        throw error;
                }
                else if (!found) {
                        return response.status(400).send({error:true, message: "Unable to find a restroom associated with that ID."});
                }
                else {
                        return response.send({error: false, data: results[0], message: "Restroom info for Restroom #: " + id});
                }
        });
});

app.get("/comments/:id([0-9]+)", function (request, response) {
        var data = [];
        var id = request.params.id;
        connection.query("SELECT * FROM restroom_comments WHERE restroom_id=?", id, function(error, results, fields) {
                var found = results.length > 0;

                if (error) {
                        throw error;
                }

                console.log(JSON.stringify(results));
                return response.send(JSON.stringify(results));        
        });
});

app.post("/comments", function (request, response) {
        var refID = request.body.refID;
        var comment = request.body.comment;

        connection.query("INSERT INTO restroom_comments(restroom_id, comment) VALUES (?, ?)", [refID, comment], function (error, results, fields) {
                if (error) {
                        throw error;
                }
                return response.send({error: false, data: results, message: "Successfully added comment."});
        });
});

app.post("/restroom", function (request, response) {
        var name = request.body.name;
        var xcoord = request.body.xcoord;
        var ycoord = request.body.ycoord;
        var cleanRating = request.body.cleanRating;
        var keyReq = request.body.keyReq;
        var custReq = request.body.custReq;

        if (!name || !xcoord || !ycoord) {
                return response.status(400).send({error: true, message: "Either the name or the coordinates are missing. Please try again with the required information."});
        }

        connection.query("INSERT INTO restrooms(name, xcoord, ycoord, cleanRating, keyReq, custReq) VALUES (?,?,?,?,?,?)", [name, xcoord, ycoord, cleanRating, keyReq, custReq], function (error, results, fields) {
                if (error) throw error;
                return response.send({error: false, data: results, message: "Successfully added restroom."});
        });
});

app.put("/restroom/:id([0-9]+)", function (request, response) {
        var id = request.params.id;
        var rating = request.body.rating;

        connection.query("UPDATE restrooms SET cleanRating = ? WHERE id = ?", [rating, id], function (error, results, fields) {
                if (error) throw error;

                return response.status(200);
        });
});

app.delete("/restroom/:id([0-9]+)", function(request, response) {
        var id = request.params.id;

        if (!id) {
                return response.status(400).send({error: true, message: "No ID found. Please provide the ID of the restroom you're trying to delete."});
        }

        connection.query("DELETE FROM restrooms WHERE id=?", id, function(error, results, fields) {
                if (error) throw error;
                return response.send({error:false, data: results, message: "Successfully deleted Restroom #: " + id});
        });
});

app.listen(8080, function() {
        console.log("Server is now listening on port 8080");
});

module.exports = app;