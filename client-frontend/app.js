'use strict';

const express = require('express');
const fs = require('fs');
const path = require('path');

let eventId = '';

run().catch(err => console.log(err));

async function run() {
  const app = express();

  app.get('/events', async function(req, res) {
    console.log('Got /events');
    res.set({
      'Cache-Control': 'no-cache',
      'Content-Type': 'text/event-stream',
      'Connection': 'keep-alive'
    });
    res.flushHeaders();

    // Tell the client to retry every 10 seconds if connectivity is lost
    res.write('retry: 10000\n\n');
    let count = 0;

    while (true) {
      await new Promise(resolve => setTimeout(resolve, 1000));
      doIt(res);
      // Emit an SSE that contains the current 'count' as a string
    }
  });

  app.use(express.static(__dirname + '/public'));
  app.get('/', function(req, res){
      res.sendFile(__dirname + '/index.html');
  });

  app.get('/set/:eventId', function(req, res){
    eventId = req.params.eventId;
    res.sendStatus(200);
});

  app.get('/track.html', function(req, res){
    res.sendFile(__dirname + '/track.html');
});

  app.get('/css/bootstrap.min.css', function(req, res){
    res.sendFile(__dirname + '/css/bootstrap.min.css');
  });

  app.get('/css/offcanvas.css', function(req, res){
    res.sendFile(__dirname + '/css/offcanvas.css');
  });

  app.get('/pics/pizza-logo.svg', function(req, res){
    res.sendFile(__dirname + '/pics/pizza-logo.svg');
  });

  await app.listen(3000);
  console.log('Listening on port 3000');
}



const doIt = (res) => {
    res.write(`data: ${JSON.stringify({name: eventId})}\n\n`);
}