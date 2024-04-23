const mysql = require('mysql')

	// MySQL connection
	const connection = mysql.createConnection({
	    host: 'localhost',
	    user: 'root',
	    password: 'root',
	    database: 'app_db'
	});

	const registrationToken = [];

	let query_iniciar = "SELECT `token` FROM `USUARIO` WHERE `notificaciones` = ?";
	console.log('Ei');
	connection.query(query_iniciar, ["true"], (err, results, fields) => {
	        if (err) {
	            console.log("There was an error");
	            console.log(err);
	        } else {
		    console.log(results.length);
	            if (results.length > 0) {
			console.log("SUCCESSSS");
			for (let i = 0; i < results.length; i++) {
	    			registrationToken.push(results[i].token);
				console.log(results[i].token);
			}
				var admin = require("firebase-admin");
				var serviceAccount = require("/home/alvarovelasco/firebase-token.json");

				admin.initializeApp({
				  	credential: admin.credential.cert(serviceAccount),
					databaseURL: "https://prismappfcm.firebaseio.com"
				});
				var message = {
					notification: {
					  title: 'Location Tracker',
					  body: 'Check your location stats'
					},
					tokens: registrationToken
				  };
				//Send a message to devices subscribed to the provided topic.
				admin.messaging().sendMulticast(message)
				  .then((response) => {
				    // Response is a message ID string.
				    console.log('Successfully sent message:', response);
				  })
				  .catch((error) => {
				    console.log('Error sending message:', error);
				});
		    }
		}
		});

	console.log(registrationToken);

