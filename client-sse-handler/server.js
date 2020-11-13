var express = require('express');
var bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');
const { PubSub } = require('@google-cloud/pubsub');
const { json } = require('body-parser');

var app = express();
app.use(bodyParser.json());

const clients = {}; 
const uuid = uuidv4();
console.log('SERVER START WITH : UUID', uuid);

app.get('/events/:uuid', function (req, res) {
	res.writeHead(200, {
		'Access-Control-Allow-Origin': '*',
		'Content-Type': 'text/event-stream', 
		'Cache-Control': 'no-cache',
		'Connection': 'keep-alive'
	});
	res.write('\n');
	(function () {
		const clientId = req.params.uuid;
		clients[clientId] = res; 
		clients[clientId].write("data: " + "connected with serverId = " + uuid + " with clientId = " + clientId + "\n\n"); 
		req.on("close", function () {
			delete clients[clientId]
		});
	})()
});

const listenForMessages = async () => {
  try {
	const pubSubClient = new PubSub();
  	const [subscription] = await pubSubClient.topic(process.env.TOPIC_NAME).createSubscription('rand'+Math.random());

	const messageHandler = message => {
		console.log(`message data: ${JSON.stringify(message.data)}`);
		console.log(`message attributes: ${JSON.stringify(message.attributes)}`);
		const jsonData = message.attributes;
		if(jsonData.hasOwnProperty("uuid") && clients.hasOwnProperty(jsonData.uuid)) {
			console.log("found client");
			clients[jsonData.uuid].write(`data: ${JSON.stringify({ name: jsonData.eventId, extraData: jsonData.extraData})}\n\n`); 
		}
		else if(jsonData.uuid === "") {
				//brodcast to managers
				console.log("brodcast !");
				Object.keys(clients).forEach(e => e.write(`data: ${JSON.stringify({ name: jsonData.eventId, extraData: jsonData.extraData})}\n\n`));
		}
		else {
			console.log("skipping!");
		}
		message.ack();
	};
	subscription.on('message', messageHandler);
  } catch(e) {
	  console.log('ERROR');
	  console.log(e);
  }
}


app.listen(process.env.PORT || 9000);
listenForMessages();