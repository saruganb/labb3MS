const express = require("express");

const app = express();
const cors = require('cors')

// socket
const server = require("http").createServer(app);
const io = require("socket.io")(server, {
    cors: {
        origin: "*",
    },
});
const mysql = require('mysql')
const fs = require("fs");

//For a common socket io name

let connectionName = ""

//SQL

const con = mysql.createConnection({
    //host:'localhost',
    //host:'mysql-whiteboard-ms',
    host: "api5-mysql",
    port: "8080",
    user: "whiteboardUser",
    password: "passMS",
    database: "whiteboardSM",
    connectTimeout: 40000

})
con.connect(function(err) {
    if (err) throw err;
    console.log("Connected!");

    let createTableWhiteboard = `create table if not exists whiteboard(
                          id INT NOT NULL AUTO_INCREMENT,
                          image TEXT NULL,
                          ownerId TEXT NOT NULL,
                          name VARCHAR(50) NOT NULL,
                          PRIMARY KEY (id)
                    )`;

    con.query(createTableWhiteboard, function(err, results, fields) {
        if (err) {
            console.log(err.message);
        }
    });

    let createTableGroupchat = `create table if not exists groupchat(
                          groupchatId INT NOT NULL AUTO_INCREMENT,
                          id INT NOT NULL,
                          PRIMARY KEY (groupchatId),
                          FOREIGN KEY (id) REFERENCES whiteboard(id)
                    )`;

    con.query(createTableGroupchat, function(err, results, fields) {
        if (err) {
            console.log(err.message);
        }
    });

    let createTableUser = `create table if not exists user(
                        id int NOT NULL,
                        userId VARCHAR (400) NOT NULL,
                        groupchatId int NOT NULL,
                        PRIMARY KEY (id,userId),
                        FOREIGN KEY (id) REFERENCES whiteboard(id),
                        FOREIGN KEY (groupchatId) REFERENCES groupchat(groupchatId)
                    )`;

    con.query(createTableUser, function(err, results, fields) {
        if (err) {
            console.log(err.message);
        }
    });

    let createTableMessages = `create table if not exists messages(
                        messageId int NOT NULL AUTO_INCREMENT,
                        groupchatId int NOT NULL,
                        body VARCHAR(200) NOT NULL,
                        senderId TEXT NOT NULL,
                        PRIMARY KEY (messageId),
                        FOREIGN KEY (groupchatId) REFERENCES groupchat(groupchatId)
                    )`;

    con.query(createTableMessages, function(err, results, fields) {
        if (err) {
            console.log(err.message);
        }
    });
});

//Keycloak

const keycloak = require('./KeycloakConfig/keycloak-config').initKeycloak();
/*Configure Session*/
const session = require('express-session');
var memoryStore = new session.MemoryStore();
app.use(session({
    secret: 'mySecret',
    resave: false,
    saveUninitialized: true,
    store: memoryStore
}));

app.use(keycloak.middleware());


//socket.io

io.on('connection', onConnection);

//Settings

app.set("view engine", "ejs");
app.set(express.urlencoded({ extended: true }));
app.use(cors({origin: true, credentials: true}))
app.set(express.json());


//REST CALLS

app.get("/get/all/:ownerId",keycloak.protect('realm:user'),(req,res) =>{
    let ownerId = req.params['ownerId']
    con.query("SELECT DISTINCT whiteboard.name, whiteboard.id FROM whiteboard, whiteboardSM.user WHERE ownerId = '" + ownerId + "' OR (userId = '" + ownerId + "' AND user.id = whiteboard.id)",
        function (err,result,fields){
        if(err) throw err;
        //console.log(result)
        res.send(result)
    })
})

app.get("/get/all/joined/:userId",keycloak.protect('realm:user'),(req,res) =>{
    let userId = req.params['userId']
    con.query("SELECT id FROM whiteboardSM.user WHERE userId = " + userId,
        function (err,result,fields){
            if(err) throw err;
            let list = []
            console.log(result)
            for(let i = 0; i < result.length; i++){
                con.query("SELECT whiteboard.name, whiteboard.id FROM whiteboard WHERE ownerId = " + result[i].id,
                    function (err,result,fields){
                        if(err) throw err;
                        //console.log(result)
                        list.push(result)
                    })
            }
            res.send(list)
        })
})

app.post("/whiteboard/join/:userId/:id",keycloak.protect('realm:user'),(req,res) =>{
    let id = req.params['id']
    let userId = req.params['userId']
    
    con.query("SELECT * FROM whiteboard WHERE id = " + id, function (err,result,fields){
        if(err) throw err;
        if(result.length == 0){
            return res.send({message: 'No such id'});
        }
        else{
            con.query("SELECT * FROM whiteboardSM.user WHERE userId ='" + userId + "' AND id =" + id, function(err,result,fields){
                if(err) throw err;
                console.log(userId + " " + id)
                console.log(result.length)
                if(result.length.toFixed() == 1){
                    connectionName = id
                    return res.send({message: 'Already in that room'});
                    
                }
                else{
                    con.query("SELECT groupchatId FROM whiteboardSM.groupchat WHERE id =" + id, function (err,result,fields){
                        if(err) throw err;

                        let groupchatId = result[0].groupchatId

                        const sql =  'INSERT INTO whiteboardSM.user(id,userId,groupchatId) VALUES (?, ?,?)'
                        let values =  [id,userId,groupchatId]
                        con.query(sql, values, function (err, result){
                            if (err) throw err;
                            console.log("Number of records inserted: " + result.affectedRows);
                            connectionName = id
                            res.send("201")
                        })
                    })

                }
            })
        }
    })

})

app.get("/whiteboard/:id",keycloak.protect('realm:user'),(req, res) => {
    let  id = req.params['id'] 
    connectionName = id;
    const dataImagePrefix = `data:image/png;base64,`
    con.query("SELECT image FROM whiteboard WHERE id  = "+ id , function (err, result, fields) {
        if (err) throw err;
        //console.log(result)
        
        img = result.map(result => `${dataImagePrefix}${result.image.toString('base64')}`)
        //console.log(img )
        res.send(img);
    });
});
const multer  = require('multer')
const storage = multer.memoryStorage()
const upload = multer({ storage: storage })

app.post("/save/:id",keycloak.protect('realm:user'),upload.single('image'),(req,res) => {
    let id = req.params['id']
    //console.log(req.body)
    const image = req.body.image.replace('data:image/png;base64,','')
    //image = image.replace('data:image/png;base64,','')
    //console.log(image.image.replace('data:image/png;base64,',''))
    
    const sql =  'UPDATE whiteboard SET image = ? WHERE id = ?'
    let values =  [image, id]
    con.query(sql, values, function (err, result){
        if (err) throw err;
        console.log("Number of records inserted: " + result.affectedRows);
    })
    res.send("201")
})

app.post("/create/:id/:name",keycloak.protect('realm:user'),(req,res) =>{
    let id = req.params['id'] 
    let name = req.params['name']

    con.beginTransaction(function (err){
        if(err){
            return con.rollback(function() {
                throw err;
            });
        }
        const sql =  'INSERT INTO whiteboard(image,ownerId,name) VALUES (?, ?, ?)'
        let values =  ["",id,name]
        con.query(sql, values, function (err, result){
            if (err){
                return con.rollback(function() {
                    throw err;
                });
            }
            console.log(result)
            let insertId = result.insertId
            connectionName = insertId
            console.log("Number of records inserted: " + result.affectedRows);
            //res.send(insertId.toFixed())

            const sqlChat =  'INSERT INTO groupchat(id) VALUES (?)'
            let chatValues = [insertId]
            con.query(sqlChat, chatValues, function (err,result){
                if(err){
                    return con.rollback(function() {
                        throw err;
                    });
                }
                console.log(result)
                console.log("insertId : " +  result.insertId)
                let insertGroupchatId = result.insertId
                console.log("Number of records inserted: " + result.affectedRows);

                const sqlUser = 'INSERT INTO user(id,userId,groupchatId) VALUES (?,?,?)'
                let userValues = [insertId,id,insertGroupchatId]

                con.query(sqlUser,userValues,function (err,result){
                    if(err){
                        return con.rollback(function() {
                            throw err;
                        });
                    }
                    con.commit(function (err) {
                        console.log('Commiting transaction.....');
                        if (err) {
                            return con.rollback(function () {
                                throw err;
                            });
                        }

                        console.log('Transaction Complete.');
                        res.send(insertId.toFixed())
                    });
                })
            })
        })
    })
})
app.get("/get/all/message/:whiteboardId",keycloak.protect('realm:user'), (req,res) => {
    let id = req.params['whiteboardId']

    con.query("SELECT groupchatId FROM whiteboardSM.groupchat WHERE id =" + id, function (err,result,fields) {
        if (err) throw err;
        
        if(result.length == 0){
            return res.send({message: 'No such id'});
        }
        else {
            let groupchatId = result[0].groupchatId 
            con.query("SELECT * FROM whiteboardSM.messages WHERE groupchatId =" + groupchatId, function (err,result,fields){
                if(err) throw err;
                
                res.send(result)
            })
        }
       

    })    
    
})
function insertMessage (data){
    con.query("SELECT groupchatId FROM whiteboardSM.groupchat WHERE id =" + data.whiteboardId, function (err,result,fields){
        if(err) throw err;

        let groupchatId = result[0].groupchatId

        const sql =  'INSERT INTO whiteboardSM.messages(groupchatId,body,senderId) VALUES (?, ?, ?)'
        let values =  [groupchatId, data.body, data.senderId]
        con.query(sql, values, function (err, result){
            if (err) throw err;
            console.log("Number of records inserted: " + result.affectedRows);
        })
    })
    return true;
}
function onConnection(socket) {
    console.log("user online")
    socket.on(connectionName.toString(), (data) =>
        socket.broadcast.emit(connectionName.toString(), data));

    socket.on(connectionName.toString() + "chat", function (data) {
        if (insertMessage(data)) {
            //emits to everyone including the sender, to ensure that it is successfylly sent 
            io.emit(connectionName.toString() + "chat", data)
        }
    })
}

server.listen(2000, () => {
    console.log("server is running");
});

