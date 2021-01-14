var express = require('express');
var bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');
const winston = require('winston');
const { PubSub } = require('@google-cloud/pubsub');

const logger = winston.createLogger({
	level: 'info',
	transports: [
	  new winston.transports.Console(),
	]
  });

var app = express();
app.use(bodyParser.json());

const clients = {}; 

const createSubscription = async (uuid) => {
	const pubSubClient = new PubSub();
	const [subscription] = await pubSubClient
	//.topic(process.env.TOPIC_NAME)
	.topic('projects/techday-pizza-store-demo/topics/pizza-store')
	.createSubscription(`sub_${uuid}`, {
		filter: `attributes.uuid = "${uuid}"`,
	})
	.catch(e => {
		if(e.code === 6) {
			logger.info(`subscription with uuid ${uuid} already exists`);
			return [null];
		} else {
			logger.info(`unexpected error in createSubscription ${e}`);
			throw Error('unexpected error');
		}
	});
	return subscription;
}

const getSubscription = async (uuid) => {
	const pubSubClient = new PubSub();
	const subscription = await pubSubClient
	//.topic(process.env.TOPIC_NAME)
	.topic('projects/techday-pizza-store-demo/topics/pizza-store')
	.subscription(`sub_${uuid}`);
	return subscription;
}

const createPullSubscription = async (uuid) => {
	let subscription = await createSubscription(uuid);
	if(!subscription) {
		subscription = await getSubscription(uuid);
	}
	logger.info(`Subscription with uuid = ${uuid} is ready`);
	subscription.on('message', (message) => messageHandler(uuid, message));
};


const messageHandler = (uuid, message) => {
	let bufferOriginal = Buffer.from(message.data);
	const payload = bufferOriginal.toString('utf8')
	try {
		clients[uuid].write(`data: ${JSON.stringify({attributes: message.attributes, payload})}\n\n`);
		message.ack();
		logger.info(`messsage delivered to ${uuid}, evendId = ${message.attributes.eventId}`);
	} catch(e) {
		message.nack();
		logger.info(`messsage failed to delivered to ${uuid}`);
	}
};

app.get('/events/:uuid', async (req, res) => {
	const clientId = req.params.uuid;
	await createPullSubscription(clientId);
	res.writeHead(200, {
		'Access-Control-Allow-Origin': '*',
		'Content-Type': 'text/event-stream', 
		'Cache-Control': 'no-cache',
		'Connection': 'keep-alive'
	});
	res.write('\n');
	(function () {
		clients[clientId] = res; 
		req.on("close", function () {
			delete clients[clientId]
		});
	})()
});

(async () => {
	logger.info(`new instance started, with uuid : ${uuidv4()}`);
	app.listen(process.env.PORT || 9000);
})().catch(err => {
	console.error(err);
});


