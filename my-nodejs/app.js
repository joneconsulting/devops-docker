var express = require('express');
var app = express();

app.get('/', (req, res) => {
  res.status(200).json({
    message: `Hello, ${req.query.name}!`
  });
});

app.listen(8000, () => {
  console.log(`Example app for CI/CD listening on port 8000`)
})
