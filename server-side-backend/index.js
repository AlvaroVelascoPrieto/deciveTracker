const express = require('express')
const mysql = require('mysql')
var upload = require('./multerUtil');
const fs = require("fs")
const app = express()
const port = 80

app.use(express.json())
app.use(express.urlencoded({extended: false}))

// MySQL connection
const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'root',
    database: 'app_db'
});

// For testing connection
connection.connect();

// Routes
app.route('/')

.get((req, res) => {
    console.log(req.query);
    let dni = req.query.dni;
    let contrasena = req.query.contrasena;

    console.log(dni);
    let query_iniciar = "SELECT * FROM `USUARIO` WHERE `dni` = ? AND `contrasena` = ?";

    connection.query(query_iniciar, [dni, contrasena], (err, results, fields) => {
        if (err) {
            console.log("There was an error");
            console.log(err);
            res.json({
                    'code': 500,
                    'message': "There was a server error."
            });
	    console.log('There was a server error.')
        } else {

            if (results.length > 0) {
                res.json({
                    'code': 200,
                    'message': 'Get values with success.',
                    'data': results[0]
                });
		console.log('Log in successful')
            } else {
                res.json({
                    'code': 300,
                    'message': 'There are no users in the database with those values.',
                });
		console.log('There are no users in de database with those values')
            }
        }
    })
})

.post((req, res) => {
  let dni = req.body.dni;
  let contrasena = req.body.contrasena;
  let nombre = req.body.nombre;
  let apellido = req.body.apellido;
  let telefono = req.body.telefono;
  console.log(dni)

  let query_registrarse = "INSERT INTO `USUARIO` VALUES(?, ?, ?, ?, ?)"

  connection.query(query_registrarse, [dni, contrasena, nombre, apellido, telefono], 
    (err, results, fields) => {
        if (err) {
            res.json({
                'code': 300,
                'message': 'There was an error while trying to sign up'
            });
	    console.log('There was an error while trying so sign up')
        } else {
            res.json({
                'code': 200,
                'message': 'Successful sign up'
            });
	    console.log('Succesful sign up')
        }
    });
})

.put((req, res) => {
            let id = req.body.originalId;
            let password = req.body.nPassword;
            let name = req.body.nName;
            let lastName = req.body.nLastName;
            let phone = req.body.nPhone;
	    console.log(id)
	    id = parseInt(id);
	    console.log(id)
	    console.log(req.body)
            let query_actualizar = "UPDATE `USUARIO` SET `dni` = ?, `contrasena` = ?, `nombre` = ?, " +
                    "`apellido` = ?, `telefono` = ? WHERE dni = ?";

            connection.query(query_actualizar, [id, password, name, lastName,
                    phone, id], (err, results, fields) => {
                if (err) {
                    res.json({
                            'code': 300,
                            'message': 'Failed on update'
              })
		console.log('Failed on update')
		console.log(err)
                } else {
                    res.json({
                            'code': 200,
                            'message': 'Successful update'
              })
		console.log('Successful update')
		console.log(results)
                }
            })
        })

app.route('/images/')

.post(upload.single('file'), function(req, res, next){
    res.send({ret_code: '0'});
    console.log('Upload happened')
});

app.route('/public/uploads/:name')
.get(async function (request, result) {
  const name = request.params.name
  let image = await fs.readFileSync("public/uploads/" + name)
  image = Buffer.from(image, "base64")
  console.log('The following IDs profile picture has been served: ' + name)
  result.writeHead(200, {
    "Content-Type": "image/jpg",
    "Content-Length": image.length
  })
  result.end(image)
  return
})

// Starting to listen
app.listen(port, () => {
  console.log('Server listen on port 80...')
})
