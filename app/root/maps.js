var SQLITE = require("../sqlite");
var Request = require("ringo/webapp/request").Request;
var FILE = require("fs");
var auth = require("../auth");


var System = Packages.java.lang.System;

var getDb = exports.getDb = function(request) {
    var dataDir;
    
    if (request) {
        dataDir = request.env.servlet.getServletConfig().getInitParameter("GEOEXPLORER_DATA");
    }
    if (!dataDir) {
        dataDir = String(
            System.getProperty("GEOEXPLORER_DATA") || 
            System.getenv("GEOEXPLORER_DATA") || "."
        );           
    }
    var db = FILE.join(dataDir, "mareano.db");

    // set up maps table
    try {
        var connection = SQLITE.open(db);
    } catch (err) {
        // TODO: nicer exception handling - this is hard for the user to find
        throw new Error("Can't open '" + db + "' for writing.  Set GEOEXPLORER_DATA to a writable directory.");
    }
    var statement = connection.createStatement();
    statement.executeUpdate(
        "CREATE TABLE IF NOT EXISTS maps (" + 
            "id INTEGER PRIMARY KEY ASC, config BLOB" +
        ");"
    );
    connection.close();
    return db;
}


var createResponse = function(data, status) {
    if (typeof data !== "string") {
        data = JSON.stringify(data);
    }
    return {
        status: status || 200,
        headers: {
            "Content-Type": "application/json"
        },
        body: [data]
    };
};

var getConfig = function(env) {
    var request = new Request(env);
    var config = request.input.read().decodeToString(request.charset || "utf-8");
    var obj;
    try {
        obj = JSON.parse(config);
    } catch (err) {
        config = null;
    }
    if (typeof obj !== "object" || Array.isArray(obj)) {
        config = null;
    }
    return config;
};

var getId = function(env) {
    var id = env.pathInfo.split("/")[1];
    if (!id) {
        // null means no id
        id = null;
    } else {
        id = Number(id);
        if (isNaN(id)) {
            // false means invalid id
            id = false;
        }
    }
    return id;
};

function isAuthorized(env) {
    return auth.getStatus(new Request(env)) !== 401;
}

var handlers = {
    "GET": function(env) {
        var resp;
        var id = getId(env);
        if (id === null) {
            // retrieve all map identifiers
            resp = createResponse(getMapList(env));
        } else if (id === false) {
            // invalid id
            resp = createResponse({error: "Invalid map id."}, 400);
        } else {
            // retrieve single map config
            resp = createResponse(readMap(id, env));
        }
        return resp;
    },
    "POST": function(env) {
        var resp;
        if (isAuthorized(env)) {
            var id = getId(env);
            if (id !== null) {
                resp = createResponse({error: "Can't POST to map " + id}, 405);
            } else {
                var config = getConfig(env);
                if (!config) {
                    resp = createResponse({error: "Bad map config."}, 400);
                } else {
                    // return the map id
                    resp = createResponse(createMap(config, env));
                }
            }
        } else {
            resp = createResponse({error: "Not authorized"}, 401);
        }
        return resp;
    },
    "PUT": function(env) {
        var resp;
        if (isAuthorized(env)) {
            var id = getId(env);
            if (id === null) {
                resp = createResponse({error: "Can't PUT without map id."}, 405);
            } else if (id === false) {
                resp = createResponse({error: "Invalid map id."}, 400);
            } else {
                // valid map id
                var config = getConfig(env);
                if (!config) {
                    resp = createResponse({error: "Bad map config."}, 400);
                } else {
                    resp = createResponse(updateMap(id, config, env));
                }
            }
        } else {
            resp = createResponse({error: "Not authorized"}, 401);
        }
        return resp;
    },
    "DELETE": function(env) {
        var resp;
        if (isAuthorized(env)) {
            var id = getId(env);
            if (id === null) {
                resp = createResponse({error: "Can't DELETE without map id."}, 405);
            } else if (id === false) {
                resp = createResponse({error: "Invalid map id."}, 400);
            } else {
                resp = createResponse(deleteMap(id, env));
            }
        } else {
            resp = createResponse({error: "Not authorized"}, 401);
        }
        return resp;
    }
};

var getMapList = exports.getMapList = function(request) {
    var connection = SQLITE.open(getDb(request));
    var statement = connection.createStatement();
    var results = statement.executeQuery(
        "SELECT id, config FROM maps;"
    );
    var items = [];
    var config;
    while (results.next()) {
        config = JSON.parse(results.getString("config"));
        items.push({
            id: results.getInt("id"), 
            title: config.about && config.about.title,
            "abstract": config.about && config.about["abstract"],
            created: config.created,
            modified: config.modified
        });
    }
    results.close();
    connection.close();
    // return all items
    return {maps: items};
};

var createMap = exports.createMap = function(config, request) {
    if (typeof config === "string") {
        config = JSON.parse(config);
    }
    // add creation & modified date
    var now = Date.now();
    config.created = now;
    config.modified = now;
    config = JSON.stringify(config);
    var connection = SQLITE.open(getDb(request));
    // store the new map config
    var prep = connection.prepareStatement(
        "INSERT INTO maps (config) VALUES (?);"
    );
    prep.setString(1, config);
    prep.executeUpdate();
    // get the map id
    var statement = connection.createStatement();
    var results = statement.executeQuery("SELECT last_insert_rowid() AS id;");
    results.next();
    var id = Number(results.getInt("id"));
    results.close();
    connection.close();
    // return the map id
    return {id: id};
};

var readMap = exports.readMap = function(id, request) {
    var config;
    var connection = SQLITE.open(getDb(request));
    var prep = connection.prepareStatement(
        "SELECT config FROM maps WHERE id = ?;"
    );
    prep.setInt(1, id);
    var results = prep.executeQuery();
    if (results.next()) {
        // found map by id
        config = JSON.parse(String(results.getString("config")));
    } else {
        // not found
        throw {message: "No map with id " + id, code: 404};
    }
    results.close();
    connection.close();
    return config;
};

var updateMap = exports.updateMap = function(id, config, request) {
    if (typeof config === "string") {
        config = JSON.parse(config);
    }
    // update modified date
    config.modified = Date.now();
    config = JSON.stringify(config);
    var result;
    var connection = SQLITE.open(getDb(request));
    var prep = connection.prepareStatement(
        "UPDATE OR FAIL maps SET config = ? WHERE id = ?;"
    );
    prep.setString(1, config);
    prep.setInt(2, id);
    var rows = prep.executeUpdate();
    connection.close();
    if (!rows) {
        throw {message: "No map with id " + id, code: 404};
    } else {
        result = config;
    }
    return result;
}

var deleteMap = exports.deleteMap = function(id, request) {
    var result;
    var connection = SQLITE.open(getDb(request));
    var prep = connection.prepareStatement(
        "DELETE FROM maps WHERE id = ?;"
    );
    prep.setInt(1, id);
    var rows = prep.executeUpdate();
    connection.close();
    if (!rows) {
        throw {message: "No map with id " + id, code: 404};
    } else {
        result = {id: id};
    }
    return result;
};

exports.app = function(env, pathInfo) {
    // TODO: make it so this is unnecessary
    env.pathInfo = pathInfo || "";
    var resp;
    var method = env.method;
    var handler = handlers[method];
    if (handler) {
        try {
            resp = handler(env);            
        } catch (x) {
            resp = createResponse({error: x.message}, x.code || 500);
        }
    } else {
        resp = createResponse({error: "Not allowed: " + method}, 405);
    }
    return resp;    
};
